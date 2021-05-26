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

public class AddIncome extends AppCompatActivity {

    private static final String TAG = "Add Income";
    private ImageView backBtn;
    private Button category;
    private TextInputLayout description;
    private TextInputLayout date;
    private TextInputLayout amount;
    private FirestoreMethods method;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    ArrayAdapter<String> adapter;
    private Map<String, Object> incomeData;
    private HashMap<String, Object> _userData;
    private boolean replaceable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        replaceable = false;

        method = new FirestoreMethods();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Back btn hook
        backBtn = findViewById(R.id.add_income_back_button);
        backBtn.setOnClickListener(view -> {
            AddIncome.super.onBackPressed();
            finish();
        });

        // Data hooks
        category = findViewById(R.id.select_category_button);
        description = findViewById(R.id.add_income_description);
        date = findViewById(R.id.add_income_date);
        amount = findViewById(R.id.add_income_amount);

        assert firebaseUser != null;
        collectionReference = method.db
                .collection("Users").document(firebaseUser.getUid())
                .collection("Account").document("Income")
                .collection("Categories");

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

    private void receiveData() {
        Objects.requireNonNull(category).setText(CommonMethods.toTitleCase((String) Objects.requireNonNull(_userData.get("category"))));
        Objects.requireNonNull(description.getEditText()).setText((String) _userData.get("description"));
        Objects.requireNonNull(date.getEditText()).setText((String) _userData.get("date"));
        Objects.requireNonNull(amount.getEditText()).setText((String) _userData.get("amount"));
    }

    public void sendIncome(View view) {
        documentReference = collectionReference.document(category.getText().toString().toLowerCase().trim());
        incomeData = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("date", Objects.requireNonNull(date.getEditText()).getText().toString().trim().toLowerCase());
        data.put("amount", Objects.requireNonNull(amount.getEditText()).getText().toString().toLowerCase().trim());
        incomeData.put(Objects.requireNonNull(description.getEditText()).getText().toString().toLowerCase().trim(), data);
        if (replaceable) {
            method.send_data(incomeData, documentReference);
            backBtn.callOnClick();
        } else {
            checkDuplicate();
        }
    }

    private void checkDuplicate() {
        collectionReference.document(category.getText().toString().trim().toLowerCase())
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
                    }
                });
    }

    public void onClick(View w) {
        new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setAdapter(adapter, (dialog, which) -> {
                    // TODO: user specific action
                    category.setText(adapter.getItem(which));
                    dialog.dismiss();
                }).create().show();
    }
}