package com.example.stockwise.fragments.profile.SettingsNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAboutBinding;
import com.example.stockwise.databinding.ActivityPrivacyPolicyBinding;

public class about extends AppCompatActivity {
    private ActivityAboutBinding bind; // declaring view binding
    private Context context; // declaring context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityAboutBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context

        setContentView(bind.getRoot()); // setting view binding
        // setting action bar title
        setSupportActionBar(bind.toolbarAboutUs); // setting toolbar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking if action bar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button
    }

    // back button function
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context); // calling back button function
    }
}