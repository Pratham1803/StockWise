package com.example.stockwise.loginModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.stockwise.R;
import com.example.stockwise.SplashActivity;
import com.example.stockwise.databinding.ActivityLandingPageBinding;
import com.example.stockwise.databinding.ActivityLoginModuleBinding;
import com.example.stockwise.model.LandingPage;

public class LoginModule extends AppCompatActivity {

    ActivityLoginModuleBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_module);

        bind = ActivityLoginModuleBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bind.btnNext.setText("PLEASE WAIT...");
                startActivity(new Intent(LoginModule.this, ManageOtp.class));
                finish();
            }
        });


    }
}