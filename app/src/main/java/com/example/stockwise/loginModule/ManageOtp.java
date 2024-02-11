package com.example.stockwise.loginModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.stockwise.MainActivity;
import com.example.stockwise.Params;
import com.example.stockwise.SplashActivity;
import com.example.stockwise.databinding.ActivityManageOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ManageOtp extends AppCompatActivity {
    private ActivityManageOtpBinding bind; // declaring view binding
    private String mobileNum; // to store user mobile number
    private String verificationId; // to store otp verification id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityManageOtpBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        // collecting mobile number and verification id from loginModule Activity intent
        // and storing in variables
        this.mobileNum = getIntent().getStringExtra("MOBILE_NUM");
        this.verificationId = getIntent().getStringExtra("VERIFICATION_ID");

        bind.txtNumShow.setText(String.format("Hi, %s", mobileNum)); // setting textview text

        // setting on click listener on verify button
        bind.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = bind.edOTP.getText().toString(); // getting otp from textbox

                // when user click on button but otp is not entered by user
                if(otp.isEmpty()){
                    Toast.makeText(ManageOtp.this, "Enter The OTP", Toast.LENGTH_SHORT).show();
                }
                else{
                    bind.progrssBarOtp.setVisibility(View.VISIBLE); // displaying progressbar
                    verifyCode(otp); // verifying Otp
                }
            }
        });

        // when user click on edit number image open activity loginModule to re-enter the mobile number
        bind.imgEditNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling LoginModule activity to change the mobile number
                Intent intent = new Intent(ManageOtp.this,LoginModule.class);
                intent.putExtra("MOBILE_NUM",mobileNum); // giving mobile number to the activity
                startActivity(intent);
            }
        });
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
                                        startActivity(new Intent(ManageOtp.this, MainActivity.class));
                                        finish();
                                    }
                                }
                        ).addOnFailureListener(
                                // if error comes then give message
                                // set the textbox to enter new number
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ManageOtp.this, "Something Went Wrong!! Try Again", Toast.LENGTH_SHORT).show();
                                        bind.edOTP.setText("");
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
                // when process is complete implement this method
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // setting user id and contact number to the user model
                            Params.getOwnerModel().setId(task.getResult().getUser().getUid().toString());
                            Params.getOwnerModel().setContact_num(task.getResult().getUser().getPhoneNumber().toString());

                            // setting user reference to work with firebase
                            Params.setREFERENCE();

                            // checking in database that this user id is available or not in database
                            Params.getREFERENCE().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChildren()){
                                        // user id is available
                                        // opening main activity with all the user details of current user who logged in
                                        SplashActivity splashActivity = new SplashActivity();
                                        splashActivity.openMainActivity(ManageOtp.this);
                                    }else {
                                        // if user is not registered then register from here
                                        /*
                                        edNum.setText("");
                                        edNum.setInputType(InputType.TYPE_CLASS_TEXT); // chaging type to store name
                                        edNum.setHint(R.string.edUserNameHint); // changing hint of textbox to get name*/
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(ManageOtp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        bind.progrssBarOtp.setVisibility(View.INVISIBLE); // invisible the progress bar
                    }
                });
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