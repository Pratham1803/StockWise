package com.example.stockwise.fragments.product;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.stockwise.MainActivity;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.databinding.FragmentProfileBinding;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class ProductFragment extends Fragment {
    private Context context; // to store context
    private ScanOptions scanner; // scanner
    private String barCodeId; // setting bar code number
    private FragmentProductBinding bind; // bind view
    private ArrayList<ProductModel> arrProduct; // List of productModule class to store the details of multiple product
    private ProductAdapter productAdapter; // object of product adapter
    private int totalOutOfStockIteams = 0; // to store total out of stock items
    private int totalReorderPointReachedIteams = 0; // to store total reorder point reached items

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar

        // set recycler view
        arrProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        productAdapter = new ProductAdapter(arrProduct, context); // initializing productAdapter
        bind.recyclerProduct.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerProduct.setAdapter(productAdapter); // setting adapter to the recycler view

        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                arrProduct.clear(); // deleting all products from the list
                totalOutOfStockIteams = 0; // setting total out of stock items to 0
                totalReorderPointReachedIteams = 0; // setting total reorder point reached items to 0

                for (DataSnapshot post : snapshot.getChildren()) {
                    ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                    newProduct.setId(post.getKey().toString()); // setting user id of product to class object

                    if (newProduct.getIsOutOfStock().equals("true")) // if product is out of stock
                        totalOutOfStockIteams++; // increasing the count of out of stock items
                    if (newProduct.getIsReorderPointReached().equals("true")) // if product is out of stock
                        totalReorderPointReachedIteams++; // increasing the count of out of stock items

                    arrProduct.add(newProduct); // adding product in product's arraylist
                }
                productAdapter.notifyItemInserted(arrProduct.size()); // notifying the adapter that new products are added
                bind.txtOutOfStockNum.setText("Total Items Out of Stock : " + totalOutOfStockIteams); // setting total products count
                bind.txtReorderPointNum.setText("Total Items at Reorder Point : "+totalReorderPointReachedIteams);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return bind.getRoot();
    }

    // setting listener to, create action bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);

        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct);
        MenuItem btnSearch = menu.findItem(R.id.search);
        MenuItem btnScan = menu.findItem(R.id.scanner);
        SearchView searchView = (SearchView) btnSearch.getActionView();

        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                scanner = new ScanOptions();
                scanner.setPrompt("App is ready for use"); // title on scanner
                scanner.setBeepEnabled(true); // enable beep sound
                scanner.setOrientationLocked(true);
                scanner.setCaptureActivity(ScannerOrientation.class);
                bar.launch(scanner); // launching the scanner
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // setting on click lister in add product item
        btnAddProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // starting activity of add product screen
                startActivity(new Intent(context, AddProduct.class));
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            searchProduct_barcode();
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // search using Barcode num
    private void searchProduct_barcode() {
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrProduct.clear();
                for (DataSnapshot post : snapshot.getChildren()) {
                    if (post.child(Params.getBarCode()).getValue().toString().equals(barCodeId)) {
                        ProductModel productModel = post.getValue(ProductModel.class);
                        productModel.setId(post.getKey().toString());
                        arrProduct.add(productModel);
                        productAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}