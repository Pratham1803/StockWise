package com.example.stockwise.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.stockwise.R;
import com.example.stockwise.SplashActivity;
import com.example.stockwise.databinding.ActivityLandingPageBinding;
import com.example.stockwise.loginModule.LoginActivity;
import com.example.stockwise.loginModule.LoginModule;

public class LandingPage extends AppCompatActivity {

    ActivityLandingPageBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        bind = ActivityLandingPageBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingPage.this, LoginModule.class));
                finish();
            }
        });

    }
}