package com.infinityandriod.darzi.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ViewCustomer extends Activity {

    private static final String TAG = "Edit Customers";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_layout);

        TextView title = findViewById(R.id.textView_title);
        title.setText("Manage Customers");

        ImageView backBtn = findViewById(R.id.back_pressed);
        backBtn.setOnClickListener(view -> {
            ViewCustomer.super.onBackPressed(); finish();});

        FirestoreMethods method = new FirestoreMethods();

        int[] colors = {
                Color.rgb(20,84,96),
                Color.rgb(209,82,82)
        };

        LinearLayout linearLayout = findViewById(R.id.view_layout);

        // Loading Screen
        ProgressBar progressBar = findViewById(R.id.progressBar);
        // Main content
        ScrollView scrollView = findViewById(R.id.mainContent);

        final CollectionReference collectionReference;
        collectionReference = method.db
                .collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Customers");

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int rnd = new Random().nextInt(colors.length);
                        int past = 4;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            ViewStub stub = new ViewStub(this);
                            stub.setLayoutResource(R.layout.data_card);
                            linearLayout.addView(stub);
                            View inflated = stub.inflate();
                            LinearLayout linearLayout1 = inflated.findViewById(R.id.data_card_linear_layout);
                            while (rnd == past){
                                rnd = new Random().nextInt(colors.length);
                            }
                            linearLayout1.setBackgroundColor(colors[rnd]);
                            past = rnd;
                            TextView name = inflated.findViewById(R.id.full_name);
                            name.setText(CommonMethods.toTitleCase((String) Objects.requireNonNull(document.getData().get("fullName"))));
                            TextView email = inflated.findViewById(R.id.email);
                            email.setText((String) document.getData().get("email"));
                            Button edit = inflated.findViewById(R.id.edit_btn);
                            edit.setOnClickListener(new View.OnClickListener() {
                                final String fullName = (String) document.getData().get("fullName");
                                final String gender = (String) document.getData().get("gender");
                                @Override
                                public void onClick(View v) {
                                    collectionReference
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        assert fullName != null;
                                                        if (document.getId().equals(fullName)) {
                                                            assert gender != null;
                                                            Intent intent;
                                                            if (gender.equals("Male")) {
                                                                intent = new Intent(getApplicationContext(), Mens.class);
                                                            } else {
                                                                intent = new Intent(getApplicationContext(), Women.class);
                                                            }
                                                            intent.putExtra("data", (HashMap<String, Object>) document.getData());
                                                            startActivity(intent);
                                                        }
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            });

                                }
                            });
                            Button delete = inflated.findViewById(R.id.delete_customer);
                            delete.setOnClickListener(new View.OnClickListener() {
                                final String fullName = (String) document.getData().get("fullName");

                                @Override
                                public void onClick(View v) {
                                    collectionReference
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        assert fullName != null;
                                                        if (document.getId().equals(fullName)) {
                                                            collectionReference.document(document.getId())
                                                                    .delete()
                                                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                                                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                                                            recreate();
                                                        }
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            });

                                }
                            });
                            scrollView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }



    @Override
    public void onRestart() {
        super.onRestart();
        super.recreate();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
}
