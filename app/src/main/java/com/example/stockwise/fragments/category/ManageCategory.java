package com.example.stockwise.fragments.category;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stockwise.MainActivity;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityMainBinding;
import com.example.stockwise.databinding.ActivityManageCategoryBinding;

public class ManageCategory extends AppCompatActivity {

    private ActivityManageCategoryBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        bind = ActivityManageCategoryBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        bind.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageCategory.this, MainActivity.class));
            }
        });

        bind.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageCategory.this);

                // Get the layout inflater
                LayoutInflater inflater = getLayoutInflater();

                // Inflate the custom layout for the dialog
                View view = inflater.inflate(R.layout.add_category_dialog, null);
                final EditText categoryEditText = view.findViewById(R.id.categoryEditText);

                // Set the custom layout to the builder
                builder.setView(view)
                        .setTitle("Add Product")
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Handle the "ADD" button click here
                                String categoryName = categoryEditText.getText().toString();
                                // Do something with the entered category
                                // For example, you can display a Toast message
                                // or save the category to a database
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle the "CANCEL" button click here
                                dialog.dismiss();
                            }
                        });

                // Show the AlertDialog
                builder.create().show();
            }
        });
    }
}