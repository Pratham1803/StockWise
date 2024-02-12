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
import com.example.stockwise.loginModule.Registration;
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
    protected final int navHomeId=1;
    protected final int navCustomerId=2;
    protected final int navOrderId=3;
    protected final int navProductId=4;
    protected final int navProfileId=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityMainBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        //Bottom Navigation
        bind.meowBottom.add(new MeowBottomNavigation.Model(navHomeId,R.drawable.homevector));
        bind.meowBottom.add(new MeowBottomNavigation.Model(navCustomerId,R.drawable.custvector));
        bind.meowBottom.add(new MeowBottomNavigation.Model(navOrderId,R.drawable.ordervector));
        bind.meowBottom.add(new MeowBottomNavigation.Model(navProductId,R.drawable.productvector));
        bind.meowBottom.add(new MeowBottomNavigation.Model(navProfileId,R.drawable.profilevector));

        bind.meowBottom.show(navHomeId,true);
        bind.meowBottom.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                int currentId = model.getId();
                if(currentId == navHomeId ){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, homeFragment).commit();
                } else if (currentId == navCustomerId) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, personFragment).commit();
                }else if(currentId == navOrderId){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, orderFragment).commit();
                } else if (currentId == navProductId) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, itemFragment).commit();
                }else if (currentId == navProfileId) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, profileFragment).commit();
                }
                return null;
            }
        });

        // setup of drawer navigation
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
                txtName.setText(Params.getOwnerModel().getOwner_name());

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

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, homeFragment).commit();
    }

    // on drawer navigating item select listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // id of selected fragment item

        if(id == R.id.nav_home) { // home fragment selected
            bind.meowBottom.show(navHomeId,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, homeFragment).commit();
        }
        else if (id == R.id.nav_profile) { // profile fragment selected
            bind.meowBottom.show(navProfileId,true);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, profileFragment).commit();
        }
        else if (id == R.id.nav_manageProducts) { // category fragment selected
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, itemFragment).commit();
        }
        else if (id == R.id.nav_shop)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,categoryFragment).commit();
        else if (id == R.id.nav_settings) // user click on log out
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,profileFragment).commit();

        bind.mainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}