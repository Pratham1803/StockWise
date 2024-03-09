package com.example.stockwise.fragments.category;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityProductListBinding;
import com.example.stockwise.fragments.product.AddProduct;
import com.example.stockwise.fragments.product.ProductAdapter;
import com.example.stockwise.fragments.product.ScannerOrientation;
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityProductListBinding bind;
    private Context context;
    private ScanOptions scanner; // scanner
    private String barCodeId; // setting bar code number
    private ProductAdapter productAdapter;
    private ArrayList<ProductModel> arrAllProduct;
    private ArrayList<ProductModel> arrUnAvailableProduct;
    private ArrayList<ProductModel> arrAtReorderPointProduct;
    private ArrayList<String> arrProductId;
    private CategoryModel parentCategory;
    String[] filter = {"All Products","Unavailable Products","Products at Reorder Point","Selected Products"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityProductListBinding.inflate(getLayoutInflater());
        context = bind.getRoot().getContext();
        setContentView(bind.getRoot());
        arrProductId = new ArrayList<>();

        // collecting category details
        parentCategory = new CategoryModel();
        parentCategory.setId(getIntent().getStringExtra("category_id"));
        parentCategory.setName(getIntent().getStringExtra("category_name"));

        // setting action bar title
        setSupportActionBar(bind.toolbarCategory);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(parentCategory.getName());
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        arrAllProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        arrUnAvailableProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        arrAtReorderPointProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        productAdapter = new ProductAdapter(arrAllProduct, context); // initializing productAdapter
        bind.recyclerProductCategory.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerProductCategory.setAdapter(productAdapter); // setting adapter to the recycler view

        // setting spinner
        bind.FilterSpinnerCategory.setOnItemSelectedListener(this);

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, filter);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterSpinnerCategory.setAdapter(ad);

        dbGetAllProductsId();
    }

    private void dbGetAllProductsId() {
        Params.getREFERENCE().child(Params.getCATEGORY()).child(parentCategory.getId()).child(Params.getArrProducts()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            arrProductId.add(post.getValue().toString());
                        }
                        dbGetAllProducts();
                    }
                });
    }

    private void dbGetAllProducts() {
        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                arrAllProduct.clear(); // deleting all products from the list
                arrUnAvailableProduct.clear(); // deleting all products from the list
                arrAtReorderPointProduct.clear(); // deleting all products from the list

                for (DataSnapshot post : snapshot.getChildren()) {
                    if(!arrProductId.contains(post.getKey().toString())){
                        continue;
                    }
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

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,context);
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            MainToolbar.searchProduct_Barcode(arrAllProduct,productAdapter,barCodeId);
            bind.FilterSpinnerCategory.setSelection(3);
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // setting listener to, create action bar
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct);
        btnAddProduct.setVisible(false); // hiding add product button from action bar
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
                bar.launch(scanner); // launching the scanner
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

        return super.onCreateOptionsMenu(menu);
    }

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
    public void onNothingSelected(AdapterView<?> parent) {}
}