package com.infinityandriod.darzi.User;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.CommonMethods;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Mens extends AppCompatActivity {

    private static final String TAG = "Mens";
    private ImageView backBtn;
    private FirestoreMethods method;
    private FirebaseUser user;
    private HashMap<String, Object> _userData;
    private Map<String, Object> Customers;
    boolean replaceable = false;

    // Customer details hooks
    private TextInputLayout fullName;
    private TextInputLayout phoneNo;
    private TextInputLayout email;

    // Suit Hooks
    private final String[] suitMeasurements = {
            "mens_suit_neck",
            "mens_suit_chest",
            "mens_suit_stomach",
            "mens_suit_waist",
            "mens_suit_hips",
            "mens_suit_shoulder",
            "mens_suit_jacket_length",
            "mens_suit_sleeve_length",
            "mens_suit_wrist",
            "mens_suit_vest_length",
            "mens_suit_crotch",
            "mens_suit_thigh_width",
            "mens_suit_trouser_length",
            "mens_suit_inseam",
            "mens_suit_knee",
            "mens_suit_half_hem"
    };

    // Trousers hook
    private final String[] trouserMeasurements = {
            "mens_trouser_waist",
            "mens_trouser_hips",
            "mens_trouser_crotch",
            "mens_trouser_thigh_width",
            "mens_trouser_trouser_length",
            "mens_trouser_inseam",
            "mens_trouser_knee",
            "mens_trouser_half_hem"
    };

    private final String[][] measurements = {suitMeasurements, trouserMeasurements};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mens);

        fullName = findViewById(R.id.mens_customer_details_fullname);
        phoneNo = findViewById(R.id.mens_customer_details_phone);
        email = findViewById(R.id.mens_customer_details_email);

        // Initiate Firebase Variables
        method = new FirestoreMethods();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Back btn hook
        backBtn = findViewById(R.id.mens_back_button);
        backBtn.setOnClickListener(view -> {Mens.super.onBackPressed(); finish();});

         _userData = (HashMap<String, Object>) getIntent().getSerializableExtra("data");

        try {
            receiveData();
            replaceable = true;
        } catch (Exception ignore) {}
    }

    @SuppressLint("SetTextI18n")
    private void receiveData() {
        Objects.requireNonNull(fullName.getEditText()).setText(CommonMethods.toTitleCase((String) Objects.requireNonNull(_userData.get("fullName"))));
        Objects.requireNonNull(phoneNo.getEditText()).setText((String) _userData.get("phoneNo"));
        Objects.requireNonNull(email.getEditText()).setText((String) _userData.get("email"));
        int temp;
        TextInputLayout textInputLayout;
        for (String[] measurement : measurements) {
            for (String s : measurement) {
                temp = getResources().getIdentifier(s, "id", getPackageName());
                textInputLayout = findViewById(temp);
                Objects.requireNonNull(textInputLayout.getEditText()).setText((String) _userData.get(s));
            }
        }
        Button btn = findViewById(R.id.mens_add_button);
        btn.setText("Edit Customer");
    }

    public void getData(View view) {
        Customers = new HashMap<>();
        Customers.put("gender", "Male");

        // Validate
        if (!validateEmail(email) | !validateFullName(fullName)){
            return;
        }

        // Adding customer details
        Customers.put("fullName", Objects.requireNonNull(fullName.getEditText()).getText().toString().toLowerCase().trim());
        Customers.put("phoneNo", Objects.requireNonNull(phoneNo.getEditText()).getText().toString().trim());
        Customers.put("email", Objects.requireNonNull(email.getEditText()).getText().toString().toLowerCase().trim());

        checkForDuplicate();
    }

    private void sendMensData () {
        int temp;
        TextInputLayout textInputLayout;
        for (String[] measurement : measurements) {
            for (String s : measurement) {
                temp = getResources().getIdentifier(s, "id", getPackageName());
                textInputLayout = findViewById(temp);
                Customers.put(s, Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim());
            }
        }

        DocumentReference documentReference;
        documentReference = method.db
                .collection("Users").document(user.getUid())
                .collection("Customers").document((String) Objects.requireNonNull(Customers.get("fullName")));

        method.send_data(Customers, documentReference);
        if (replaceable) {
            if (!Objects.requireNonNull(_userData.get("fullName")).toString().toLowerCase().equals(Objects.requireNonNull(fullName.getEditText()).toString().toLowerCase().trim())) {
                method.db.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .collection("Customers").document(Objects.requireNonNull(_userData.get("fullName")).toString().toLowerCase())
                        .delete();
            }
        }
        backBtn.callOnClick();
    }

    private boolean validateEmail(@NonNull TextInputLayout email) {
        String val = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFullName(@NonNull TextInputLayout fullName) {

        String val = Objects.requireNonNull(fullName.getEditText()).getText().toString().trim();


        if (val.isEmpty()) {
            fullName.setError("Field can not be empty");
            return false;
        } else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private void checkForDuplicate() {
        method.db.collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Customers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean duplicate = false;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.getId().equals(Objects.requireNonNull(fullName.getEditText()).getText().toString().toLowerCase().trim())) {
                                fullName.setError("A customer named "
                                        + Objects.requireNonNull(fullName.getEditText()).getText().toString().trim()
                                        + " already exists");
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            sendMensData();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}