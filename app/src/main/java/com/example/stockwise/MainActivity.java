package com.example.stockwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.stockwise.databinding.ActivityMainBinding;
import com.example.stockwise.fragments.HomeFragment;
import com.example.stockwise.fragments.category.CategoryFragment;
import com.example.stockwise.fragments.item.ItemFragment;
import com.example.stockwise.fragments.person.PersonFragment;
import com.example.stockwise.fragments.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding bind; // declaring view binding

    // declaring fragments object
    CategoryFragment categoryFragment;
    ItemFragment itemFragment;
    PersonFragment personFragment;
    ProfileFragment profileFragment;
    HomeFragment homeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityMainBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        // initializing Fragments object
        categoryFragment = new CategoryFragment();
        itemFragment = new ItemFragment();
        personFragment = new PersonFragment();
        profileFragment = new ProfileFragment();
        homeFragment = new HomeFragment();

        // setting drawer and custom toolbar
        setSupportActionBar(bind.toolbar); // toolbar setup
        bind.navDrawerView.setNavigationItemSelectedListener(this); // drawer navigation item select listener setup

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,bind.mainDrawerLayout,bind.toolbar,R.string.open_nav,R.string.close_nav);
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
        else if (id == R.id.nav_category) // category fragment selected
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view,categoryFragment).commit();
        else if (id == R.id.nav_logout) // user click on log out
            logOutUser(MainActivity.this); // calling log out user method

        return true;
    }

    // log out user from the app
    public void logOutUser(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to LogOut ?");

        // Set Alert Title
        builder.setTitle("Log Out !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Params.getAUTH().signOut(); // signing out current user
                context.startActivity(new Intent(context, LandingPage.class)); // redirecting user to sign in activity
                ((Activity)context).finish(); // finishing this activity
            }
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}