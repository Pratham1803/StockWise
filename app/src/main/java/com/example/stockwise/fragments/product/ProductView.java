package com.example.stockwise.fragments.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.stockwise.MenuScreens.Settings;
import com.example.stockwise.ProfileNavigation.Account;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAccountBinding;
import com.example.stockwise.databinding.ActivityProductViewBinding;

public class ProductView extends AppCompatActivity {

    private ActivityProductViewBinding bind; // view binding
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        bind = ActivityProductViewBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

    }// End OnCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // popup menu for Edit and Delete the products
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_view_popup, menu);

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEditProduct);
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                return true;
            }
        });

        // popup menu for settings
        MenuItem btnSettings = menu.findItem(R.id.PopUpDeleteProduct);
        btnSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(ProductView.this, ProductFragment.class));
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}