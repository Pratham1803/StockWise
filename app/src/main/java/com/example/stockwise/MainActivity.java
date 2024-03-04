package com.example.stockwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.stockwise.databinding.ActivityMainBinding;
import com.example.stockwise.fragments.HomeFragment;
import com.example.stockwise.fragments.category.ManageCategory;
import com.example.stockwise.fragments.product.ProductFragment;
import com.example.stockwise.fragments.transaction.transactionFragment;
import com.example.stockwise.fragments.person.PersonFragment;
import com.example.stockwise.fragments.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

import com.example.stockwise.MenuScreens.Settings;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding bind; // declaring view binding

    // declaring fragments object
    ProductFragment productFragment;
    PersonFragment personFragment;
    ProfileFragment profileFragment;
    HomeFragment homeFragment;
    transactionFragment transactionFragment;

    // Bottom Navigation Objects
    protected final int navHomeId = 1;
    protected final int navCustomerId = 2;
    protected final int navTransactionId = 3;
    protected final int navProductId = 4;
    protected final int navProfileId = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityMainBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        // setup toolbar
        setSupportActionBar(bind.toolbar);

        // Adding Menu items in Bottom Navigation
        bind.meowBottom.add(new MeowBottomNavigation.Model(navHomeId,R.drawable.homevector)); // home menu
        bind.meowBottom.add(new MeowBottomNavigation.Model(navCustomerId,R.drawable.custvector)); // customer menu
        bind.meowBottom.add(new MeowBottomNavigation.Model(navTransactionId,R.drawable.ordervector)); // transaction menu
        bind.meowBottom.add(new MeowBottomNavigation.Model(navProductId,R.drawable.productvector)); // product menu
        bind.meowBottom.add(new MeowBottomNavigation.Model(navProfileId,R.drawable.profilevector)); // profile menu

        bind.meowBottom.show(navHomeId,true); // displaying bottom navigation on screen
        // setting onclick listener of bottom navigation, when menu item is click change the screen according to it
        bind.meowBottom.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                int currentId = model.getId(); // getting the id of currently clicked menu item

                // checking that which menu item is clicked
                if(currentId == navHomeId){ // home menu item clicked
                    changeFragment(homeFragment,R.string.titleHome); // changing the screen to home screen
                } else if (currentId == navCustomerId) { // Customer menu item clicked
                    changeFragment(personFragment,R.string.titlePerson); // changing the screen to person screen
                }else if(currentId == navTransactionId){ // Transaction menu item clicked
                    changeFragment(transactionFragment,R.string.titleTransaction); // changing the screen to transaction screen
                } else if (currentId == navProductId) { // Product menu item clicked
                    changeFragment(productFragment,R.string.titleProduct); // changing the screen to product screen
                }else if (currentId == navProfileId) { // Profile menu item clicked
                    changeFragment(profileFragment,R.string.titleProfile); // changing the screen to profile screen
                }
                return null;
            }
        });

        // setup of drawer navigation
        // initializing Fragments object
        productFragment = new ProductFragment();
        personFragment = new PersonFragment();
        profileFragment = new ProfileFragment();
        homeFragment = new HomeFragment();
        transactionFragment = new transactionFragment();

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
                bind.meowBottom.setVisibility(View.GONE); // setting visibility gone to bottom nav

                // setting values to name, number and image of view
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
                // drawer is closed, so visible the bottom nav
                bind.meowBottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        bind.mainDrawerLayout.addDrawerListener(toggle); // adding listener to drawer
        toggle.syncState();

        // setting home screen as default screen when app is open
        changeFragment(homeFragment,R.string.titleHome);
    }

    // on drawer navigating item select listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // storing currently selected id

        if(id == R.id.nav_home) {
            changeFragment(homeFragment,R.string.titleHome); // change the screen to home screen
            bind.meowBottom.show(navHomeId,true); // displaying the home item is selected in bottom nav
        }
        else if (id == R.id.nav_profile) { // profile fragment selected
            changeFragment(profileFragment,R.string.titleProfile); // change the screen to profile screen
            bind.meowBottom.show(navProfileId,true);  // displaying the profile item is selected in bottom nav
        }
        else if (id == R.id.nav_manageProducts) { // Products fragment selected
            changeFragment(productFragment,R.string.titleProduct); // change the screen to product screen
            bind.meowBottom.show(navProductId,true); // displaying the product item is selected in bottom nav
        } else if (id == R.id.nav_category) { // category fragment
            // changing screen to category screen
            startActivity(new Intent(MainActivity.this, ManageCategory.class));
        }

        // unused right now
        else if (id == R.id.nav_settings) // user click on settings
            startActivity(new Intent(MainActivity.this,Settings.class));

        bind.mainDrawerLayout.closeDrawer(GravityCompat.START); // when any item is click after that close the drawer
        return true;
    }

    // change the fragment method
    private void changeFragment(Fragment fragment,int title){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_view, fragment).commit(); // changing the fragment that is given in argument
        bind.toolbar.setTitle(title); // setting title of the screen in action bar

        if(title == R.string.titleHome){
            bind.toolbar.setTitle(Params.getOwnerModel().getShop_name()); // setting title of the screen in action bar
        }
    }
}