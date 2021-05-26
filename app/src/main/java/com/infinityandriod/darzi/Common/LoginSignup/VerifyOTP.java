package com.infinityandriod.darzi.Common.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.R;
import com.infinityandriod.darzi.User.UserDashboard;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class VerifyOTP extends AppCompatActivity {

    private static final String TAG = "VerifyOTP";

    // [START declare_auth]
    FirebaseAuth firebaseAuth;
    // [END declare_auth]

    FirestoreMethods method;

    PinView pinFromUser;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    TextView otpDescriptionText;
    HashMap<String, Object> _userData;

    // Forget Password Variables
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        method = new FirestoreMethods();

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
            }
        };
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_o_t_p);

        // hooks
        pinFromUser = findViewById(R.id.pin_view);
        otpDescriptionText = findViewById(R.id.otp_description_text);
        _userData = (HashMap<String, Object>) getIntent().getSerializableExtra("userData");

        String message;

        if (_userData == null) {
            number = getIntent().getStringExtra("number");
            message = "Enter one time password sent on " + number;
            sendVerificationCodeToUser(number);
        } else {
            message = "Enter one time password sent on " + _userData.get("phoneNo");
            sendVerificationCodeToUser((String) _userData.get("phoneNo"));
        }
        otpDescriptionText.setText(message);

    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Intent i;
                        if (_userData == null) {
                            i = new Intent(VerifyOTP.this, SetNewPassword.class);
                        } else {
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                            assert user != null;
                            updateUser(user);
                            _userData.remove("password");
                            method.send_data("Users", user.getUid(), _userData);
                            // set the new task and clear flags
                            i = new Intent(VerifyOTP.this, UserDashboard.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                        // Update UI
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(VerifyOTP.this, "The verification code was invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void callNextScreenFromOTP(View view) {

        String code = Objects.requireNonNull(pinFromUser.getText()).toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }

    }

    public void resendCode(View view) {
        resendVerificationCode((String) _userData.get("phoneNo"), mResendToken);
    }

    private void updateUser(FirebaseUser user) {

        // Add Email
        user.updateEmail((String) Objects.requireNonNull(_userData.get("email")))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email updated.");
                    } else {
                        user.delete()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                    }
                                });
                        Toast.makeText(VerifyOTP.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        // Add Password
        user.updatePassword((String) Objects.requireNonNull(_userData.get("password")))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User password updated.");
                    } else {
                        user.delete()
                                .addOnCompleteListener(task12 -> {
                                    if (task12.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                    }
                                });
                        Toast.makeText(VerifyOTP.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        // Update user's display name
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName((String) _userData.get("fullName"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    }
                });
        Toast.makeText(VerifyOTP.this, "The user account was successfully created", Toast.LENGTH_SHORT).show();
    }
}
