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

import com.example.stockwise.loginModule.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    Params p = new Params();

    // collect all user data and hen open the main activity
    public void openMainActivity(Context con){
        try{
            Params.getOwnerModel().setId(Params.getAUTH().getCurrentUser().getUid().toString());
            Params.setREFERENCE();

            Params.getREFERENCE().addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Params.getOwnerModel().setName(snapshot.child(Params.getNAME()).getValue().toString());
                            Params.getOwnerModel().setId(snapshot.getKey().toString());
                            Params.getOwnerModel().setContact_num(snapshot.child(Params.getContactNumber()).getValue().toString());
                            //Params.getOwnerModel().setPicture(snapshot.child(Params.getProfilePic()).getValue().toString());

                            con.startActivity(new Intent(con, MainActivity.class));
                            ((Activity)con).finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }
            );
        }catch(Exception e){
            Log.d("ErrorMsg", "openMainActivity: "+e);
            Toast.makeText(this, "Something Went Wrong!! try Again!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFCM_TOKEN() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Params.getOwnerModel().setFcm_token(s);
                        Params.getREFERENCE().child(Params.getOwnerModel().getId()).child(Params.getFcmToken()).setValue(s);
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
                // is the user is not logged in, open login screen
                if(Params.getAUTH().getCurrentUser() == null){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }else{ // else open main activity all data of user
                    openMainActivity(SplashActivity.this);
                }
            }
        }, 1000);
    }
}