package com.example.stockwise.fragments.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
    private Context context;
    private FragmentProductBinding bind;
    private ArrayList<ProductModel> arrProduct;
    private ProductAdapter productAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater,container,false);
        context = bind.getRoot().getContext();
        setHasOptionsMenu(true);

        // set recycler view
        arrProduct = new ArrayList<ProductModel>();
        productAdapter = new ProductAdapter(arrProduct,context);
        bind.recyclerProduct.setLayoutManager(new LinearLayoutManager(context));
        bind.recyclerProduct.setAdapter(productAdapter);

        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot post : snapshot.getChildren()){
                    ProductModel newProduct = post.getValue(ProductModel.class);
                    newProduct.setId(post.getKey());
                    arrProduct.add(newProduct);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return bind.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu,menu);

        MenuItem btnAddProduct = menu.findItem(R.id.addProduct);
        btnAddProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(context,AddProduct.class));
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}