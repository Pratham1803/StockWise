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
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class ProductList extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityProductListBinding bind; // binding object
    private Context context; // context object
    private ScanOptions scanner; // scanner
    private String barCodeId; // setting bar code number
    private ProductAdapter productAdapter; // product adapter object
    private ArrayList<ProductModel> arrAllProduct; // array list of productModule of all products
    private ArrayList<ProductModel> arrUnAvailableProduct; // array list of productModule products are out of stock
    private ArrayList<ProductModel> arrAtReorderPointProduct; // array list of productModule products are at reorder point
    private ArrayList<String> arrProductId; // array list of product id
    private CategoryModel parentCategory; // category model object of parent category
    private ArrayList<String> filter = new ArrayList<>(Arrays.asList("All Products", "Unavailable Products", "Products at Reorder Point")); // filter list
    private ArrayAdapter adapterProductCategory; // array adapter of product category

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityProductListBinding.inflate(getLayoutInflater()); // inflating the layout
        context = bind.getRoot().getContext(); // getting context of the layout
        setContentView(bind.getRoot()); // setting the layout
        arrProductId = new ArrayList<>(); // initializing array list of product id

        // collecting category details
        parentCategory = new CategoryModel(); // initializing category model object of parent category
        parentCategory.setId(getIntent().getStringExtra("category_id")); // setting category id
        parentCategory.setName(getIntent().getStringExtra("category_name")); // setting category name

        // setting action bar title
        setSupportActionBar(bind.toolbarCategory); // setting action bar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking action bar is not null
        actionBar.setTitle(parentCategory.getName()); // setting action bar title
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button in action bar

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
        adapterProductCategory = new ArrayAdapter(context, android.R.layout.simple_spinner_item, filter);

        // set simple layout resource file
        // for each item of spinner
        adapterProductCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterSpinnerCategory.setAdapter(adapterProductCategory);

        dbGetAllProductsId(); // getting all products id from firebase
    }

    // getting all products id from firebase of parent category
    private void dbGetAllProductsId() {
        // collecting product ids from firebase
        Params.getREFERENCE().child(Params.getCATEGORY()).child(parentCategory.getId()).child(Params.getArrProducts()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // in dataSnapshot we have all the product ids, so we are getting it one by one using for each loop
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            arrProductId.add(post.getValue().toString()); // adding product id in product's id arraylist
                        }
                        dbGetAllProducts(); // getting all products from firebase
                    }
                });
    }

    // getting all products from firebase of parent category
    private void dbGetAllProducts() {
        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                arrAllProduct.clear(); // deleting all products from the list
                arrUnAvailableProduct.clear(); // deleting all products from the list
                arrAtReorderPointProduct.clear(); // deleting all products from the list

                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                for (DataSnapshot post : snapshot.getChildren()) {
                    // if product id is not in the product id list then continue
                    if(!arrProductId.contains(post.getKey().toString())){
                        continue;
                    }
                    ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                    newProduct.setCategory_id(newProduct.getCategory()); // setting category id
                    arrAllProduct.add(newProduct); // adding product in product's arraylist

                    // checking if product is out of stock or at reorder point
                    if(newProduct.getIsOutOfStock().equals("true")){
                        arrUnAvailableProduct.add(newProduct); // adding product in product's arraylist of out of stock products
                    }

                    // checking if product is at reorder point
                    if(newProduct.getIsReorderPointReached().equals("true")){
                        arrAtReorderPointProduct.add(newProduct); // adding product in product's arraylist of products at reorder point
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
        return MainToolbar.btnBack_clicked(item,context); // calling back button click event
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            MainToolbar.searchProduct_Barcode(arrAllProduct,productAdapter,barCodeId); // search the product by barcode
            filter.add("Selected Product"); // adding selected product type in filter
            adapterProductCategory.notifyDataSetChanged();  // notifying the adapter that new product type is added
            bind.FilterSpinnerCategory.setSelection(3); // setting selected product type in spinner
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show(); // showing message that scanner is unable to scan
    });

    // setting listener to, create action bar
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu); // inflating the toolbar menu
        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct);
        btnAddProduct.setVisible(false); // hiding add product button from action bar
        MenuItem btnSearch = menu.findItem(R.id.search); // getting search button item from actionbar
        MenuItem btnScan = menu.findItem(R.id.scanner); // getting scanner button item from actionbar
        SearchView searchView = (SearchView) btnSearch.getActionView(); // getting search view from search button

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text); // getting search edit text
        searchEditText.setTextColor(getResources().getColor(R.color.white)); // setting text color of search edit text
        searchEditText.setHintTextColor(getResources().getColor(R.color.white)); // setting hint text color of search edit text

        // setting on click lister in scanner item
        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                scanner = MainToolbar.getScanner(); // getting scanner
                bar.launch(scanner); // launching the scanner
                return true;
            }
        });

        assert searchView != null; // checking search view is not null
        // setting on query text listener in search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searching the product by name
                MainToolbar.btnSearch(query.toLowerCase(),arrAllProduct,productAdapter); // searching the product by name
                Log.d("SuccessMsg", "onQueryTextSubmit: Text = "+query); // showing message that search is successful
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searching the product by name
                if (newText.length() > 1) { // if search text length is greater than 1
                    MainToolbar.btnSearch(newText.toLowerCase(), arrAllProduct, productAdapter); // searching the product by name
                    Log.d("SuccessMsg", "onQueryTextChange: Text = "+newText); // showing message that search is successful
                } else if (newText.length() == 0){ // if search text length is 0
                    productAdapter.setLocalDataSet(arrAllProduct); // setting all products in the list
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // spinner item selected event
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String currentSelection = filter.get(i); // getting selected item from spinner

        // checking which item is selected
        if (currentSelection.equals(filter.get(0))) { // if all products is selected
            productAdapter.setLocalDataSet(arrAllProduct); // setting all products in the list
            if(filter.size() == 4) { // if selected product type is added in filter
                filter.remove(3); // removing selected product type from filter
                adapterProductCategory.notifyDataSetChanged(); // notifying the adapter that selected product type is removed
            }
        } else if (currentSelection.equals(filter.get(1))) { // if unavailable products is selected
            productAdapter.setLocalDataSet(arrUnAvailableProduct); // setting unavailable products in the list
        } else if (currentSelection.equals(filter.get(2))) { // if products at reorder point is selected
            productAdapter.setLocalDataSet(arrAtReorderPointProduct); // setting products at reorder point in the list
        }
        productAdapter.notifyDataSetChanged(); // notifying the adapter that new products are added
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}