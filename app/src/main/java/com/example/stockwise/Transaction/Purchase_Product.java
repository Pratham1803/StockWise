package com.example.stockwise.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityPrivacyPolicyBinding;
import com.example.stockwise.databinding.ActivityPurchaseProductBinding;
import com.example.stockwise.fragments.transaction.transactionFragment;

public class Purchase_Product extends AppCompatActivity {
    private ActivityPurchaseProductBinding bind; // declaring view binding


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_product);

        bind = ActivityPurchaseProductBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());


        bind.TransactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog();
            }
        });

        bind.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Purchase_Product.this, transactionFragment.class));
            }
        });

        bind.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Purchase_Product.this,Select_items.class));
            }
        });

    }

    private void OpenDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // Handle the selected date
                bind.DateShow.setText((day)+"/"+(month+1)+"/"+(year));
            }
        }, 2024, 0, 15);

        dialog.show();
    }
}