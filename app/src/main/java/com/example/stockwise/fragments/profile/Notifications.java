package com.example.stockwise.fragments.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAboutBinding;
import com.example.stockwise.databinding.ActivityNotificationsBinding;

public class Notifications extends AppCompatActivity {
    private ActivityNotificationsBinding bind; // declaring view binding
    private Context context; // declaring context

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityNotificationsBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context

        setContentView(bind.getRoot()); // setting view
        // setting action bar title
        setSupportActionBar(bind.toolbarNotifications); // setting custom toolbar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking if action bar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

        // setting switch button
        bind.switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // if switch is checked
                    bind.switchReorder.setChecked(true); // set reorder switch to true
                    bind.switchOutOfStock.setChecked(true); // set out of stock switch to true
                } else { // if switch is not checked
                    bind.switchReorder.setChecked(false); // set reorder switch to false
                    bind.switchOutOfStock.setChecked(false); // set out of stock switch to false
                }
            }
        });

        // setting switch button
        bind.switchReorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // if switch is checked
                    if (bind.switchOutOfStock.isChecked()){ // if out of stock switch is checked
                        bind.switchAll.setChecked(true); // set all switch to true
                    }
                    else{ // if out of stock switch is not checked
                        bind.switchAll.setChecked(false); // set all switch to false
                    }
                } else{ // if switch is not checked
                    // if out of stock switch is false
                    if (bind.switchOutOfStock.isChecked() == false) {
                        bind.switchReorder.setChecked(false); // set reorder switch to false
                        bind.switchAll.setChecked(false); // set all switch to false
                    }
                    else{ // if out of stock switch is checked
                        bind.switchReorder.setChecked(false); // set reorder switch to false
                        bind.switchAll.setChecked(false); // set all switch to false
                        bind.switchOutOfStock.setChecked(true); // set out of stock switch to true
                    }
                }
            }
        });

        // setting switch button
        bind.switchOutOfStock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // if switch is checked
                    if (bind.switchReorder.isChecked()){ // if reorder switch is checked
                        bind.switchAll.setChecked(true); // set all switch to true
                    }
                    else{ // if reorder switch is not checked
                        bind.switchAll.setChecked(false); // set all switch to false
                    }
                } else{ // if switch is not checked
                    // if reorder switch is false
                    if (bind.switchReorder.isChecked() == false) {
                        bind.switchOutOfStock.setChecked(false); // set out of stock switch to false
                        bind.switchAll.setChecked(false); // set all switch to false
                    }
                    else{ // if reorder switch is checked
                        bind.switchOutOfStock.setChecked(false); // set out of stock switch to false
                        bind.switchAll.setChecked(false); // set all switch to false
                        bind.switchReorder.setChecked(true); // set reorder switch to true
                    }
                }
            }
        });

        // setting switch button for sell
        bind.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bind.switchAll.setChecked(true); // set all switch to true
                bind.switchReorder.setChecked(true); // set reorder switch to true
                bind.switchOutOfStock.setChecked(true); // set out of stock switch to true
                bind.switchSell.setChecked(true); // set sell switch to true
                bind.switchPurchase.setChecked(true); // set purchase switch to true
            }
        });
    }

    // back button click event
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context); // calling back button click event
    }
}