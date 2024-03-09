package com.example.stockwise.fragments.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.SearchView;

import android.widget.EditText;
import android.widget.Toast;

import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class ProductFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Context context; // to store context
    private ScanOptions scanner; // scanner
    private String barCodeId; // setting bar code number
    private FragmentProductBinding bind; // bind view
    private ArrayList<ProductModel> arrAllProduct; // List of productModule class to store the details of multiple product
    private ArrayList<ProductModel> arrUnAvailableProduct; // List of productModule class to store the details of multiple product
    private ArrayList<ProductModel> arrAtReorderPointProduct; // List of productModule class to store the details of multiple product
    private ProductAdapter productAdapter; // object of product adapter

    String[] filter = {"All Products","Unavailable Products","Products at Reorder Point","Selected Products"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar

        // set recycler view
        arrAllProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        arrUnAvailableProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        arrAtReorderPointProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        productAdapter = new ProductAdapter(arrAllProduct, context); // initializing productAdapter
        bind.recyclerProduct.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerProduct.setAdapter(productAdapter); // setting adapter to the recycler view

        // setting spinner
        bind.FilterSpinner.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter 
        // having the list of courses 
        ArrayAdapter ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, filter);

        // set simple layout resource file 
        // for each item of spinner 
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterSpinner.setAdapter(ad);

        dbGetAllProducts();
        return bind.getRoot();
    }

    // get all products from firebase
    private void dbGetAllProducts(){
        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                arrAllProduct.clear(); // deleting all products from the list
                arrUnAvailableProduct.clear(); // deleting all products from the list
                arrAtReorderPointProduct.clear(); // deleting all products from the list

                for (DataSnapshot post : snapshot.getChildren()) {
                    ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                    newProduct.setId(post.getKey().toString()); // setting user id of product to class object

                    arrAllProduct.add(newProduct); // adding product in product's arraylist

                    if(newProduct.getIsOutOfStock().equals("true")){
                        arrUnAvailableProduct.add(newProduct);
                    }
                    if(newProduct.getIsReorderPointReached().equals("true")){
                        arrAtReorderPointProduct.add(newProduct);
                    }
                }
                productAdapter.notifyDataSetChanged(); // notifying the adapter that new products are added
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: "+error.getMessage());
            }
        });
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

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                scanner = MainToolbar.getScanner();
                bar.launch(scanner);
                return true;
            }
        });

        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainToolbar.btnSearch(query.toLowerCase(),arrAllProduct,productAdapter);
                Log.d("SuccessMsg", "onQueryTextSubmit: Text = "+query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    MainToolbar.btnSearch(newText.toLowerCase(), arrAllProduct, productAdapter);
                    Log.d("SuccessMsg", "onQueryTextChange: Text = "+newText);
                } else if (newText.length() == 0){
                    productAdapter.setLocalDataSet(arrAllProduct);
                }
                return true;
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
            MainToolbar.searchProduct_Barcode(arrAllProduct,productAdapter,barCodeId);
            bind.FilterSpinner.setSelection(3);
//            searchProduct_barcode();
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String currentSelection = filter[i];

        if(currentSelection.equals(filter[0])){
            productAdapter.setLocalDataSet(arrAllProduct);
        }else if(currentSelection.equals(filter[1])){
            productAdapter.setLocalDataSet(arrUnAvailableProduct);
        }else if (currentSelection.equals(filter[2])){
            productAdapter.setLocalDataSet(arrAtReorderPointProduct);
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}