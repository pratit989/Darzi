package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.infinityandriod.darzi.R;

public class ExpenseManagement extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_management);

        //hooks

        backBtn = findViewById(R.id.back_pressed);


        backBtn.setOnClickListener(view -> ExpenseManagement.super.onBackPressed());

    }
    public void callAddExpenseScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), AddExpense.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.expand_all_add_expense), "transition_add_expense");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ExpenseManagement.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    public void callAddExpenseCategoryScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), AddExpenseCategory.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.expand_all_add_expense_category), "transition_add_expense_category");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ExpenseManagement.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void CallViewExpenseScreen(View view) {
        Intent intent = new Intent(getApplicationContext(),ViewExpense.class);
        startActivity(intent);
    }
}