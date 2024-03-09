package com.example.stockwise.fragments.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityProductViewBinding;
import com.example.stockwise.model.ProductModel;

public class ProductView extends AppCompatActivity {
    private ActivityProductViewBinding bind; // view binding
    private Context context;
    private ProductModel parentProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        bind = ActivityProductViewBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // getting context of the view
        setContentView(bind.getRoot());

        // getting the product details from the intent
        Intent intent = getIntent();
        parentProduct = (ProductModel) intent.getSerializableExtra("productObj");

        // setting Action bar
        bind.toolbarProductView.setTitle(parentProduct.getName());
        setSupportActionBar(bind.toolbarProductView); // setting action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        // setting product details
        bind.txtProductViewName.setText(parentProduct.getName());
        bind.txtSerialNumberView.setText(parentProduct.getBarCodeNum());
        Glide.with(context).load(parentProduct.getPicture()).into(bind.imgProductImageView);
        bind.txtSellingPrice.setText("Rs. "+parentProduct.getSale_price());
        bind.txtPurchasePrice.setText("Rs. "+parentProduct.getPurchase_price());
        bind.txtShopNameShow.setText(Params.getOwnerModel().getShop_name());
        bind.txtItemCategory.setText(parentProduct.getCategory());
        bind.txtSkuShow.setText(parentProduct.getBarCodeNum());
        bind.txtCurrentStockShow.setText(parentProduct.getCurrent_stock());
        bind.txtReorderPoint.setText(parentProduct.getReorder_point());
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context);
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
        MenuItem btnDelete = menu.findItem(R.id.PopUpDeleteProduct);
        btnDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}