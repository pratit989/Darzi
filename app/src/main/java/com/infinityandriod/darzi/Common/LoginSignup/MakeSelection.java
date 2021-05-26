package com.infinityandriod.darzi.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hbb20.CountryCodePicker;
import com.infinityandriod.darzi.R;

public class MakeSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_selection);

        ImageView backBtn = findViewById(R.id.general_back_btn);
        backBtn.setOnClickListener(view -> {
            MakeSelection.super.onBackPressed();
            finish();
        });

        Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
        intent.putExtra("type", "mobile");
        startActivity(intent);
        finish();
    }

    public void callForgetPasswordMobile(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
        intent.putExtra("type", "mobile");
        startActivity(intent);
    }

    public void callForgetPasswordEmail(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
        intent.putExtra("type", "email");
        startActivity(intent);
    }
}