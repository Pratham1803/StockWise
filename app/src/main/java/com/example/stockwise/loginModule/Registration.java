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
        isConditonCheckd = false; // store the boolean, that terms accepted check box

        // setting check box check change listener on, Terms checked check box
        bind.cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    // check box is checked
                    isConditonCheckd = true;
                else
                    // check box is unchecked
                    isConditonCheckd = false;
            }
        });
    }

    // create account button clicked
    public void btnCreateAccount_Clicked(View view) {
        // if the terms accepted is checked then, store the data in database
        if(isConditonCheckd) {
            String shop_name = bind.edShopName.getText().toString(); // collecting shop name from edittext
            String owner_name = bind.edUsername.getText().toString(); // collecting owner name from edittext
            String emailId = bind.edEmail.getText().toString(); // collecting Email id from edittext

            // check that all the three fields are having values or not
            if(shop_name.isEmpty() && owner_name.isEmpty() && emailId.isEmpty() )
                // there is no value in one of the text box
                Toast.makeText(this, "Please Fill the Details!!", Toast.LENGTH_SHORT).show();
            else {
                bind.btnConfirm.setText("Please Wait.."); // changing the text of button

                Params.getOwnerModel().setOwner_name(owner_name); // setting the owner name to Param class field
                Params.getOwnerModel().setShop_name(shop_name); // setting the shop name to Param class field
                Params.getOwnerModel().setEmail_id(emailId); // setting the Email id to Param class field
                registerNewUser();
            }
        }else{
            // user not checked terms check box
            Toast.makeText(this, "Check Terms & Conditions!!", Toast.LENGTH_SHORT).show();
        }
    }

    // if there is new user then register in database
    private void registerNewUser(){
        // adding new user in database
        // storing image of default user image
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