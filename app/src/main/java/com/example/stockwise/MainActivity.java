package com.example.stockwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stockwise.databinding.ActivityMainBinding;
import com.example.stockwise.fragments.HomeFragment;
import com.example.stockwise.fragments.category.ManageCategory;
import com.example.stockwise.fragments.product.ProductFragment;
import com.example.stockwise.fragments.profile.Manage_Shop;
import com.example.stockwise.fragments.transaction.transactionFragment;
import com.example.stockwise.fragments.person.PersonFragment;
import com.example.stockwise.fragments.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

import com.example.stockwise.fragments.profile.Settings;
import kotlin.Unit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding bind; // declaring view binding

    // declaring fragments object
    ProductFragment productFragment = new ProductFragment(); // product fragment object
    PersonFragment personFragment = new PersonFragment(); // person fragment object
    ProfileFragment profileFragment = new ProfileFragment(); // profile fragment object
    HomeFragment homeFragment = new HomeFragment(); // home fragment object
    transactionFragment transactionFragment = new transactionFragment(); // transaction fragment object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityMainBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot()); // setting view binding root view

        // setup toolbar
        setSupportActionBar(bind.toolbar); // setting toolbar as action bar

        // Adding Menu items in Bottom Navigation
        bind.bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    int id = item.getItemId(); // getting the id of currently clicked menu item
                    if(id == R.id.nav_home){
                        changeFragment(homeFragment,R.string.titleHome);
                        return true;
                    } else if (id == R.id.nav_customer) {
                        changeFragment(personFragment,R.string.titlePerson);
                        return true;
                    } else if (id == R.id.nav_transaction) {
                        changeFragment(transactionFragment,R.string.titleTransaction);
                        return true;
                    } else if (id == R.id.nav_product) {
                        changeFragment(productFragment,R.string.titleProduct);
                        return true;
                    } else if (id == R.id.nav_profile) {
                        changeFragment(profileFragment,R.string.titleProfile);
                        return true;
                    }
                    return false;
                }
        );

        bind.bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // setting drawer and custom toolbar
        setSupportActionBar(bind.toolbar); // toolbar setup
        bind.navDrawerView.setNavigationItemSelectedListener(this); // drawer navigation item select listener setup

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,bind.mainDrawerLayout,bind.toolbar,R.string.open_nav,R.string.close_nav);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white)); // setting color of 3 line icon to open drawer

        // drawer listener, to show bottom navigation when drawer is closed or when drawer is open show bottom navigation
        bind.mainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // drawer is opened/ slide
                bind.bottomNavigationView.setVisibility(View.GONE); // setting visibility gone to bottom nav

                // setting values to name, number and image of view
                TextView txtName = findViewById(R.id.txtDrawerName); // getting name text view
                txtName.setText(Params.getOwnerModel().getOwner_name()); // setting name of owner

                TextView txtNum = findViewById(R.id.txtDrawerNum); // getting number text view
                txtNum.setText(Params.getOwnerModel().getContact_num()); // setting number of owner

                ImageView imgProfile = findViewById(R.id.imgDrawerProfile); // getting image view
                Glide.with(MainActivity.this).load(Params.getOwnerModel().getPicture()).into(imgProfile); // setting image of owner
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // drawer is closed, so visible the bottom nav
                bind.bottomNavigationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        bind.mainDrawerLayout.addDrawerListener(toggle); // adding listener to drawer
        toggle.syncState(); // syncing the drawer

        // setting home screen as default screen when app is open
        changeFragment(homeFragment,R.string.titleHome);

        // checking permission for sending sms
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){
        }
        else { // if permission is not granted then ask for permission
            // asking for permission
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},100);
        }
    }

    // activity result of sms permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // checking if permission is granted or not
        if(requestCode==100 && grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED) { // if permission is granted
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show(); // show toast message
        }
        else { // if permission is denied
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show(); // show toast message
        }
    }

    // on drawer navigating item select listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // storing currently selected id

        if(id == R.id.nav_home) { // home fragment selected
            changeFragment(homeFragment,R.string.titleHome); // change the screen to home screen
            bind.bottomNavigationView.setSelectedItemId(R.id.nav_home); // displaying the home item is selected in bottom nav
        }
        else if (id == R.id.nav_profile) { // profile fragment selected
            changeFragment(profileFragment,R.string.titleProfile); // change the screen to profile screen
            bind.bottomNavigationView.setSelectedItemId(R.id.nav_profile);  // displaying the profile item is selected in bottom nav
        }
        else if (id == R.id.nav_shop) { // profile fragment selected
            startActivity(new Intent(MainActivity.this, Manage_Shop.class));
        }
        else if (id == R.id.nav_manageProducts) { // Products fragment selected
            changeFragment(productFragment,R.string.titleProduct); // change the screen to product screen
            bind.bottomNavigationView.setSelectedItemId(R.id.nav_product); // displaying the product item is selected in bottom nav
        } else if (id == R.id.nav_category) { // category fragment
            // changing screen to category screen
            startActivity(new Intent(MainActivity.this, ManageCategory.class));
        }
        else if (id == R.id.nav_settings) // user click on settings
            startActivity(new Intent(MainActivity.this,Settings.class));

        bind.mainDrawerLayout.closeDrawer(GravityCompat.START); // when any item is click after that close the drawer
        return true;
    }

    // change the fragment method
    private void changeFragment(Fragment fragment,int title){
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, fragment).commit(); // changing the fragment that is given in argument
            bind.toolbar.setTitle(title); // setting title of the screen in action bar
            if (title == R.string.titleHome) { // if title is home then set the shop name as title
                bind.toolbar.setTitle(Params.getOwnerModel().getShop_name()); // setting title of the screen in action bar
            }
        }catch (Exception e){
            Log.d("ErrorMsg", "changeFragment: "+e.getMessage());
        }
    }
}