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
import java.util.Arrays;

public class ProductFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Context context; // to store context
    private ScanOptions scanner; // scanner
    private String barCodeId; // setting bar code number
    private FragmentProductBinding bind; // bind view
    private ArrayList<ProductModel> arrAllProduct; // List of productModule class to store the details of multiple product
    private ArrayList<ProductModel> arrUnAvailableProduct; // List of productModule class to store the details of multiple product
    private ArrayList<ProductModel> arrAtReorderPointProduct; // List of productModule class to store the details of multiple product
    private ProductAdapter productAdapter; // object of product adapter

    ArrayList<String> filter = new ArrayList<>(Arrays.asList("All Products", "Unavailable Products", "Products at Reorder Point"));
    private ArrayAdapter adapterProductCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar

        // getting all products from firebase
        arrAllProduct = Params.getOwnerModel().getArrAllProduct();
        arrUnAvailableProduct = Params.getOwnerModel().getArrUnAvailableProduct();
        arrAtReorderPointProduct = Params.getOwnerModel().getArrAtReorderPointProduct();

        // set recycler view
        productAdapter = new ProductAdapter(arrAllProduct, context); // initializing productAdapter
        bind.recyclerProduct.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerProduct.setAdapter(productAdapter); // setting adapter to the recycler view

        // setting spinner
        bind.FilterSpinner.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter 
        // having the list of courses 
        adapterProductCategory = new ArrayAdapter(context, android.R.layout.simple_spinner_item, filter);

        // set simple layout resource file 
        // for each item of spinner 
        adapterProductCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterSpinner.setAdapter(adapterProductCategory);

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

        assert searchView != null;
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainToolbar.btnSearch(query.toLowerCase(), arrAllProduct, productAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    MainToolbar.btnSearch(newText.toLowerCase(), arrAllProduct, productAdapter);
                } else if (newText.length() == 0) {
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
            MainToolbar.searchProduct_Barcode(arrAllProduct, productAdapter, barCodeId);
            filter.add("Selected Product");
            adapterProductCategory.notifyDataSetChanged();
            bind.FilterSpinner.setSelection(3);
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String currentSelection = filter.get(i);

        if (currentSelection.equals(filter.get(0))) {
            productAdapter.setLocalDataSet(arrAllProduct);
            if(filter.size() == 4) {
                filter.remove(3);
                adapterProductCategory.notifyDataSetChanged();
            }
        } else if (currentSelection.equals(filter.get(1))) {
            productAdapter.setLocalDataSet(arrUnAvailableProduct);
        } else if (currentSelection.equals(filter.get(2))) {
            productAdapter.setLocalDataSet(arrAtReorderPointProduct);
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}