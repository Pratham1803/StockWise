package com.example.stockwise.fragments.transaction;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
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
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

public class transactionFragment extends Fragment {

    private FragmentTransactionBinding bind; // bind view
    private Context context; // to store context
    private Calendar selectedDate; // to store selected date
    private TransactionHistory_adapter transactionHistoryAdapter; // initializing adapter
    private ArrayList<DbTransactionModel> arrTransactions; // to store transaction
    private ArrayList<String> lsName; // to store names
    private int REQUEST_CODE_ADD_PRODUCT = 1; // request code for add product

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
                AlertDialog.Builder builder = DialogBuilder.showDialog(context, "Create New Order", ""); // initializing dialog builder
                builder.setMessage(null); // set message to null

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.new_order_dialog, null); // initializing custom layout
                builder.setView(customLayout); // setting custom layout
                Button btnSell = customLayout.findViewById(R.id.btnSell); // initializing sell button
                Button btnPurchase = customLayout.findViewById(R.id.btnPurchase); // initializing purchase button

                AlertDialog dialog = builder.create(); // creating dialog

                // add a button to the alert dialog
                btnSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SellProduct.class); // initializing intent
                        intent.putExtra("isPurchasing", false); // setting extra value
                        startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT); // starting activity for result
                        dialog.dismiss(); // dismissing dialog
                    }
                });

                // add a button to the alert dialog
                btnPurchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SellProduct.class); // initializing intent
                        intent.putExtra("isPurchasing", true); // setting extra value
                        startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT); // starting activity for result
                        dialog.dismiss(); // dismissing dialog
                    }
                });

                // add a button to the alert dialog
                builder.setNegativeButton("Cancel", (dialogIn, which) -> {
                    dialogIn.dismiss(); // dismissing dialog
                });

                // create and show the alert dialog
                dialog.show();
            }
        });

        selectedDate = Calendar.getInstance(); // initializing selected date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // initializing date format
        bind.DateShow.setText(dateFormat.format(selectedDate.getTime())); // setting date to text view
        bind.DateShow.setText("All Records"); // setting text to text view
        bind.TransactionHistoryDate.setOnClickListener(v -> OpenDialog()); // setting on click listener

        arrTransactions = new ArrayList<>(); // initializing array list
        arrTransactions.addAll(Params.getOwnerModel().getArrTransactions()); // adding all transactions to array list
        lsName = new ArrayList<>(); // initializing array list
        dbCollectPersonNames(); // collecting person names

        return bind.getRoot();
    }// End OnCreate

    // OnActivityResult method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        if (requestCode == REQUEST_CODE_ADD_PRODUCT && resultCode == Activity.RESULT_OK) {
            // Refresh your product list here
            if(arrTransactions.size() != Params.getOwnerModel().getArrTransactions().size()) { // checking size
                arrTransactions.clear(); // clearing array list
                arrTransactions.addAll(Params.getOwnerModel().getArrTransactions()); // adding all transactions to array list
                dbCollectPersonNames(); // collecting person names
            }
        }
    }

    // OpenDialog method
    private void OpenDialog() {
        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth); // setting date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // initializing date format
                        String date = dateFormat.format(selectedDate.getTime()); // formatting date
                        bind.DateShow.setText(date); // setting date to text view
                        arrTransactions.clear(); // clearing array list

                        // iterating through transactions
                        for(DbTransactionModel dbTransactionModel : Params.getOwnerModel().getArrTransactions()){
                            if(dbTransactionModel.getDate().equals(date)) // checking date
                                arrTransactions.add(dbTransactionModel); // adding transaction
                        }
                        transactionHistoryAdapter.notifyDataSetChanged(); // notifying adapter
                    }
                },
                selectedDate.get(Calendar.YEAR), // setting year
                selectedDate.get(Calendar.MONTH), // setting month
                selectedDate.get(Calendar.DAY_OF_MONTH) // setting day
        );

        // setting buttons
        datePickerDialog.setButton2("All Record", (dialog, which) -> {
            bind.DateShow.setText("All Records"); // setting text to text view
            arrTransactions.clear(); // clearing array list
            arrTransactions.addAll(Params.getOwnerModel().getArrTransactions()); // adding all transactions to array list
            if (transactionHistoryAdapter != null) // checking adapter
                transactionHistoryAdapter.notifyDataSetChanged(); // notifying adapter
        });
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // setting max date
        datePickerDialog.show(); // showing dialog
    }

    // dbCollectPersonNames method
    private void dbCollectPersonNames() {
        lsName.clear(); // clearing array list

        // iterating through transactions
        for (DbTransactionModel dbTransactionModel : arrTransactions) {
            DatabaseReference dbRef; // initializing database reference

            // checking is purchase
            if (dbTransactionModel.getIsPurchase().equals("true")) // checking is purchase
                dbRef = Params.getREFERENCE().child(Params.getPERSON()).child(Params.getVENDOR()); // initializing database reference
            else // if not purchase
                dbRef = Params.getREFERENCE().child(Params.getPERSON()).child(Params.getCUSTOMER()); // initializing database reference

            // adding value event listener
            dbRef.child(dbTransactionModel.getPerson_id()).child(Params.getNAME()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            lsName.add(snapshot.getValue(String.class)); // adding name to array list
                            if (lsName.size() == arrTransactions.size()) { // checking size
                                transactionHistoryAdapter = new TransactionHistory_adapter(arrTransactions, lsName, context); // initializing adapter
                                bind.TransactionRecycler.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager
                                bind.TransactionRecycler.setAdapter(transactionHistoryAdapter); // setting adapter
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("ErrorMsg", "Collecting Person Name : " + error.getMessage());
                        }
                    });
        }
    }
}