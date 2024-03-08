package com.example.stockwise.MenuScreens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.stockwise.MainActivity;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivitySettingsBinding;

import com.example.stockwise.SettingsNavigation.PrivacyPolicy;

public class Settings extends AppCompatActivity {
    private ActivitySettingsBinding bind; // declaring view binding
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bind = ActivitySettingsBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        setContentView(bind.getRoot());

        // setting action bar title
        setSupportActionBar(bind.toolbarSetting);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        bind.LayoutReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("What went wrong?");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.report_alert, null);
                builder.setView(customLayout);

                // add a button
                builder.setPositiveButton("OK", (dialog, which) -> {
                    //todo code
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });

        bind.LayoutPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, PrivacyPolicy.class));
                //finish();
            }
        });

    }// End of OnCreate

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context);
    }
}