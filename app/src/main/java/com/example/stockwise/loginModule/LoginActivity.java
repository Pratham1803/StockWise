package com.example.stockwise.loginModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.stockwise.MainActivity;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.SplashActivity;
import com.example.stockwise.model.OwnerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    // declaring required buttons and edittext views
    private EditText edNum,edOtp;
    private LinearLayout linearLayout_num;
    private LinearLayout linearLayout_otp;
    private Button btnSubmit,btnVerifyOtp;
    ProgressBar progressBar;
    private String verificationId;

    // defining Button Events of submit butto clicked
    public void btnSubmit_Clicked(View v){
        String edText = edNum.getText().toString().replace(" ","");

        // if user is entering mobile number
        if(edNum.getHint().toString().equals(getResources().getString(R.string.edUserNumHint))){
            if(edText.isEmpty())
                Toast.makeText(this, "Fill Mobile Num", Toast.LENGTH_SHORT).show();
            else{
                edNum.setInputType(InputType.TYPE_CLASS_NUMBER); // changing type of textbox as Number
                String countryCode = edText.substring(0,3);
                if(countryCode.charAt(0) != '+')
                    edText = "+91"+edText; // adding +91 with number

                progressBar.setVisibility(View.VISIBLE); // displaying progressbar
                sendVerificationCode(edText); // generating otp
            }
            // if user entering Name
        } else if (edNum.getHint().toString().equals(getResources().getString(R.string.edUserNameHint))) {

            Params.getOwnerModel().setId(Params.getAUTH().getCurrentUser().getUid()); // collecting current user UID
            Params.getOwnerModel().setName(edText); // collecting name from textbox
            Params.getOwnerModel().setContact_num(Params.getAUTH().getCurrentUser().getPhoneNumber()); // current user mobile number

            registerNewUser(); // registering new user in database
        }
    }

    // verify otp button clicked
    public void btnVerifyOtp_Clicked(View v){
        String otp = edOtp.getText().toString(); // getting otp from textbox
        if(otp.isEmpty()){
            Toast.makeText(this, "Enter The OTP", Toast.LENGTH_SHORT).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE); // displaying progressbar
            verifyCode(otp); // verifing Otp
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initilizing views
        btnSubmit = findViewById(R.id.btnSubmit);
        btnVerifyOtp = findViewById(R.id.btnOtpVerifiy);
        edNum = findViewById(R.id.edMobileNum);
        edOtp = findViewById(R.id.edOtpNum);
        progressBar = findViewById(R.id.progressBar);
        linearLayout_num = findViewById(R.id.layout_Num);
        linearLayout_otp = findViewById(R.id.layout_OTP);
    }

    // if there is new user then register in database
    private void registerNewUser(){

        // adding new user in database
        Params.getSTORAGE().child("person-icon.png").getDownloadUrl().addOnSuccessListener(
                new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Params.getOwnerModel().setPicture(uri.toString());
                                // user registered successfully then redirect to the main activity
                        Params.getDATABASE().getReference(Params.getOwnerModel().getId()).setValue(Params.getOwnerModel()).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // setting url of image
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                        ).addOnFailureListener(
                                // if error comes then give message
                                // set the textbox to enter new number
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Something Went Wrong!! Try Again", Toast.LENGTH_SHORT).show();
                                        edNum.setText("");
                                        edNum.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                                        edNum.setHint(R.string.edUserNumHint);
                                    }
                                }
                        );
                    }
                }
        );

    }

    // sign in the user in firebase with credentials
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        Params.getAUTH().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            Params.getOwnerModel().setId(task.getResult().getUser().getUid().toString());
                            Params.getOwnerModel().setContact_num(task.getResult().getUser().getPhoneNumber().toString());

                            // checking that given user is registered or not in the database
                            Params.setREFERENCE();
                            Params.getREFERENCE().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChildren()){
                                        SplashActivity splashActivity = new SplashActivity();
                                        splashActivity.openMainActivity(LoginActivity.this);
                                    }else {
                                        linearLayout_otp.setVisibility(View.INVISIBLE);
                                        linearLayout_num.setVisibility(View.VISIBLE);

                                        edNum.setText("");
                                        edNum.setInputType(InputType.TYPE_CLASS_TEXT); // chaging type to store name
                                        edNum.setHint(R.string.edUserNameHint); // changing hint of textbox to get name
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE); // invisible the progress bar
                    }
                });
    }

    // generating Otp
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
                                final String code = phoneAuthCredential.getSmsCode();
                                if(code != null){
                                    edOtp.setText(code);
                                    verifyCode(code);
                                }
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // verfication failed set message
                                Log.d("ErrorMsg", "onVerificationFailed: "+e);
                                Toast.makeText(LoginActivity.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                // verfication code sent by firebase
                                super.onCodeSent(s, forceResendingToken);

                                // un-displaying the enter number fields
                                linearLayout_num.setVisibility(View.INVISIBLE);

                                verificationId = s;
                                progressBar.setVisibility(View.INVISIBLE);

                                // visible the otp fields
                                linearLayout_otp.setVisibility(View.VISIBLE);
                            }
                        }).build();		 // OnVerificationStateChangedCallbacks

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

}