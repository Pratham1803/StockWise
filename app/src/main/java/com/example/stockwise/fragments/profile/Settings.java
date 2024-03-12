package com.example.stockwise.fragments.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivitySettingsBinding;

import com.example.stockwise.fragments.profile.SettingsNavigation.PrivacyPolicy;

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
                CheckBox cb1 = customLayout.findViewById(R.id.cbPasword);
                CheckBox cb2 = customLayout.findViewById(R.id.cbBug);
                CheckBox cb3 = customLayout.findViewById(R.id.cbPerformance);
                CheckBox cb4 = customLayout.findViewById(R.id.cbSearch);
                CheckBox cb5 = customLayout.findViewById(R.id.cbElse);

                // add a button
                builder.setPositiveButton("Submit", (dialog, which) -> {
                    if(cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked() || cb5.isChecked()){
                        // If any one checkbox is checked then toast is display
                        Toast.makeText(Settings.this, "Report Submitted", Toast.LENGTH_SHORT).show();
                    }

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