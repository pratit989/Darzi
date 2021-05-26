package com.infinityandriod.darzi.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infinityandriod.darzi.R;
import com.infinityandriod.darzi.User.UserDashboard;

public class SplashScreen extends AppCompatActivity {

    //variables
    ImageView backgroundImage;
    TextView poweredByLine;

    //animations
    Animation sideAnim, bottomAnim;

    SharedPreferences onBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //hooks
        backgroundImage = findViewById(R.id.background_image);
        poweredByLine = findViewById(R.id.powered_by_line);

        //animations
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //set animations on elements

        backgroundImage.setAnimation(sideAnim);
        poweredByLine.setAnimation(bottomAnim);

        int SPLASH_TIMER = 5000;
        new Handler().postDelayed(() -> {


            onBoardingScreen = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);

            if (user != null) {
                Intent intent = new Intent(getApplicationContext(), UserDashboard.class);
                startActivity(intent);
            } else {

                SharedPreferences.Editor  editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime",false);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
                startActivity(intent);
            }
            finish();

        }, SPLASH_TIMER);

    }
}
