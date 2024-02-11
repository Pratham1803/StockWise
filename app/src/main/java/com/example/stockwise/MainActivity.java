package com.example.stockwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.stockwise.fragments.HomeFragment;
import com.example.stockwise.fragments.category.CategoryFragment;
import com.example.stockwise.fragments.item.ItemFragment;
import com.example.stockwise.fragments.order.orderFragment;
import com.example.stockwise.fragments.person.PersonFragment;
import com.example.stockwise.fragments.profile.ProfileFragment;
import com.example.stockwise.model.TransactionModel;
import com.google.android.material.navigation.NavigationView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawer;

    // fragments object
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
        setContentView(R.layout.activity_main);

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.main_drawer_layout);
        navDrawer = findViewById(R.id.nav_drawer_view);
        navDrawer.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,homeFragment).commit();
            navDrawer.setCheckedItem(R.id.nav_home);

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_home)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, homeFragment).commit();
        else if (id == R.id.nav_profile)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,profileFragment).commit();
        else if (id == R.id.nav_manageProducts)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,categoryFragment).commit();
        else if (id == R.id.nav_shop)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,categoryFragment).commit();
        else if (id == R.id.nav_settings)
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,categoryFragment).commit();

        return true;
    }

    public void logOutUser(Context context){
        Params.getAUTH().signOut();
        context.startActivity(new Intent(context, Long.class));
        ((Activity)context).finish();
    }
}