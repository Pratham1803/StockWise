package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stockwise.R;
import com.example.stockwise.Transaction.Purchase_Product;
import com.example.stockwise.databinding.FragmentHomeBinding;
import com.example.stockwise.databinding.FragmentTransactionBinding;

public class transactionFragment extends Fragment {

    private View root;
    private Context context;
    private FragmentTransactionBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View Binding
        bind = FragmentTransactionBinding.inflate(inflater, container, false);
        context = bind.getRoot().getContext();
        root = bind.getRoot();

        // Select Date button click event
        bind.btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, Purchase_Product.class));
            }
        });

        // Inflate the layout for this fragment
        return root;


    }
}