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
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context

        setContentView(bind.getRoot());
        // setting action bar title
        setSupportActionBar(bind.toolbarPrivacyPolicy);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        bind.scrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        bind.btnIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowIntroduction.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowUpdates.setVisibility(View.GONE);
                    }
                }

                if(bind.ShowIntroduction.getVisibility() == View.GONE) {
                    bind.ShowIntroduction.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowIntroduction.setVisibility(View.GONE);
                }

            }
        });

        bind.btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowInformation.getVisibility()==View.GONE){

                    if(bind.ShowIntroduction.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowIntroduction.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowUpdates.setVisibility(View.GONE);
                    }
                }

                if(bind.ShowInformation.getVisibility() == View.GONE) {
                    bind.ShowInformation.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowInformation.setVisibility(View.GONE);
                }
            }
        });

        bind.btnUseInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowUseInformation.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowIntroduction.setVisibility(View.GONE);
                        bind.ShowUpdates.setVisibility(View.GONE);
                    }
                }
                if(bind.ShowUseInformation.getVisibility() == View.GONE) {
                    bind.ShowUseInformation.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowUseInformation.setVisibility(View.GONE);
                }
            }
        });

        bind.btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowSecurity.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowUpdates.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowIntroduction.setVisibility(View.GONE);
                    }
                }
                if(bind.ShowSecurity.getVisibility() == View.GONE) {
                    bind.ShowSecurity.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowSecurity.setVisibility(View.GONE);
                }
            }
        });

        bind.btnUpdatees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowUpdates.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowIntroduction.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowSecurity.setVisibility(View.GONE);
                    }
                }
                if(bind.ShowUpdates.getVisibility() == View.GONE) {
                    bind.ShowUpdates.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowUpdates.setVisibility(View.GONE);
                }
            }
        });

    }// End of onCreate

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context);
    }
}