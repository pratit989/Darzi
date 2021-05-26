package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AddOrder extends AppCompatActivity {

    private static final String TAG = "Add Order";
    private ImageView backBtn;
    private FirebaseUser firebaseUser;
    private CollectionReference collectionReference;
    private FirestoreMethods method;
    private ArrayAdapter<String> adapter;
    private Button customer;
    private DocumentReference documentReference;
    private Map<String, Object> incomeData;
    private TextInputLayout description;
    private TextInputLayout dateReceived;
    private TextInputLayout amount;
    private TextInputLayout paid;
    private TextInputLayout dateToCollect;
    private boolean replaceable = false;
    private HashMap<String, Object> _userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        // Back btn hook
        backBtn = findViewById(R.id.general_back_btn);
        backBtn.setOnClickListener(view -> AddOrder.super.onBackPressed());

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        method = new FirestoreMethods();

        // Get data hooks
        customer = findViewById(R.id.selectCustomer);
        description = findViewById(R.id.add_order_description);
        dateReceived = findViewById(R.id.add_order_date_received);
        amount = findViewById(R.id.add_order_amount);
        paid = findViewById(R.id.add_order_paid);
        dateToCollect = findViewById(R.id.add_order_date_to_collect);

        incomeData = new HashMap<>();

        assert firebaseUser != null;
        collectionReference = method.db
                .collection("Users").document(firebaseUser.getUid())
                .collection("Customers");

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int n = Objects.requireNonNull(task.getResult()).size();
                        int i = 0;
                        String[] arr = new String[n];
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            arr[i++] = CommonMethods.toTitleCase(document.getId());
                            collectionReference = method.db
                                    .collection("Users").document(firebaseUser.getUid())
                                    .collection("Orders");
                        }
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        _userData = (HashMap<String, Object>) getIntent().getSerializableExtra("data");

        try {
            receiveData();
            replaceable = true;
        } catch (Exception ignore) {}
    }

    public void onClick(View w) {
        new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setAdapter(adapter, (dialog, which) -> {
                    // TODO: user specific action
                    customer.setText(adapter.getItem(which));
                    dialog.dismiss();
                }).create().show();
    }

    public void addOrder(View view) {
        documentReference = collectionReference.document(customer.getText().toString().toLowerCase().trim());
        Map<String, Object> data = new HashMap<>();
        data.put("description", Objects.requireNonNull(description.getEditText()).getText().toString().trim().toLowerCase());
        data.put("date received", Objects.requireNonNull(dateReceived.getEditText()).getText().toString().trim().toLowerCase());
        data.put("amount", Objects.requireNonNull(amount.getEditText()).getText().toString().toLowerCase().trim());
        data.put("paid", Objects.requireNonNull(paid.getEditText()).getText().toString().trim().toLowerCase());
        data.put("date to collect", Objects.requireNonNull(dateToCollect.getEditText()).getText().toString().toLowerCase().trim());
        incomeData.put(Objects.requireNonNull(description.getEditText()).getText().toString().toLowerCase().trim(), data);

        if (replaceable) {
            method.send_data(incomeData, documentReference);
            backBtn.callOnClick();
        } else {
            checkDuplicate();
        }
    }

    private void checkDuplicate() {
        collectionReference.document(customer.getText().toString().trim().toLowerCase())
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    boolean duplicate = false;
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Set<String> keys = Objects.requireNonNull(document.getData()).keySet();
                        for (String x : keys) {
                            if (x.equals(Objects.requireNonNull(description.getEditText()).getText().toString().toLowerCase().trim())) {
                                duplicate = true;
                            }
                        }
                        if (duplicate) {
                            description.setError("This income " + description.getEditText().getText().toString().trim() + " already exists");
                        } else {
                            method.send_data(incomeData, documentReference);
                            backBtn.callOnClick();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        method.send_data(incomeData, documentReference);
                        backBtn.callOnClick();
                    }
                });
    }

    private void receiveData() {
        Objects.requireNonNull(customer).setText(CommonMethods.toTitleCase((String) Objects.requireNonNull(_userData.get("customer"))));
        Objects.requireNonNull(description.getEditText()).setText((String) _userData.get("description"));
        Objects.requireNonNull(dateReceived.getEditText()).setText((String) _userData.get("dateReceived"));
        Objects.requireNonNull(dateToCollect.getEditText()).setText((String) _userData.get("dateToCollect"));
        Objects.requireNonNull(paid.getEditText()).setText((String) _userData.get("paid"));
        Objects.requireNonNull(amount.getEditText()).setText((String) _userData.get("amount"));
    }
}