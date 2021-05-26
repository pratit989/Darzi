package com.infinityandriod.darzi.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.infinityandriod.darzi.R;

import java.util.Calendar;
import java.util.HashMap;

public class SignUp2ndClass extends AppCompatActivity {

    //Variables
    ImageView backBtn;
    Button next, login;
    TextView titleText;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up2nd_class);


        //Hooks
        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);
        login = findViewById(R.id.signup_login_button);
        titleText = findViewById(R.id.signup_title_text);
        ImageView backBtn = findViewById(R.id.signup_back_button);
        backBtn.setOnClickListener(view -> SignUp2ndClass.super.onBackPressed());

        //slideText = findViewById(R.id.signup_slide_text);
        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);


    }


    public void call3rdSignupScreen(View view) {

        final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (!validateGender() | !validateAge()){
            return;
        }

        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());

        HashMap<String, Object> _userData = (HashMap<String, Object>) getIntent().getSerializableExtra("userData");
        _userData.put("dateOfBirth", datePicker.getDayOfMonth() + "-" + MONTHS[datePicker.getMonth()] + "-" + datePicker.getYear());
        _userData.put("gender", selectedGender.getText());


        Intent intent = new Intent(getApplicationContext(), SignUp3rdClass.class);

        intent.putExtra("userData", _userData);

        //Add Transition and call next activity
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<>(next, "transition_next_btn");
        pairs[2] = new Pair<>(login, "transition_login_btn");
        pairs[3] = new Pair<>(titleText, "transition_title_text");
        //pairs[4] = new Pair(slideText, "transition_slide_text");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2ndClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 14) {
            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }


}