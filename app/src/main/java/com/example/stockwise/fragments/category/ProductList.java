package com.example.stockwise.fragments.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityProductListBinding;
import com.example.stockwise.fragments.product.ProductAdapter;
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityProductListBinding bind;
    private Context context;
    private ProductAdapter productAdapter;
    private ArrayList<ProductModel> arrAllProduct;
    private ArrayList<ProductModel> arrUnAvailableProduct;
    private ArrayList<ProductModel> arrAtReorderPointProduct;
    private ArrayList<String> arrProductId;
    private CategoryModel parentCategory;
    String[] filter = { "All Products","Unavailable Products","Products at Reorder Point"};
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
        bind.toolbarCategory.setTitle(parentCategory.getName());
        setSupportActionBar(bind.toolbarCategory);

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
    public void onNothingSelected(AdapterView<?> parent) {

    }
}