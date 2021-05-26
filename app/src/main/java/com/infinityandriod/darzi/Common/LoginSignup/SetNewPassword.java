package com.infinityandriod.darzi.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infinityandriod.darzi.R;

import java.util.Objects;

public class SetNewPassword extends AppCompatActivity {

    private static final String TAG = "Set new Password";
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String newPassword;
    String confirmPassword;
    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void getPassword(View view) {
        textInputLayout = findViewById(R.id.newPassword);
        newPassword = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
        textInputLayout = findViewById(R.id.confirmPassword);
        confirmPassword = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();

        if (!newPassword.equals(confirmPassword) | !validatePassword()) {
            return;
        }

        // Add Password
        firebaseUser.updatePassword(Objects.requireNonNull(newPassword))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User password updated.");
                        Intent intent = new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        firebaseAuth.signOut();
                    } else {
                        Toast.makeText(SetNewPassword.this, "Failed to change user password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validatePassword() {

        String val = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();
        String checkPassword =  "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=S+$)" +           //no white spaces
                ".{6,}" +               //at least 4 characters
                "$";


        if (val.isEmpty()) {
            textInputLayout.setError("Field can not be empty");
            return false;
        }  else if (val.matches(checkPassword)) {
            textInputLayout.setError("Password should contain 4 characters!");
            return false;
        } else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }
}