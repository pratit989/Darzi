package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.infinityandriod.darzi.R;

public class AddCustomer extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        //hooks

        backBtn = findViewById(R.id.back_pressed);


        backBtn.setOnClickListener(view -> AddCustomer.super.onBackPressed());

    }

    public void callMensScreen(View view){
        Intent intent = new Intent(getApplicationContext(),Mens.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.expand_all_mens),"transition_mens");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCustomer.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }
    public void callWomenScreen(View view){
        Intent intent = new Intent(getApplicationContext(), Women.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.expand_all_womens),"transition_womens");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCustomer.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }
}