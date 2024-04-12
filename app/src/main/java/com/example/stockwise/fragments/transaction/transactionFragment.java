package com.example.stockwise.fragments.transaction;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentTransactionBinding;
import com.example.stockwise.model.DbTransactionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class transactionFragment extends Fragment {

    private FragmentTransactionBinding bind; // bind view
    private Context context; // to store context
    private Calendar selectedDate;
    private TransactionHistory_adapter transactionHistoryAdapter; // initializing adapter
    private ArrayList<DbTransactionModel> arrTransactions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentTransactionBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext(); // initializing context

        setHasOptionsMenu(true); // setting action bar

        bind.btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an alert builder
                AlertDialog.Builder builder = DialogBuilder.showDialog(context, "Create New Order", "");
                builder.setMessage(null);

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.new_order_dialog, null);
                builder.setView(customLayout);
                Button btnSell = customLayout.findViewById(R.id.btnSell);
                Button btnPurchase = customLayout.findViewById(R.id.btnPurchase);

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

                builder.setNegativeButton("Cancel", (dialogIn, which) -> {
                    dialogIn.dismiss();
                });
                // create and show the alert dialog

                builder.create().show();
            }
        });

        selectedDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
        bind.DateShow.setText("All Records");
        bind.TransactionHistoryDate.setOnClickListener(v -> OpenDialog());

        // set adapter
        arrTransactions = new ArrayList<>();
        arrTransactions.addAll(Params.getOwnerModel().getArrTransactions());
        transactionHistoryAdapter = new TransactionHistory_adapter(arrTransactions, context);
        bind.TransactionRecycler.setLayoutManager(new LinearLayoutManager(context));
        bind.TransactionRecycler.setAdapter(transactionHistoryAdapter);

        return bind.getRoot();
    }// End OnCreate

    // collect transactions history
    private void dbGetTransactions() {
        Params.getREFERENCE().child(Params.getTRANSACTION()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrTransactions.clear();
                if (snapshot.hasChildren()) {
                    String tempDate;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        tempDate = dataSnapshot.getKey();
                        if (bind.DateShow.getText().equals(tempDate)) {
                            for (DataSnapshot post : dataSnapshot.getChildren()) {
                                DbTransactionModel dbTransactionModel = post.getValue(DbTransactionModel.class);
                                arrTransactions.add(dbTransactionModel);
                            }
                        }
                    }
                    transactionHistoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "Collecting Transactions : " + error.getMessage());
            }
        });
    }

    private void OpenDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
                        dbGetTransactions();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setButton2("All Record", (dialog, which) -> {
            bind.DateShow.setText("All Records");
            arrTransactions.addAll(Params.getOwnerModel().getArrTransactions());
            transactionHistoryAdapter.notifyDataSetChanged();
        });
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}