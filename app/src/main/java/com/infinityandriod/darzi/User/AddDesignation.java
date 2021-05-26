package com.infinityandriod.darzi.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AddDesignation extends AppCompatActivity {

    private static final String TAG = "Add Designation";
    private TextInputLayout designation;
    private FirestoreMethods method;
    private Map<String, Object> designations;
    private ImageView backBtn;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_designation);

        designations = new HashMap<>();

        method = new FirestoreMethods();

        //back btn hook
        backBtn = findViewById(R.id.add_staff_designation_back_button);
        backBtn.setOnClickListener(view -> {
            AddDesignation.super.onBackPressed();
            finish();
        });

        //data hook
        designation = findViewById(R.id.add_designation_name);
    }

    public void getDesignation(View view) {
        String designationName = Objects.requireNonNull(designation.getEditText()).getText().toString().trim().toLowerCase();
        designations.put(designationName, designationName);

        documentReference = method.db
                .collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Designations").document("List");

        checkDuplicate();
    }

    private void checkDuplicate() {
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        boolean duplicate = false;
                        assert document != null;
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Set<String> keys = Objects.requireNonNull(document.getData()).keySet();
                            for (String x : keys) {
                                if (x.equals(Objects.requireNonNull(designation.getEditText()).getText().toString().toLowerCase().trim())) {
                                    duplicate = true;
                                }
                            }
                            if (duplicate) {
                                designation.setError("The designation " + designation.getEditText().getText().toString().trim() + " already exists");
                            } else {
                                method.send_data(designations, documentReference);
                                backBtn.callOnClick();
                            }
                        } else {
                            method.send_data(designations, documentReference);
                            backBtn.callOnClick();
                            Log.d(TAG, "No such document");
                        }
                    }
                });
    }
}