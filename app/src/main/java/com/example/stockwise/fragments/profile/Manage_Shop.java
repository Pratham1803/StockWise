package com.example.stockwise.fragments.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    private ActivityManageShopBinding binding;
    private Context context;
    private boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageShopBinding.inflate(getLayoutInflater());
        context = binding.getRoot().getContext();
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarAccount);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        // setting all fields non-editable
        reset();

        // inserting values in fields according to user
        binding.EdShopName.setText(Params.getOwnerModel().getShop_name());
        Params.getREFERENCE().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child(Params.getGstNum()).exists())
                                binding.EditGst.setText(snapshot.child(Params.getGstNum()).getValue().toString());
                            if (snapshot.child(Params.getADDRESS()).exists())
                                binding.EditShopAddress.setText(snapshot.child(Params.getADDRESS()).getValue().toString());
                            if (snapshot.child(Params.getCinNum()).exists())
                                binding.EditCin.setText(snapshot.child(Params.getCinNum()).getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
                    }
                }
        );
        binding.btnUpdateShopDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(Params.getADDRESS(), binding.EditShopAddress.getText().toString());
                map.put(Params.getGstNum(), binding.EditGst.getText().toString());
                map.put(Params.getCinNum(), binding.EditCin.getText().toString());
                map.put(Params.getShopName(), binding.EdShopName.getText().toString());

                Params.getREFERENCE().updateChildren(map).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SweetAlertDialog sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Manage Shop","Shop details updated successfully");
                                sweetAlertDialog.setOnDismissListener(dialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
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
                isEditable = !isEditable;
                reset();
            }
        });
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_shop_edit, menu);

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEditShop);
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                isEditable = !isEditable;
                reset();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void reset() {
        // reset all fields
        binding.EditShopAddress.setEnabled(isEditable);
        binding.EdShopName.setEnabled(isEditable);
        binding.EditCin.setEnabled(isEditable);
        binding.EditGst.setEnabled(isEditable);

        if (isEditable)
            binding.btnUpdateShopDetails.setVisibility(View.VISIBLE);
        else
            binding.btnUpdateShopDetails.setVisibility(View.GONE);
    }
}