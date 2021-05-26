package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.infinityandriod.darzi.R;

public class StaffManagement extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_management);

        //hooks

        backBtn = findViewById(R.id.back_pressed);


        backBtn.setOnClickListener(view -> StaffManagement.super.onBackPressed());

    }

    public void callAddStaffScreen(View view){
        Intent intent = new Intent(getApplicationContext(),AddStaff.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.expand_all_add_staff),"transition_add_staff");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StaffManagement.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }

    public void callViewStaffScreen(View view){
        Intent intent = new Intent(getApplicationContext(),ViewStaff.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.expand_all_view_staff),"transition_add_staff");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StaffManagement.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }
    }

    public void callPaySalaryScreen(View view){
        Intent intent = new Intent(getApplicationContext(),PaySalary.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.expand_all_pay_salary),"transition_pay_salary");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StaffManagement.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }

    public void callAddStaffDesignationScreen(View view){
        Intent intent = new Intent(getApplicationContext(),AddDesignation.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.expand_all_add_staff_designation),"transition_add_staff_designation");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StaffManagement.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }
}