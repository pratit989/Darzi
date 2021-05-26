package com.infinityandriod.darzi.User;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infinityandriod.darzi.Common.LoginSignup.StartUpScreen;
import com.infinityandriod.darzi.HelperClasses.HomeAdapter.ManagementAdapter;
import com.infinityandriod.darzi.HelperClasses.HomeAdapter.ManagementHelperClass;
import com.infinityandriod.darzi.HelperClasses.HomeAdapter.ViewEditCardAdapter;
import com.infinityandriod.darzi.HelperClasses.HomeAdapter.ViewEditCardHelperClass;
import com.infinityandriod.darzi.R;

import java.util.ArrayList;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //variables
    private static final float END_SCALE = 0.7f;

    private RecyclerView viewEditRecycler, managementRecycler;
    private RecyclerView.Adapter adapter;
    private ImageView menuIcon;
    private LinearLayout contentView;
    RequestOptions options;
    ImageView imageView;
    StorageReference storageReference;
    FirebaseUser user;


    //Drawer Menu

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        //hooks
        viewEditRecycler = findViewById(R.id.view_edit_recycler);
        managementRecycler =findViewById(R.id.management_recycler);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);


        //menu hooks
        drawerLayout = findViewById((R.id.drawer_layout));
        navigationView = findViewById((R.id.navigation_view));

        // Reference to an image file in Cloud Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child("users/" + user.getUid() + "/profile.jpg");

        // ImageView in your Activity
        imageView = findViewById(R.id.miniProfilePicture);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.blank_profile)
                .error(R.drawable.blank_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .priority(Priority.HIGH)
                .skipMemoryCache(true);
        GlideApp.with(this /* context */)
                .load(storageReference)
                .apply(options)
                .into(imageView);


        navigationDrawer();

        //function calls
        viewEditRecycler();
        managementRecycler();
    }



    //navigation drawer functions
    private void navigationDrawer() {

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        menuIcon.setOnClickListener(view -> {

            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);

            else drawerLayout.openDrawer(GravityCompat.START);


        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_income_management:
                startActivity(new Intent(getApplicationContext(), IncomeManagement.class));
                break;

            case R.id.nav_expense_management:
                startActivity(new Intent(getApplicationContext(), ExpenseManagement.class));
                break;

            case R.id.nav_staff_management:
                startActivity(new Intent(getApplicationContext(), StaffManagement.class));
                break;

            case R.id.nav_add_customer:
                startActivity(new Intent(getApplicationContext(), AddCustomer.class));
                break;


            case R.id.nav_calender:
                startActivity(new Intent(getApplicationContext(), Calender.class));
                break;

            case R.id.nav_add_order:
                startActivity(new Intent(getApplicationContext(), AddOrder.class));
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(UserDashboard.this, StartUpScreen.class); // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;

            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                break;

            case R.id.nav_view_edit_customer:
                startActivity(new Intent(getApplicationContext(), ViewCustomer.class));
                break;

            case R.id.nav_view_edit_order:
                startActivity(new Intent(getApplicationContext(), ViewOrders.class));
                break;
        }


        return true;
    }

    private void viewEditRecycler() {

        viewEditRecycler.setHasFixedSize(true);
        viewEditRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        ArrayList<ViewEditCardHelperClass> viewEditCustomers = new ArrayList<>();

        viewEditCustomers.add(new ViewEditCardHelperClass(R.drawable.design3));
        viewEditCustomers.add(new ViewEditCardHelperClass(R.drawable.design2));
        viewEditCustomers.add(new ViewEditCardHelperClass(R.drawable.design1));


        adapter = new ViewEditCardAdapter(viewEditCustomers);
        viewEditRecycler.setAdapter(adapter);


    }

    private void managementRecycler() {

        managementRecycler.setHasFixedSize(true);
        managementRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        ArrayList<ManagementHelperClass> managementCategories  = new ArrayList<>();

        managementCategories.add(new ManagementHelperClass(R.drawable.add_staff_image,"Staff Management",""));
        managementCategories.add(new ManagementHelperClass(R.drawable.add_income_image,"Income Management",""));
        managementCategories.add(new ManagementHelperClass(R.drawable.add_expense_image,"Expense Management",""));

        adapter = new ManagementAdapter(managementCategories);
        managementRecycler.setAdapter(adapter);

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();

        super.onBackPressed();
    }

    public void callCatalogueScreen(View view){
        Intent intent = new Intent(getApplicationContext(),Catalogue.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.view_edit_background),"transition_catalogue");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboard.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }

    public void callStaffManagementScreen(View view){
        Intent intent = new Intent(getApplicationContext(),StaffManagement.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.management_layout),"transition_staff_management");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboard.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }


    }

    public void callCustomerViewScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), ViewCustomer.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(findViewById(R.id.edit_btn),"transition_edit_customer");

        startActivity(intent);
    }

    public void callViewExpenseScreen(View view) {
        startActivity(new Intent(getApplicationContext(), ViewExpense.class));
    }

    public void callViewIncomeScreen(View view) {
        startActivity(new Intent(getApplicationContext(), ViewIncome.class));
    }

    public void callProfile(View view) {
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }

    @Override
    public void onRestart() {
        super.onRestart();
        GlideApp.with(this /* context */)
                .load(storageReference)
                .apply(options)
                .into(imageView);
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }

    public void callViewOrdersScreen(View view) {
        startActivity(new Intent(getApplicationContext(), ViewOrders.class));
    }
}
