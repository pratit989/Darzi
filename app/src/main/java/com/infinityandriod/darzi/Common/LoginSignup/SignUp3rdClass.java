package com.infinityandriod.darzi.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.infinityandriod.darzi.R;

import java.util.HashMap;
import java.util.Objects;

public class SignUp3rdClass extends AppCompatActivity {

    ScrollView scrollView;
    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up3rd_class);

        //hooks
        scrollView = findViewById(R.id.signup_3rd_screen_scroll_view);
        countryCodePicker = findViewById(R.id.country_code_picker);
        phoneNumber = findViewById(R.id.signup_phone_number);
        ImageView backBtn = findViewById(R.id.signup_back_button);
        backBtn.setOnClickListener(view -> SignUp3rdClass.super.onBackPressed());

    }

    public void callVerifyOTPScreen(View view) {

        //validate fields

        if(!validatePhoneNumber()){
            return;
        }

        //get all values passed from previous screens using intent
        HashMap<String, Object> _userData = (HashMap<String, Object>) getIntent().getSerializableExtra("userData");

        String _getUserEnteredPhoneNumber = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString().trim(); //get phone number
        if (_getUserEnteredPhoneNumber.charAt(0) == '0'){
            _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
        }
        final String _phoneNo = "+"+countryCodePicker.getFullNumber()+_getUserEnteredPhoneNumber;
        _userData.put("phoneNo", _phoneNo);


        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);

        //pass all fields to the next activity
        intent.putExtra("userData", _userData);

        //add transition
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(scrollView, "transition_OTP_screen");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
            finish();
        }


    }
    private boolean validatePhoneNumber() {
        String val = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString().trim();
        String checkSpaces = "\\A\\w{1,10}\\z";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkSpaces)) {
            phoneNumber.setError("No White spaces are allowed!");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    public void callLoginScreen(View view) {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.login_btn),"transition_login");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClass.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
            finish();
        }
    }
}