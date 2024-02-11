package com.example.stockwise.loginModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.stockwise.MainActivity;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityLoginModuleBinding;
import com.example.stockwise.databinding.ActivityManageOtpBinding;

public class ManageOtp extends AppCompatActivity {

    ActivityManageOtpBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_otp);

        bind = ActivityManageOtpBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.EditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageOtp.this, LoginModule.class));
                finish();
            }
        });

        bind.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageOtp.this, MainActivity.class));
                finish();
            }
        });
    }
}