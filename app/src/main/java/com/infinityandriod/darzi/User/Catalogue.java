package com.infinityandriod.darzi.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.infinityandriod.darzi.HelperClasses.HomeAdapter.ViewEditCardAdapter;
import com.infinityandriod.darzi.HelperClasses.HomeAdapter.ViewEditCardHelperClass;
import com.infinityandriod.darzi.R;

import java.util.ArrayList;

public class Catalogue extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        //hooks

        backBtn = findViewById(R.id.back_pressed);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Catalogue.super.onBackPressed();
            }
        });

    }

    public void CallCataloguePathaniScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Pathani.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.catalogue_pathani), "transition_catalogue_pathani");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Catalogue.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    public void CallCatalogueSuitScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Suits.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.catalogue_suit), "transition_catalogue_suit");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Catalogue.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

}