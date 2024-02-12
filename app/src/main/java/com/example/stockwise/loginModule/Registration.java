package com.example.stockwise.loginModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.stockwise.MainActivity;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class Registration extends AppCompatActivity {
    private ActivityRegistrationBinding bind;
    private boolean isConditonCheckd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        isConditonCheckd = false;

        bind.cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isConditonCheckd = true;
                else
                    isConditonCheckd = false;
            }
        });
    }

    public void btnCreateAccount_Clicked(View view) {
        if(isConditonCheckd) {
            String shop_name = bind.edShopName.getText().toString();
            String owner_name = bind.edUsername.getText().toString();
            String emailId = bind.edEmail.getText().toString();

            if(shop_name.isEmpty() && owner_name.isEmpty() && emailId.isEmpty() )
                Toast.makeText(this, "Please Fill the Details!!", Toast.LENGTH_SHORT).show();
            else {
                bind.btnConfirm.setText("Please Wait..");

                Params.getOwnerModel().setOwner_name(owner_name);
                Params.getOwnerModel().setShop_name(shop_name);
                Params.getOwnerModel().setEmail_id(emailId);
                registerNewUser();
            }
        }else{
            Toast.makeText(this, "Check Terms & Conditions!!", Toast.LENGTH_SHORT).show();
        }
    }

    // if there is new user then register in database
    private void registerNewUser(){

        // adding new user in database
        Params.getSTORAGE().child("default_user.jpg").getDownloadUrl().addOnSuccessListener(
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
                                        startActivity(new Intent(Registration.this, MainActivity.class));
                                        finish();
                                    }
                                }
                        ).addOnFailureListener(
                                // if error comes then give message
                                // set the textbox to enter new number
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Registration.this, "Something Went Wrong!! Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }
        );

    }
}