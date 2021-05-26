package com.infinityandriod.darzi.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ViewStaff extends Activity {

    private static final String TAG = "Edit Customers";
    ImageView backBtn;
    CollectionReference collectionReference;
    int[] colors;
    FirestoreMethods method;
    LinearLayout linearLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_layout);

        TextView title = findViewById(R.id.textView_title);
        title.setText("Manage Staff");

        backBtn = findViewById(R.id.back_pressed);
        backBtn.setOnClickListener(view -> {
            ViewStaff.super.onBackPressed(); finish();});

        method = new FirestoreMethods();

        colors = new int[]{
                Color.rgb(219,90,107),
                Color.rgb(224,175,31),
                Color.rgb(104,160,176),
                Color.rgb(51,204,90),
        };

        linearLayout = findViewById(R.id.view_layout_new);

        // Loading Screen
        ProgressBar progressBar = findViewById(R.id.progressBar);
        // Main content
        ScrollView scrollView = findViewById(R.id.mainContent);
        collectionReference = method.db
                .collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Staff");

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int past = 4;
                        int rnd = new Random().nextInt(colors.length);
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
                            email.setText((String) document.getData().get("designation"));
                            TextView secondData = inflated.findViewById(R.id.second_data);
                            secondData.setText("Designation:");
                            Button edit = inflated.findViewById(R.id.edit_btn);
                            edit.setText("Edit Staff Details");
                            edit.setOnClickListener(new View.OnClickListener() {
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
                                                            Intent intent;
                                                            intent = new Intent(getApplicationContext(), AddStaff.class);
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
                            delete.setText("Fire Staff");
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

    public void showSearchBar(View view) {
        TextInputLayout search_bar = findViewById(R.id.view_layout_search_bar);
        search_bar.setVisibility(View.VISIBLE);

        ImageView searchBtn = findViewById(R.id.search_btn);

        backBtn.setVisibility(View.GONE);
        searchBtn.setVisibility(View.GONE);

        Objects.requireNonNull(search_bar.getEditText()).addTextChangedListener(

                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        linearLayout.removeAllViewsInLayout();
                        CollectionReference collectionReference = method.db
                                .collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .collection("Staff");

                        collectionReference
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int past = 4;
                                        int rnd = new Random().nextInt(colors.length);
                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            if (Objects.requireNonNull(document.getData().get("fullName")).toString().contains(s.toString().toLowerCase())) {
                                                ViewStub stub = new ViewStub(ViewStaff.this);
                                                stub.setLayoutResource(R.layout.data_card);
                                                linearLayout.addView(stub);
                                                View inflated = stub.inflate();
                                                LinearLayout linearLayout1 = inflated.findViewById(R.id.data_card_linear_layout);
                                                while (rnd == past) {
                                                    rnd = new Random().nextInt(colors.length);
                                                }
                                                linearLayout1.setBackgroundColor(colors[rnd]);
                                                past = rnd;
                                                TextView name = inflated.findViewById(R.id.full_name);
                                                name.setText(CommonMethods.toTitleCase((String) Objects.requireNonNull(document.getData().get("fullName"))));
                                                TextView email = inflated.findViewById(R.id.email);
                                                email.setText((String) document.getData().get("designation"));
                                                TextView secondData = inflated.findViewById(R.id.second_data);
                                                secondData.setText("Designation:");
                                                Button edit = inflated.findViewById(R.id.edit_btn);
                                                edit.setText("Edit Staff Details");
                                                edit.setOnClickListener(new View.OnClickListener() {
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
                                                                                Intent intent;
                                                                                intent = new Intent(getApplicationContext(), AddStaff.class);
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
                                                delete.setText("Fire Staff");
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
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                });
                    }
                });
    }
}
