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

public class Women extends AppCompatActivity {

    private static final String TAG = "Women";
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

    // Shirt Hooks
    private final String[] shirtMeasurements = {
            "womens_shirt_chest",
            "womens_shirt_stomach",
            "womens_shirt_waist",
            "womens_shirt_hips",
            "womens_shirt_shoulder",
            "womens_shirt_jacket_length",
            "womens_shirt_sleeve_length",
            "womens_shirt_short_sleeve_length",
            "womens_shirt_wrist_cuff",
            "womens_shirt_bicep",
            "womens_shirt_shirt_length",
            "womens_shirt_arm_hole",
            "womens_shirt_inseam",
            "womens_shirt_knee",
            "womens_shirt_half_hem"
    };

    // Blouses hook
    private final String[] blouseMeasurements = {
            "womens_blouse_shirt_length",
            "womens_blouse_shoulder_width",
            "womens_blouse_neck",
            "womens_blouse_chest",
            "womens_blouse_bicep",
            "womens_blouse_wrist",
            "womens_blouse_sleeve",
            "womens_blouse_short_sleeve",
            "womens_blouse_half_3_4_sleeve",
            "womens_blouse_waist",
            "womens_blouse_stomach",
            "womens_blouse_breast",
            "womens_blouse_waist_point",
            "womens_blouse_sleeve_hole",
            "womens_blouse_bust"
    };

    // Skirt hook
    private final String[] skirtMeasurements = {
            "womens_skirt_waist",
            "womens_skirt_waist_to_hip_length",
            "womens_skirt_skirt_length",
            "womens_skirt_hip_width",
    };

    // Skirt hook
    private final String[] gownMeasurements = {
            "womens_gown_hallow_to_hem",
            "womens_gown_bust",
            "womens_gown_waist",
            "womens_gown_hips",
    };

    private final String[][] measurements = {shirtMeasurements, blouseMeasurements, skirtMeasurements, gownMeasurements};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_womens);

        // Initiate Firebase Variables
        method = new FirestoreMethods();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Back btn hook
        backBtn = findViewById(R.id.womens_back_button);
        backBtn.setOnClickListener(view -> {Women.super.onBackPressed(); finish();});

        fullName = findViewById(R.id.womens_customer_details_fullname);
        phoneNo = findViewById(R.id.womens_customer_details_phone);
        email = findViewById(R.id.womens_customer_details_email);

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
        Button btn = findViewById(R.id.womens_add_button);
        btn.setText("Edit Customer");
    }

    public void getData(View view) {
        Customers = new HashMap<>();
        Customers.put("gender", "Female");

        // Validate
        if (!validateEmail(email) | !validateFullName(fullName)){
            return;
        }

        // Adding customer details
        Customers.put("fullName", Objects.requireNonNull(fullName.getEditText()).getText().toString().trim());
        Customers.put("phoneNo", Objects.requireNonNull(phoneNo.getEditText()).getText().toString().trim());
        Customers.put("email", Objects.requireNonNull(email.getEditText()).getText().toString().trim());

        checkForDuplicate();
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
                            sendWomensData();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void sendWomensData() {
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
}