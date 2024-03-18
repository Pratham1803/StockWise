package com.example.stockwise.fragments.transaction;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.databinding.ActivitySelectItemsBinding;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.stockwise.R;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class Select_Items extends AppCompatActivity {
    private ActivitySelectItemsBinding bind; // declaring view binding
    private Context context; // to store context
    private ScanOptions scanner; // scanner
    private String barCodeId; // to store barcode id
    private ArrayList<ProductModel> arrProduct; // to store product data
    private ProductSellAdapter productSellAdapter; // to store adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_items);

        bind = ActivitySelectItemsBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());
        context = bind.getRoot().getContext(); // initializing context

        // setting action bar title
        setSupportActionBar(bind.toolbarSelectItem);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        bind.btnScanItems.setOnClickListener(v -> ScanItem());

        // setup recycler view
        arrProduct = new ArrayList<>();
        productSellAdapter = new ProductSellAdapter(arrProduct, context);
        bind.recyclerViewSelectItems.setAdapter(productSellAdapter);
        bind.recyclerViewSelectItems.setLayoutManager(new LinearLayoutManager(context));
    }// End OnCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }

    private void ScanItem() {
//        if (bind.spPerson.getSelectedItemPosition() == 0) {
//            Toast.makeText(context, "Please select customer name", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (bind.DateShow.getText().toString().equals("Select Date")) {
//            Toast.makeText(context, "Please select transaction date", Toast.LENGTH_SHORT).show();
//            return;
//        }

        scanner = MainToolbar.getScanner();
        bar.launch(scanner);
    }

    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            Params.getREFERENCE().child(Params.getPRODUCT()).child(barCodeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ProductModel productModel = snapshot.getValue(ProductModel.class);
                    arrProduct.add(0,productModel);
                    productSellAdapter.notifyItemInserted(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

}