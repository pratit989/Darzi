package com.infinityandriod.darzi.Common.LoginSignup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.infinityandriod.darzi.R;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {

    String type;
    TextInputLayout entryBox;
    Drawable icon;
    TextView description;
    String enteredData;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        type = getIntent().getStringExtra("type");

        ImageView backBtn = findViewById(R.id.general_back_btn);
        backBtn.setOnClickListener(view -> {
            ForgetPassword.super.onBackPressed();
            finish();
        });

        entryBox = findViewById(R.id.forgot_pass_email);
        description = findViewById(R.id.forget_password_description);
        countryCodePicker = findViewById(R.id.country_code_picker);

        if (type.equals("mobile")) {
            changeToMobileMethod();
        } else if (type.equals("email")) {
            changeToEmailMethod();
        }
    }

    private void changeToEmailMethod() {
        ((ViewGroup) countryCodePicker.getParent()).removeView(countryCodePicker);
        Objects.requireNonNull(entryBox).setHint("Email");
        icon = AppCompatResources.getDrawable(this, R.drawable.field_email_icon);
        entryBox.setStartIconDrawable(icon);
        description.setText(R.string.forgot_password_details);
    }

    private void changeToMobileMethod() {
        Objects.requireNonNull(entryBox).setHint("Mobile");
        icon = AppCompatResources.getDrawable(this, R.drawable.field_phone_icon);
        entryBox.setStartIconDrawable(icon);
        description.setText(R.string.forgot_password_details_phonNo);
    }

    public void callVerificationMethod(View view) {
        if (type.equals("mobile")) {
            if (!validatePhoneNumber()) {
                return;
            }
            enteredData = Objects.requireNonNull(entryBox.getEditText()).getText().toString().trim().toLowerCase();
            String _phoneNo = "+" + countryCodePicker.getFullNumber() + enteredData;
            Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
            intent.putExtra("number", _phoneNo);
            startActivity(intent);
        }
    }

    private boolean validatePhoneNumber() {
        String val = Objects.requireNonNull(entryBox.getEditText()).getText().toString().trim();
        String checkSpaces = "\\A\\w{1,10}\\z";
        if (val.isEmpty()) {
            entryBox.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkSpaces)) {
            entryBox.setError("No White spaces are allowed!");
            return false;
        } else {
            entryBox.setError(null);
            entryBox.setErrorEnabled(false);
            return true;
        }
    }
}