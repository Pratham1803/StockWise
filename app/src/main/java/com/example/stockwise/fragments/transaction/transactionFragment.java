package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.databinding.FragmentTransactionBinding;

public class transactionFragment extends Fragment {

    private FragmentTransactionBinding bind; // bind view
    private Context context; // to store context
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentTransactionBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar
        // Inflate the layout for this fragment

        bind.btnSaleProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SellProduct.class);
                intent.putExtra("isPurchasing", false);
                startActivity(intent);
            }
        });

        bind.btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SellProduct.class);
                intent.putExtra("isPurchasing", true);
                startActivity(intent);
            }
        });

        return bind.getRoot();
    }
}