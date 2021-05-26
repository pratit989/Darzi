package com.infinityandriod.darzi.Common.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.infinityandriod.darzi.R;
import com.infinityandriod.darzi.User.UserDashboard;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    private FirebaseAuth mAuth;

    TextInputLayout email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_login);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.login_email_id);
        password = findViewById(R.id.login_password);
    }

    public void letTheUserLoggedIn(View view) {

        if (validateEmail() | !validatePassword()) {
            return;
        }

        String email_str = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String password_str = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email_str, password_str)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        Intent i = new Intent(Login.this, UserDashboard.class); // set the new task and clear flags
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateEmail() {
        String val = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return true;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return true;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return false;
        }
    }

    private boolean validatePassword() {

        String val = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        String checkPassword = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";


        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (val.matches(checkPassword)) {
            password.setError("Password should contain 4 characters!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void callSignUpScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.signup_btn), "transition_signup");
        startActivity(intent);
    }

    public void callForgetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), MakeSelection.class);
        startActivity(intent);
    }
}