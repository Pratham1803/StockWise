package com.example.stockwise.fragments.transaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.databinding.FragmentTransactionBinding;
import com.example.stockwise.fragments.profile.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class transactionFragment extends Fragment {

    private FragmentTransactionBinding bind; // bind view
    private Context context; // to store context
    private Calendar selectedDate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentTransactionBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context
        setHasOptionsMenu(true); // setting action bar
        // Inflate the layout for this fragment

//        bind.btnSaleProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, SellProduct.class);
//                intent.putExtra("isPurchasing", false);
//                startActivity(intent);
//            }
//        });
//
//        bind.btnPurchase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, SellProduct.class);
//                intent.putExtra("isPurchasing", true);
//                startActivity(intent);
//            }
//        });

        bind.btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Create New Order");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.new_order_dialog, null);
                builder.setView(customLayout);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnSell = customLayout.findViewById(R.id.btnSell);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnPurchase = customLayout.findViewById(R.id.btnPurchase);

                btnSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SellProduct.class);
                        intent.putExtra("isPurchasing", false);
                        startActivity(intent);
                    }
                });

                btnPurchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SellProduct.class);
                        intent.putExtra("isPurchasing", true);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        selectedDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
        bind.TransactionHistoryDate.setOnClickListener(v -> OpenDialog());

        return bind.getRoot();
    }// End OnCreate

    private void OpenDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}