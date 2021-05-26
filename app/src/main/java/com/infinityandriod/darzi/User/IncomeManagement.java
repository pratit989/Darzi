package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.infinityandriod.darzi.R;

public class IncomeManagement extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_management);

        //hooks

        backBtn = findViewById(R.id.back_pressed);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IncomeManagement.super.onBackPressed();
            }
        });

    }

    public void callAddIncomeScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), AddIncome.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.expand_all_add_income), "transition_add_incomme");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(IncomeManagement.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    public void callAddIncomeCategoryScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), AddIncomeCategory.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.expand_all_add_income_category), "transition_add_income_category");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(IncomeManagement.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void CallViewIncomeScreen(View view) {
        Intent intent = new Intent(getApplicationContext(),ViewIncome.class);
        startActivity(intent);
    }
}