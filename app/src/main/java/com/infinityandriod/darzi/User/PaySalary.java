package com.infinityandriod.darzi.User;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaySalary extends AppCompatActivity {

    private static final String TAG = "Pay Salary";
    private Button button;
    private TextInputLayout description;
    private TextInputLayout date;
    private TextInputLayout amount;
    private FirestoreMethods method;
    ArrayAdapter<String> adapter;
    private CollectionReference collectionReference;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private Map<String, Object> salaryData;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_salary);

        method = new FirestoreMethods();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Back btn hooks
        backBtn = findViewById(R.id.pay_salary_back_button);
        backBtn.setOnClickListener(view -> {
            PaySalary.super.onBackPressed();
            finish();
        });

        // Data hooks
        button = findViewById(R.id.select_staff_button);
        description = findViewById(R.id.pay_salary_description);
        date = findViewById(R.id.pay_salary_date);
        amount = findViewById(R.id.pay_salary_amount);

        method.db.collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Staff")
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
    }

    public void sendSalary(View view) {
        documentReference = collectionReference.document(Objects.requireNonNull(description.getEditText()).getText().toString().toLowerCase().trim());

        salaryData = new HashMap<>();
        salaryData.put("date", Objects.requireNonNull(date.getEditText()).getText().toString().toLowerCase().trim());
        salaryData.put("amount", Objects.requireNonNull(amount.getEditText()).getText().toString().toLowerCase().trim());

        checkDuplicates();
    }

    private void checkDuplicates() {
        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean duplicate = false;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.getId().equals(Objects.requireNonNull(description.getEditText()).getText().toString().toLowerCase().trim())) {
                                description.setError("A customer named "
                                        + Objects.requireNonNull(description.getEditText()).getText().toString().trim()
                                        + " already exists");
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            method.send_data(salaryData, documentReference);
                            backBtn.callOnClick();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void onClick(View w) {
        new AlertDialog.Builder(this)
                .setTitle("Select Designation")
                .setAdapter(adapter, (dialog, which) -> {
                    // TODO: user specific action
                    button.setText(adapter.getItem(which));
                    collectionReference = method.db
                            .collection("Users").document(firebaseUser.getUid())
                            .collection("Staff").document(button.getText().toString().toLowerCase().trim())
                            .collection("Salary Payments");
                    dialog.dismiss();
                }).create().show();
    }
}