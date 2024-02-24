package com.example.stockwise.fragments.product;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
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

import java.util.ArrayList;

public class ProductFragment extends Fragment {
    private Context context; // to store context
    private FragmentProductBinding bind; // bind view
    private ArrayList<ProductModel> arrProduct; // List of productModule class to store the details of multiple product
    private ProductAdapter productAdapter; // object of product adapter
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater,container,false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar

        // set recycler view
        arrProduct = new ArrayList<ProductModel>(); // initializing Array list of productModule
        productAdapter = new ProductAdapter(arrProduct,context); // initializing productAdapter
        bind.recyclerProduct.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerProduct.setAdapter(productAdapter); // setting adapter to the recycler view

        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                for(DataSnapshot post : snapshot.getChildren()){
                    ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                    newProduct.setId(post.getKey()); // setting user id of product to class object
                    arrProduct.add(newProduct); // adding product in product's arraylist
                }
                // dataset is changed, so notifying the adapter
                productAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.toolbar_menu,menu);

        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct);
        MenuItem btnSearch = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) btnSearch.getActionView();
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
                startActivity(new Intent(context,AddProduct.class));
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}