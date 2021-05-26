package com.infinityandriod.darzi.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.infinityandriod.darzi.R;

public class StartUpScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_screen);
    }

    public void callLoginScreen(View view){
        Intent intent = new Intent(getApplicationContext(),Login.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.login_btn),"transition_login");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartUpScreen.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
            finish();
        }


    }

    public void callSignUpScreen(View view){
        Intent intent = new Intent(getApplicationContext(),SignUp.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.signup_btn),"transition_signup");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartUpScreen.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
            finish();
        }


    }

}