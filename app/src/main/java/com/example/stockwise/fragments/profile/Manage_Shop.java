package com.example.stockwise.fragments.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityManageShopBinding;
import com.example.stockwise.model.OwnerModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Manage_Shop extends AppCompatActivity {
    private ActivityManageShopBinding binding; // binding object
    private Context context; // context object
    private boolean isEditable = false; // editable flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageShopBinding.inflate(getLayoutInflater()); // inflate the layout
        context = binding.getRoot().getContext(); // get context
        setContentView(binding.getRoot()); // set the view

        setSupportActionBar(binding.toolbarAccount); // setting toolbar
        ActionBar actionBar = getSupportActionBar(); // actionbar object
        assert actionBar != null; // check if actionbar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

        // setting all fields non-editable
        reset();

        // set conditions
        binding.EditGst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.EditGst.getText().toString().length() < 15) { // check if gst number is less than 15 characters
                    binding.EditGst.setError("GST number should be 15 characters"); // set error
                }
                if (binding.EditGst.getText().toString().length() > 15) { // check if gst number is greater than 15 characters
                    binding.EditGst.setError("GST number should be 15 characters"); // set error
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // set conditions
        binding.EditCin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.EditCin.getText().toString().length() < 21) { // check if cin number is less than 21 characters
                    binding.EditCin.setError("CIN number should be 21 characters"); // set error
                }
                if (binding.EditCin.getText().toString().length() > 21) { // check if cin number is greater than 21 characters
                    binding.EditCin.setError("CIN number should be 21 characters"); // set error
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // inserting values in fields according to user
        binding.EdShopName.setText(Params.getOwnerModel().getShop_name()); // set shop name
        Params.getREFERENCE().addListenerForSingleValueEvent( // get reference
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // check if snapshot exists
                        if (snapshot.exists()) {
                            if (snapshot.child(Params.getGstNum()).exists()) // check if gst number exists
                                binding.EditGst.setText(snapshot.child(Params.getGstNum()).getValue().toString()); // set gst number
                            if (snapshot.child(Params.getADDRESS()).exists()) // check if address exists
                                binding.EditShopAddress.setText(snapshot.child(Params.getADDRESS()).getValue().toString()); // set address
                            if (snapshot.child(Params.getCinNum()).exists()) // check if cin number exists
                                binding.EditCin.setText(snapshot.child(Params.getCinNum()).getValue().toString()); // set cin number
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
                    }
                }
        );

        // update shop details
        binding.btnUpdateShopDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if fields are empty
                if (binding.EditShopAddress.getText().toString().isEmpty() || binding.EditGst.getText().toString().isEmpty() || binding.EditCin.getText().toString().isEmpty() || binding.EdShopName.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if gst number is not 15 characters
                if(binding.EditGst.getText().toString().length() != 15){
                    binding.EditGst.setError("GST number should be 15 characters"); // set error
                    return;
                }

                // check if cin number is not 21 characters
                if(binding.EditCin.getText().toString().length() != 21){
                    binding.EditCin.setError("CIN number should be 21 characters"); // set error
                    return;
                }

                // update shop details
                HashMap<String, Object> map = new HashMap<>(); // hashmap object
                map.put(Params.getADDRESS(), binding.EditShopAddress.getText().toString()); // set address
                map.put(Params.getGstNum(), binding.EditGst.getText().toString()); // set gst number
                map.put(Params.getCinNum(), binding.EditCin.getText().toString()); // set cin number
                map.put(Params.getShopName(), binding.EdShopName.getText().toString()); // set shop name

                // update shop details
                Params.getREFERENCE().updateChildren(map).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // show success dialog
                                SweetAlertDialog sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Manage Shop","Shop details updated successfully");
                                sweetAlertDialog.setOnDismissListener(dialog -> {
                                    sweetAlertDialog.dismissWithAnimation(); // dismiss dialog
                                    finish(); // finish activity
                                });
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to update shop details", Toast.LENGTH_SHORT).show();
                        Log.d("ErrorMsg", "onFailure: "+e.getMessage());
                    }
                }
                );
                isEditable = !isEditable; // set editable flag
                reset(); // reset all fields
            }
        });
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context); // back button clicked
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_shop_edit, menu); // inflate menu

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEditShop); // find menu item

        // on menu item click listener for edit
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                isEditable = !isEditable; // set editable flag
                reset(); // reset all fields
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void reset() {
        // reset all fields
        binding.EditShopAddress.setEnabled(isEditable); // set address editable
        binding.EdShopName.setEnabled(isEditable); // set shop name editable
        binding.EditCin.setEnabled(isEditable); // set cin number editable
        binding.EditGst.setEnabled(isEditable); // set gst number editable

        // check if editable flag is true
        if (isEditable) // set button visible
            binding.btnUpdateShopDetails.setVisibility(View.VISIBLE);
        else // set button gone
            binding.btnUpdateShopDetails.setVisibility(View.GONE);
    }
}