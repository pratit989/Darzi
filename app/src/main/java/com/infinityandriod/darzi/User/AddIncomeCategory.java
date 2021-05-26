package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.NetworkStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddIncomeCategory extends AppCompatActivity {

    private static final String TAG = "Add Income Category";
    ImageView backBtn;
    TextInputLayout category;
    FirestoreMethods method;
    String categoryName;
    boolean duplicate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_category);

        method = new FirestoreMethods();

        // Back button hooks
        backBtn = findViewById(R.id.add_income_category_back_button);
        backBtn.setOnClickListener(view -> {
            AddIncomeCategory.super.onBackPressed();
            finish();
        });

        // Data hook
        category = findViewById(R.id.add_income_category_name);
    }

    public void getCategory(View view) {
        categoryName = Objects.requireNonNull(category.getEditText()).getText().toString().trim().toLowerCase();
        checkDuplicate();
    }

    private void checkDuplicate() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        CollectionReference collectionReference = method.db
                .collection("Users").document(firebaseUser.getUid())
                .collection("Account").document("Income")
                .collection("Categories");

        Map<String, Object> empty = new HashMap<>();

        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        duplicate = false;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.getId().equals(Objects.requireNonNull(category.getEditText()).getText().toString().toLowerCase().trim())) {
                                duplicate = true;
                                category.setError("The category " + category.getEditText().getText().toString() + " already exists");
                            }
                        }
                        if (!duplicate) {
                            method.send_data(empty, collectionReference.document(categoryName));
                            backBtn.callOnClick();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }




}