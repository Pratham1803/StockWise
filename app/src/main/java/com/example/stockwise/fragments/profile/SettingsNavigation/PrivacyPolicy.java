package com.example.stockwise.fragments.profile.SettingsNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicy extends AppCompatActivity {
    private ActivityPrivacyPolicyBinding bind; // declaring view binding
    private Context context; // declaring context
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        setContentView(bind.getRoot()); // setting view binding

        // setting action bar title
        setSupportActionBar(bind.toolbarPrivacyPolicy); // setting toolbar
        ActionBar actionBar = getSupportActionBar();  // getting action bar
        assert actionBar != null; // checking action bar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

        bind.scrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // setting layer type

        // setting on click listener on buttons
        bind.btnIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowIntroduction.getVisibility()==View.GONE){ // checking visibility of view
                    if(bind.ShowInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUpdates.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowSecurity.getVisibility()==View.VISIBLE) // checking visibility of view
                    {
                        bind.ShowSecurity.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowUseInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowUpdates.setVisibility(View.GONE); // setting visibility of view
                    }
                }

                if(bind.ShowIntroduction.getVisibility() == View.GONE) { // checking visibility of view
                    bind.ShowIntroduction.setVisibility(View.VISIBLE); // setting visibility of view
                } else { // if view is visible
                    bind.ShowIntroduction.setVisibility(View.GONE); // setting visibility of view
                }
            }
        });

        // setting on click listener on buttons
        bind.btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowInformation.getVisibility()==View.GONE){ // checking visibility of view
                    if(bind.ShowIntroduction.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUpdates.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowSecurity.getVisibility()==View.VISIBLE) // checking visibility of view
                    {
                        bind.ShowSecurity.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowIntroduction.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowUseInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowUpdates.setVisibility(View.GONE); // setting visibility of view
                    }
                }

                if(bind.ShowInformation.getVisibility() == View.GONE) { // checking visibility of view
                    bind.ShowInformation.setVisibility(View.VISIBLE); // setting visibility of view
                } else { // if view is visible
                    bind.ShowInformation.setVisibility(View.GONE); // setting visibility of view
                }
            }
        });

        // setting on click listener on buttons
        bind.btnUseInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowUseInformation.getVisibility()==View.GONE){ // checking visibility of view
                    if(bind.ShowInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||    // checking visibility of view
                            bind.ShowSecurity.getVisibility()==View.VISIBLE) // checking visibility of view
                    {
                        bind.ShowSecurity.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowIntroduction.setVisibility(View.GONE);     // setting visibility of view
                        bind.ShowUpdates.setVisibility(View.GONE); // setting visibility of view
                    }
                }
                if(bind.ShowUseInformation.getVisibility() == View.GONE) { // checking visibility of view
                    bind.ShowUseInformation.setVisibility(View.VISIBLE); // setting visibility of view
                } else { // if view is visible
                    bind.ShowUseInformation.setVisibility(View.GONE); // setting visibility of view
                }
            }
        });

        // setting on click listener on buttons
        bind.btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowSecurity.getVisibility()==View.GONE){ // checking visibility of view
                    if(bind.ShowInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUpdates.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE) // checking visibility of view
                    {
                        bind.ShowUpdates.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowUseInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowIntroduction.setVisibility(View.GONE); // setting visibility of view
                    }
                }
                if(bind.ShowSecurity.getVisibility() == View.GONE) { // checking visibility of view
                    bind.ShowSecurity.setVisibility(View.VISIBLE); // setting visibility of view
                } else { // if view is visible
                    bind.ShowSecurity.setVisibility(View.GONE); // setting visibility of view
                }
            }
        });

        // setting on click listener on buttons
        bind.btnUpdatees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowUpdates.getVisibility()==View.GONE){ // checking visibility of view
                    if(bind.ShowInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE || // checking visibility of view
                            bind.ShowSecurity.getVisibility()==View.VISIBLE) // checking visibility of view
                    {
                        bind.ShowIntroduction.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowUseInformation.setVisibility(View.GONE); // setting visibility of view
                        bind.ShowSecurity.setVisibility(View.GONE); // setting visibility of view
                    }
                }
                if(bind.ShowUpdates.getVisibility() == View.GONE) { // checking visibility of view
                    bind.ShowUpdates.setVisibility(View.VISIBLE); // setting visibility of view
                } else { // if view is visible
                    bind.ShowUpdates.setVisibility(View.GONE); // setting visibility of view
                }
            }
        });

    }// End of onCreate

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context); // calling back button click event
    }
}