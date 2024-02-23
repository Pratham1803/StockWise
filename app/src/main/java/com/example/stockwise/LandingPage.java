package com.example.stockwise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.stockwise.databinding.ActivityLandingPageBinding;
import com.example.stockwise.loginModule.LoginModule;
import com.example.stockwise.loginModule.Registration;

public class LandingPage extends AppCompatActivity {
    ActivityLandingPageBinding bind; // declaring view binding
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityLandingPageBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        // user click on login button
        bind.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingPage.this, LoginModule.class)); // open login activity
                finish(); // ending current activity
            }
        });

        // user click on sign up activity
        bind.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingPage.this, LoginModule.class)); // open log in activity
                finish(); // ending current activity
            }
        });
    }
}