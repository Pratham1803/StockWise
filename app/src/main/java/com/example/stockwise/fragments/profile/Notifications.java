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
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        bind = ActivityNotificationsBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context

        setContentView(bind.getRoot());
        // setting action bar title
        setSupportActionBar(bind.toolbarNotifications);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        bind.switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bind.switchReorder.setChecked(true);
                    bind.switchOutOfStock.setChecked(true);
                } else {
                    bind.switchReorder.setChecked(false);
                    bind.switchOutOfStock.setChecked(false);
                }
            }
        });

        bind.switchReorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bind.switchOutOfStock.isChecked()){
                        bind.switchAll.setChecked(true);
                    }
                    else{
                        bind.switchAll.setChecked(false);
                    }
                } else{
                    if (bind.switchOutOfStock.isChecked() == false)
                    {
                        bind.switchReorder.setChecked(false);
                        bind.switchAll.setChecked(false);
                    }
                    else{
                        bind.switchReorder.setChecked(false);
                        bind.switchAll.setChecked(false);
                        bind.switchOutOfStock.setChecked(true);
                    }

                }
            }
        });

        bind.switchOutOfStock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bind.switchReorder.isChecked()){
                        bind.switchAll.setChecked(true);
                    }
                    else{
                        bind.switchAll.setChecked(false);
                    }
                } else{
                    if (bind.switchReorder.isChecked() == false)
                    {
                        bind.switchOutOfStock.setChecked(false);
                        bind.switchAll.setChecked(false);
                    }
                    else{
                        bind.switchOutOfStock.setChecked(false);
                        bind.switchAll.setChecked(false);
                        bind.switchReorder.setChecked(true);
                    }

                }
            }
        });

        bind.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bind.switchAll.setChecked(true);
                bind.switchReorder.setChecked(true);
                bind.switchOutOfStock.setChecked(true);
                bind.switchSell.setChecked(true);
                bind.switchPurchase.setChecked(true);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context);
    }
}