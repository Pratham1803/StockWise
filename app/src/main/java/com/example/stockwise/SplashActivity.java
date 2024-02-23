package com.example.stockwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    Params p = new Params();

    // collect all user data and hen open the main activity
    public void openMainActivity(Context con){
            // setting the user id of current user
            Params.getOwnerModel().setId(Params.getAUTH().getCurrentUser().getUid().toString());
            Params.setREFERENCE(); // setting reference of the firebase database of current user

            // storing all the data of current user in OwnerModel class
            Params.getREFERENCE().addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                Params.getOwnerModel().setOwner_name(snapshot.child(Params.getOwnerName()).getValue().toString()); // set name of current user
                                Params.getOwnerModel().setShop_name(snapshot.child(Params.getShopName()).getValue().toString()); // set name of current user
                                Params.getOwnerModel().setId(snapshot.getKey().toString()); // user id of current user
                                Params.getOwnerModel().setContact_num(snapshot.child(Params.getContactNumber()).getValue().toString()); // contact number
                                Params.getOwnerModel().setPicture(snapshot.child(Params.getProfilePic()).getValue().toString()); // profile picture

                                con.startActivity(new Intent(con, MainActivity.class)); // start main activity
                                ((Activity) con).finish(); // finish this activity
                            }catch(Exception e){
                                Log.d("ErrorMsg", "openMainActivity: "+e);
                                Toast.makeText(SplashActivity.this, "Something Went Wrong!! try Again!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }
            );
    }

    // setting FCM token for firebase messaging
    private void getFCM_TOKEN() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Params.getOwnerModel().setFcm_token(s); // setting fcm token to the current user model
                        Params.getREFERENCE().child(Params.getOwnerModel().getId()).child(Params.getFcmToken()).setValue(s); // storing token in firebase database
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // delay of 1 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // is the user is not logged in, open login screen (LoginModule)
                if(Params.getAUTH().getCurrentUser() == null){
                    startActivity(new Intent(SplashActivity.this, LandingPage.class));
                    finish();
                }else{ // else open main activity with all data of user
                    openMainActivity(SplashActivity.this);
                }
            }
        }, 1000);
    }
}