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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class ViewOrders extends Activity {

    private static final String TAG = "View Income";
    Map<String, Object> dataToSend;
    ImageView backBtn;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    ScrollView scrollView;
    FirestoreMethods method;
    int[] colors;
    CollectionReference collectionReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_layout);

        TextView title = findViewById(R.id.textView_title);
        title.setText("Manage Income");

        backBtn = findViewById(R.id.back_pressed);
        backBtn.setOnClickListener(view -> {
            ViewOrders.super.onBackPressed(); finish();});

        method = new FirestoreMethods();

        colors = new int[]{
                Color.rgb(149,190,71),
                Color.rgb(186,22,12),
                Color.rgb(104,160,176),
                Color.rgb(247,164,94),
                Color.rgb(255,193,7),
        };


        linearLayout = findViewById(R.id.view_layout_new);

        // Loading Screen
        progressBar = findViewById(R.id.progressBar);
        // Main content
        scrollView = findViewById(R.id.mainContent);

        collectionReference = method.db
                .collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Orders");

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int rnd = new Random().nextInt(colors.length);
                        int past = 10;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Set<String> keys = Objects.requireNonNull(document.getData()).keySet();
                            DocumentReference documentReference = collectionReference.document(document.getId());
                            for (String x : keys) {
                                ViewStub stub = new ViewStub(this);
                                stub.setLayoutResource(R.layout.data_card);
                                linearLayout.addView(stub);
                                View inflated = stub.inflate();
                                LinearLayout linearLayout1 = inflated.findViewById(R.id.data_card_linear_layout);
                                while (rnd == past) {
                                    rnd = new Random().nextInt(colors.length);
                                }
                                linearLayout1.setBackgroundColor(colors[rnd]);
                                past = rnd;
                                TextView firstData = inflated.findViewById(R.id.first_data);
                                firstData.setText("Customer: ");
                                TextView name = inflated.findViewById(R.id.full_name);
                                name.setText(CommonMethods.toTitleCase(document.getId()));
                                TextView email = inflated.findViewById(R.id.email);
                                Map<String, Object> descriptorData = (Map<String, Object>) document.getData().get(x);
                                assert descriptorData != null;
                                String symbol = getString(R.string.Rs);
                                email.setText(symbol + " " + Objects.requireNonNull(descriptorData.get("amount")).toString());
                                TextView secondData = inflated.findViewById(R.id.second_data);
                                secondData.setText("Amount: ");
                                Button edit = inflated.findViewById(R.id.edit_btn);
                                edit.setText("Edit Log Details");
                                edit.setOnClickListener(new View.OnClickListener() {
                                    {
                                        dataToSend = new HashMap<>();
                                        dataToSend.put("customer", document.getId());
                                        dataToSend.put("description", x);
                                        dataToSend.put("dateReceived", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("date received")).toString());
                                        dataToSend.put("dateToCollect", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("date to collect")).toString());
                                        dataToSend.put("paid", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("paid")).toString());
                                        dataToSend.put("amount", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("amount")).toString());
                                    }
                                    @Override
                                    public void onClick(View v) {
                                        collectionReference
                                                .get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                            if (document.getId().equals(Objects.requireNonNull(dataToSend.get("customer")).toString())) {
                                                                Intent intent;
                                                                intent = new Intent(getApplicationContext(), AddOrder.class);
                                                                intent.putExtra("data", (HashMap<String, Object>) dataToSend);
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
                                delete.setText("Delete Log");
                                delete.setOnClickListener(new View.OnClickListener() {
                                    final Map<String, Object> updates = new HashMap<>();
                                    {
                                        updates.put(x, FieldValue.delete());
                                    }

                                    @Override
                                    public void onClick(View v) {
                                        documentReference.update(updates).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                recreate();
                                            }
                                        });
                                    }
                                });
                            }
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
        collectionReference = method.db
                .collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Orders");

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

                        collectionReference
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int past = 4;
                                        int rnd = new Random().nextInt(colors.length);
                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            if (Objects.requireNonNull(document.getId()).contains(s.toString().toLowerCase())) {
                                                Set<String> keys = Objects.requireNonNull(document.getData()).keySet();
                                                DocumentReference documentReference = collectionReference.document(document.getId());
                                                for (String x : keys) {
                                                    ViewStub stub = new ViewStub(ViewOrders.this);
                                                    stub.setLayoutResource(R.layout.data_card);
                                                    linearLayout.addView(stub);
                                                    View inflated = stub.inflate();
                                                    LinearLayout linearLayout1 = inflated.findViewById(R.id.data_card_linear_layout);
                                                    while (rnd == past) {
                                                        rnd = new Random().nextInt(colors.length);
                                                    }
                                                    linearLayout1.setBackgroundColor(colors[rnd]);
                                                    past = rnd;
                                                    TextView firstData = inflated.findViewById(R.id.first_data);
                                                    firstData.setText("Customer: ");
                                                    TextView name = inflated.findViewById(R.id.full_name);
                                                    name.setText(CommonMethods.toTitleCase(document.getId()));
                                                    TextView email = inflated.findViewById(R.id.email);
                                                    Map<String, Object> descriptorData = (Map<String, Object>) document.getData().get(x);
                                                    assert descriptorData != null;
                                                    String symbol = getString(R.string.Rs);
                                                    email.setText(symbol + " " + Objects.requireNonNull(descriptorData.get("amount")).toString());
                                                    TextView secondData = inflated.findViewById(R.id.second_data);
                                                    secondData.setText("Amount: ");
                                                    Button edit = inflated.findViewById(R.id.edit_btn);
                                                    edit.setText("Edit Log Details");
                                                    edit.setOnClickListener(new View.OnClickListener() {
                                                        {
                                                            dataToSend = new HashMap<>();
                                                            dataToSend.put("customer", document.getId());
                                                            dataToSend.put("description", x);
                                                            dataToSend.put("dateReceived", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("date received")).toString());
                                                            dataToSend.put("dateToCollect", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("date to collect")).toString());
                                                            dataToSend.put("paid", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("paid")).toString());
                                                            dataToSend.put("amount", Objects.requireNonNull(((Map<?, ?>) Objects.requireNonNull(document.getData().get(x))).get("amount")).toString());
                                                        }

                                                        @Override
                                                        public void onClick(View v) {
                                                            collectionReference
                                                                    .get()
                                                                    .addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                                                if (document.getId().equals(Objects.requireNonNull(dataToSend.get("customer")).toString())) {
                                                                                    Intent intent;
                                                                                    intent = new Intent(getApplicationContext(), AddOrder.class);
                                                                                    intent.putExtra("data", (HashMap<String, Object>) dataToSend);
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
                                                    delete.setText("Delete Log");
                                                    delete.setOnClickListener(new View.OnClickListener() {
                                                        final Map<String, Object> updates = new HashMap<>();

                                                        {
                                                            updates.put(x, FieldValue.delete());
                                                        }

                                                        @Override
                                                        public void onClick(View v) {
                                                            documentReference.update(updates).addOnCompleteListener(task1 -> {
                                                                if (task1.isSuccessful()) {
                                                                    recreate();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                                scrollView.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
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
