package com.infinityandriod.darzi.User;

import android.annotation.SuppressLint;
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

public class AddStaff extends AppCompatActivity {

    private static final String TAG = "AddStaff";
    private ImageView backBtn;
    private FirestoreMethods method;
    private final Map<String, Object> staffData = new HashMap<>();
    private HashMap<String, Object> _userData;
    private Button staffDesignation;
    private TextInputLayout staffName;
    private TextInputLayout staffAddress;
    private TextInputLayout staffPhoneNo;
    private TextInputLayout staffSalary;
    ArrayAdapter<String> adapter;
    private boolean replaceable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        replaceable = false;

        method = new FirestoreMethods();

        //hooks
        backBtn = findViewById(R.id.add_staff_back_button);
        backBtn.setOnClickListener(view -> {
            AddStaff.super.onBackPressed();
            finish();
        });

        // Data hooks
        staffDesignation = findViewById(R.id.add_staff_designation_button);
        staffName = findViewById(R.id.add_staff_fullname);
        staffAddress = findViewById(R.id.add_staff_address);
        staffPhoneNo = findViewById(R.id.add_staff_phone_no);
        staffSalary = findViewById(R.id.add_staff_salary);

        method.db.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Designations").document("List")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Set<String> keys = Objects.requireNonNull(document.getData()).keySet();
                            int n = keys.size();
                            String[] arr = new String[n];
                            int i = 0;
                            for (String x : keys)
                                arr[i++] = x;
                            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

        _userData = (HashMap<String, Object>) getIntent().getSerializableExtra("data");

        try {
            receiveData();
            replaceable = true;
        } catch (Exception ignore) {}
    }

    public void getStaffData(View view) {
        // Adding data to map
        staffData.put("designation", Objects.requireNonNull(staffDesignation.getText()).toString().toLowerCase().trim());
        staffData.put("fullName", Objects.requireNonNull(staffName.getEditText()).getText().toString().toLowerCase().trim());
        staffData.put("address", Objects.requireNonNull(staffAddress.getEditText()).getText().toString().toLowerCase().trim());
        staffData.put("phoneNo", Objects.requireNonNull(staffPhoneNo.getEditText()).getText().toString().toLowerCase().trim());
        staffData.put("salary", Objects.requireNonNull(staffSalary.getEditText()).getText().toString().toLowerCase().trim());

        if (replaceable) {
            sendStaffData();
        } else {
            checkForDuplicate((String) staffData.get("fullName"));
        }
    }

    private void sendStaffData() {
        DocumentReference documentReference = method.db
                .collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Staff")
                .document(Objects.requireNonNull(staffName.getEditText()).getText().toString().toLowerCase().trim());
        method.send_data(staffData, documentReference);
        if (replaceable) {
            if (!Objects.requireNonNull(_userData.get("fullName")).toString().toLowerCase().equals(staffName.getEditText().toString().toLowerCase().trim())) {
                method.db.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .collection("Staff").document(Objects.requireNonNull(_userData.get("fullName")).toString().toLowerCase())
                        .delete();
            }
        }
        backBtn.callOnClick();
    }

    private void checkForDuplicate(String fullName) {
        method.db.collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Staff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean duplicate = false;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.getId().contains(fullName)) {
                                staffName.setError("A staff named "
                                        + Objects.requireNonNull(staffName.getEditText()).getText().toString().trim()
                                        + " already exists");
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            sendStaffData();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void receiveData() {
        Objects.requireNonNull(staffDesignation).setText(CommonMethods.toTitleCase((String) Objects.requireNonNull(_userData.get("designation"))));
        Objects.requireNonNull(staffName.getEditText()).setText((String) _userData.get("fullName"));
        Objects.requireNonNull(staffAddress.getEditText()).setText((String) _userData.get("address"));
        Objects.requireNonNull(staffPhoneNo.getEditText()).setText((String) _userData.get("phoneNo"));
        Objects.requireNonNull(staffSalary.getEditText()).setText((String) _userData.get("salary"));

        Button btn = findViewById(R.id.add_staff_button);
        btn.setText("Edit Staff");
    }

    public void onClick(View w) {
        new AlertDialog.Builder(this)
                .setTitle("Select Designation")
                .setAdapter(adapter, (dialog, which) -> {
                    // TODO: user specific action
                    staffDesignation.setText(adapter.getItem(which));
                    dialog.dismiss();
                }).create().show();
    }
}