package com.example.stockwise.fragments.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.stockwise.MainActivity;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.databinding.FragmentProfileBinding;

public class ProductFragment extends Fragment {
    private Context context;
    private FragmentProductBinding bind;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProductBinding.inflate(inflater,container,false);
        context = bind.getRoot().getContext();
        setHasOptionsMenu(true);

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