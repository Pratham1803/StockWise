package com.example.stockwise.loginModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.stockwise.Params;
import com.example.stockwise.databinding.ActivityLoginModuleBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginModule extends AppCompatActivity {
    private ActivityLoginModuleBinding bind; // setting view binding
    private String verificationCodeId; // to store verification code id
    private String mobileNum; // to store mobile num
    private String tempMobileNum; // to store temp mobile num collect from intent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityLoginModuleBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        // when user click edit number from otp screen then it has mobile number as intent value
        // get the number and show in text box
        if(getIntent().getStringExtra("MOBILE_NUM") != null){
            tempMobileNum = getIntent().getStringExtra("MOBILE_NUM"); // initializing num
            verificationCodeId = getIntent().getStringExtra("VERIFICATION_ID"); // initializing verification id
            bind.countryCode.setCountryForPhoneCode(Integer.parseInt(tempMobileNum.substring(0,3))); // setting country code
            bind.edMobile.setText(tempMobileNum.substring(3)); // setting mobile number in textBox
        }

        // next button click event
        // when the otp sent to user then redirect to next screen of ManageOtp
        bind.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.countryCode.registerCarrierNumberEditText(bind.edMobile); // collect country code
                bind.btnNext.setText("Please Wait..");
                bind.progressBarLoginNum.setVisibility(View.VISIBLE); // displaying progressbar

                mobileNum = bind.countryCode.getFullNumberWithPlus().replace(" ",""); // getting full contact number

                if(!mobileNum.equals(tempMobileNum))
                    sendVerificationCode(mobileNum); // calling method to initiate Otp
                else {
                    Intent intent = new Intent(LoginModule.this, ManageOtp.class);
                    intent.putExtra("MOBILE_NUM",mobileNum); // giving mobile number to next activity
                    intent.putExtra("VERIFICATION_ID",verificationCodeId); // giving verification id
                    startActivity(intent); // starting ManageOtp Activity
                }
            }
        });
    }

    // Generate Otp
    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(Params.getAUTH())
                        .setPhoneNumber(number)		 // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)				 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // verification completed set otp
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // verification failed set message
                                Log.d("ErrorMsg", "onVerificationFailed: "+e);
                                Toast.makeText(LoginModule.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                // verification code sent by firebase to user mobile number
                                super.onCodeSent(s, forceResendingToken);

                                verificationCodeId = s; // initilizing otp id for verification of otp
                                bind.progressBarLoginNum.setVisibility(View.GONE); // undisplayed progressbar

                                // opening ManageOtp activity
                                Intent intent = new Intent(LoginModule.this, ManageOtp.class);
                                intent.putExtra("MOBILE_NUM",mobileNum); // giving mobile number to next activity
                                intent.putExtra("VERIFICATION_ID",verificationCodeId); // giving verification id
                                startActivity(intent); // starting ManageOtp Activity

                                ((Activity) LoginModule.this).finish(); // finishing this activity
                            }
                        }).build();		 // OnVerificationStateChangedCallbacks

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}