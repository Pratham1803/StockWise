package com.example.stockwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.stockwise.databinding.ActivityMainBinding;
import com.example.stockwise.fragments.HomeFragment;
import com.example.stockwise.fragments.category.CategoryFragment;
import com.example.stockwise.fragments.item.ItemFragment;
import com.example.stockwise.fragments.order.orderFragment;
import com.example.stockwise.fragments.person.PersonFragment;
import com.example.stockwise.fragments.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding bind; // declaring view binding

    // declaring fragments object
    CategoryFragment categoryFragment;
    ItemFragment itemFragment;
    PersonFragment personFragment;
    ProfileFragment profileFragment;
    HomeFragment homeFragment;
    orderFragment orderFragment;

    // Bottom Navigation Objects
    protected final int home=1;
    protected final int customer=2;
    protected final int order=3;
    protected final int product=4;
    protected final int profile=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityMainBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        //Bottom Navigation
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meowBottom);
        bottomNavigation.add(new MeowBottomNavigation.Model(home,R.drawable.homevector));
        bottomNavigation.add(new MeowBottomNavigation.Model(customer,R.drawable.custvector));
        bottomNavigation.add(new MeowBottomNavigation.Model(order,R.drawable.ordervector));
        bottomNavigation.add(new MeowBottomNavigation.Model(product,R.drawable.productvector));
        bottomNavigation.add(new MeowBottomNavigation.Model(profile,R.drawable.profilevector));



        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Toast.makeText(MainActivity.this, "Item Click"+model.getId(), Toast.LENGTH_SHORT).show();
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                String name;

                switch (model.getId()){
                    case home:name="Home";
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,homeFragment).commit();
                        break;
                    case customer:name="Customer";
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,personFragment).commit();
                        break;
                    case order:name="Order";
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,orderFragment).commit();
                        break;
                    case product:name="Product";
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,itemFragment).commit();
                        break;
                    case profile:name="Profile";
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,profileFragment).commit();
                        break;
                }
                return null;
            }
        });

        // initializing Fragments object
        categoryFragment = new CategoryFragment();
        itemFragment = new ItemFragment();
        personFragment = new PersonFragment();
        profileFragment = new ProfileFragment();
        homeFragment = new HomeFragment();
        orderFragment = new orderFragment();

        // setting drawer and custom toolbar
        setSupportActionBar(bind.toolbar); // toolbar setup
        bind.navDrawerView.setNavigationItemSelectedListener(this); // drawer navigation item select listener setup

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,bind.mainDrawerLayout,bind.toolbar,R.string.open_nav,R.string.close_nav);
        bind.mainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                bind.meowBottom.setVisibility(View.GONE);
                TextView txtName = findViewById(R.id.txtDrawerName);
                txtName.setText(Params.getOwnerModel().getName());

                TextView txtNum = findViewById(R.id.txtDrawerNum);
                txtNum.setText(Params.getOwnerModel().getContact_num());

                ImageView imgProfile = findViewById(R.id.imgDrawerProfile);
                Glide.with(MainActivity.this).load(Params.getOwnerModel().getPicture()).into(imgProfile);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                bind.meowBottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        bind.mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            // setup of default fragment as home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,homeFragment).commit();
            bind.navDrawerView.setCheckedItem(R.id.nav_home);
        }
    }

    // on drawer navigating item select listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // id of selected fragment item

        if(id == R.id.nav_home) // home fragment selected
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, homeFragment).commit();
        else if (id == R.id.nav_profile) // profile fragment selected
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,profileFragment).commit();
        else if (id == R.id.nav_manageProducts) // category fragment selected
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,itemFragment).commit();
        else if (id == R.id.nav_shop)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,categoryFragment).commit();
        else if (id == R.id.nav_settings) // user click on log out
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,profileFragment).commit();

        bind.mainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}