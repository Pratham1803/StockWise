package com.example.stockwise.fragments.product;

import android.app.Activity;
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
    private int REQUEST_CODE_ADD_PRODUCT = 1; // request code for add product

    // filter spinner
    ArrayList<String> filter = new ArrayList<>(Arrays.asList("All Products", "Unavailable Products", "Products at Reorder Point"));
    private ArrayAdapter adapterProductCategory; // adapter for spinner

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar

        // getting all products from firebase
        arrAllProduct = Params.getOwnerModel().getArrAllProduct(); // getting all product from owner model
        arrUnAvailableProduct = Params.getOwnerModel().getArrUnAvailableProduct(); // getting all product from owner model
        arrAtReorderPointProduct = Params.getOwnerModel().getArrAtReorderPointProduct(); // getting all product from owner model

        // set recycler view
        productAdapter = new ProductAdapter(arrAllProduct, context); // initializing productAdapter
        bind.recyclerProduct.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerProduct.setAdapter(productAdapter); // setting adapter to the recycler view

        // setting spinner
        bind.FilterSpinner.setOnItemSelectedListener(this); // setting on item selected listener

        // Create the instance of ArrayAdapter 
        // having the list of courses 
        adapterProductCategory = new ArrayAdapter(context, android.R.layout.simple_spinner_item, filter); // initializing adapter for spinner

        // set simple layout resource file 
        // for each item of spinner 
        adapterProductCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // setting drop down view resource

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterSpinner.setAdapter(adapterProductCategory); // setting adapter to spinner

        return bind.getRoot();
    }

    // setting listener to, create action bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu); // setting toolbar menu

        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct); // getting add product button from menu
        MenuItem btnSearch = menu.findItem(R.id.search); // getting search button from menu
        MenuItem btnScan = menu.findItem(R.id.scanner); // getting scanner button from menu
        SearchView searchView = (SearchView) btnSearch.getActionView(); // getting search view from search button

        assert searchView != null; // checking search view is not null
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text); // getting search edit text
        searchEditText.setTextColor(getResources().getColor(R.color.white)); // setting text color of search edit text
        searchEditText.setHintTextColor(getResources().getColor(R.color.white)); // setting hint color of search edit text

        // setting on click lister in scanner item
        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                scanner = MainToolbar.getScanner(); // getting scanner
                bar.launch(scanner); // launching scanner
                return true;
            }
        });

        // setting on query text listener in search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searching product
                MainToolbar.btnSearch(query.toLowerCase(), arrAllProduct, productAdapter); // searching product
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searching product
                if (newText.length() > 1) { // if length of text is greater than 1
                    MainToolbar.btnSearch(newText.toLowerCase(), arrAllProduct, productAdapter); // searching product
                } else if (newText.length() == 0) { // if length of text is 0
                    productAdapter.setLocalDataSet(arrAllProduct); // setting all product to adapter
                }
                return true;
            }
        });

        // setting on click lister in add product item
        btnAddProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // starting activity of add product screen
                Intent intent = new Intent(context, AddProduct.class); // initializing intent
                startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT); // starting activity for result
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    // on activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check if the request code is same as what is passed  here it is 1
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PRODUCT && resultCode == Activity.RESULT_OK) { // if request code is same as passed
            // Refresh your product list here
            productAdapter.notifyDataSetChanged(); // notify adapter
        }
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) { // if scanner has some result
            barCodeId = result.getContents(); // collect the barcode number and store it
            MainToolbar.searchProduct_Barcode(arrAllProduct, productAdapter, barCodeId); // search product by barcode
            filter.add("Selected Product"); // add selected product to filter
            adapterProductCategory.notifyDataSetChanged(); // notify adapter
            bind.FilterSpinner.setSelection(3); // set selected product to spinner
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // on item selected listener
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String currentSelection = filter.get(i); // getting current selection

        if (currentSelection.equals(filter.get(0))) { // if current selection is all product
            productAdapter.setLocalDataSet(arrAllProduct); // set all product to adapter
            if(filter.size() == 4) { // if filter size is 4
                filter.remove(3); // remove selected product from filter
                adapterProductCategory.notifyDataSetChanged(); // notify adapter
            }
        } else if (currentSelection.equals(filter.get(1))) { // if current selection is unavailable product
            productAdapter.setLocalDataSet(arrUnAvailableProduct); // set unavailable product to adapter
        } else if (currentSelection.equals(filter.get(2))) { // if current selection is product at reorder point
            productAdapter.setLocalDataSet(arrAtReorderPointProduct); // set product at reorder point to adapter
        }
        productAdapter.notifyDataSetChanged(); // notify adapter
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}