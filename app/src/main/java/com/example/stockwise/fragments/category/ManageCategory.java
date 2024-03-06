package com.example.stockwise.fragments.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityManageCategoryBinding;
import com.example.stockwise.model.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ManageCategory extends AppCompatActivity {
    private Context context;
    private ActivityManageCategoryBinding bind;
    private CategoryModel categoryModel;
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryModel> arrCategoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        bind = ActivityManageCategoryBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        setContentView(bind.getRoot());

        // setting Action bar
        setSupportActionBar(bind.toolbar); // setting action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        // setting recycler view
        arrCategoryList = new ArrayList<CategoryModel>();
        categoryAdapter = new CategoryAdapter(arrCategoryList, context);
        bind.recyclerCategory.setLayoutManager(new LinearLayoutManager(this));
        bind.recyclerCategory.setAdapter(categoryAdapter);

        // collecting data from database for recyclerview of category
        Params.getREFERENCE().child(Params.getCATEGORY()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrCategoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel newCategory = new CategoryModel();
                    newCategory.setName(dataSnapshot.child("name").getValue().toString());
                    newCategory.setNumOfProducts(dataSnapshot.child("numOfProducts").getValue().toString());
                    newCategory.setId(dataSnapshot.getKey());
                    arrCategoryList.add(newCategory);
                }
                categoryAdapter.notifyItemInserted(arrCategoryList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: "+error.getMessage());
            }
        });

        // show the dialog box to add category
        bind.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                AlertDialog.Builder builder = DialogBuilder.showDialog(ManageCategory.this, "Add Category", "");
                builder.setMessage(null);

                LayoutInflater inflater = getLayoutInflater();

                // Inflate the custom layout for the dialog
                View view = inflater.inflate(R.layout.add_category_dialog, null);
                final EditText edCategoryName = view.findViewById(R.id.categoryEditText);

                builder.setView(view);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoryModel = new CategoryModel();
                        categoryModel.setName(edCategoryName.getText().toString());
                        // Add category to database
                        addCategory();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // Show the AlertDialog
                builder.create().show();
            }
        });
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // adding category to database
    private void addCategory() {
        // Add category to database
        DatabaseReference reference = Params.getREFERENCE().child(Params.getCATEGORY()).push();
        categoryModel.setId(reference.getKey());
        categoryModel.setNumOfProducts("0");

        reference.setValue(categoryModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Show success message
                        new SweetAlertDialog(ManageCategory.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Category added successfully")
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorMsg", "onFailure: "+e.getMessage());
                        new SweetAlertDialog(ManageCategory.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Failed to add category")
                                .show();
                    }
                });
    }
}