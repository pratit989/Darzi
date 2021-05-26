package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.infinityandriod.darzi.R;

import java.util.Calendar;

public class Calender extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        //hooks

        backBtn = findViewById(R.id.back_pressed);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calender.super.onBackPressed();
            }
        });

    }
}