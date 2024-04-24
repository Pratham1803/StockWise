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
import com.example.stockwise.MainToolbar;
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
    private Context context; // initializing context
    private ActivityManageCategoryBinding bind; // initializing view binding
    private CategoryModel categoryModel; // initializing category model
    private CategoryAdapter categoryAdapter; // initializing category adapter
    private boolean isSearched; // initializing search boolean
    private ArrayList<CategoryModel> arrCategoryList; // initializing category list
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityManageCategoryBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        setContentView(bind.getRoot()); // setting view

        // setting Action bar
        setSupportActionBar(bind.toolbar); // setting action bar
        ActionBar actionBar = getSupportActionBar(); // initializing action bar
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button clickable

        // setting recycler view
        arrCategoryList = new ArrayList<>(); // initializing category list
        arrCategoryList.addAll(Params.getOwnerModel().getArrCategory()); // adding item in category list
        categoryAdapter = new CategoryAdapter(arrCategoryList, context); // initializing category adapter
        bind.recyclerCategory.setLayoutManager(new LinearLayoutManager(this)); // setting recycler view layout
        bind.recyclerCategory.setAdapter(categoryAdapter); // setting recycler view adapter

        // getting search result of category from database
        bind.searchCategory.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // submitted query
                // if query is empty then show all category
                if(query.isEmpty()){
                    arrCategoryList.clear(); // clearing category list
                    arrCategoryList.addAll(Params.getOwnerModel().getArrCategory()); // adding all items in category list
                    categoryAdapter.notifyDataSetChanged(); // notifying adapter
                    isSearched = false; // setting search boolean to false
                    return false;
                }

                ArrayList<CategoryModel> tempList = new ArrayList<>(); // initializing temporary category list
                tempList.addAll(arrCategoryList); // adding all items in temporary category list
                arrCategoryList.clear(); // clearing category list

                // retrieving all category from temporary list
                for(CategoryModel categoryModel : tempList){
                    // if category name contains query then add it to category list
                    if(categoryModel.getName().toLowerCase().contains(query.toLowerCase())){
                        arrCategoryList.add(categoryModel); // adding category to category list
                        isSearched = true; // setting search boolean to true
                    }
                }
                categoryAdapter.notifyDataSetChanged(); // notifying adapter
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // single character entering search
                // if query is empty then show all category
                if(newText.isEmpty()){
                    arrCategoryList.clear(); // clearing category list
                    arrCategoryList.addAll(Params.getOwnerModel().getArrCategory()); // adding all items in category list
                    categoryAdapter.notifyDataSetChanged(); // notifying adapter
                    isSearched = false; // setting search boolean to false
                    return false;
                }

                // retrieving all category from category list
                ArrayList<CategoryModel> tempList = new ArrayList<>(); // initializing temporary category list
                tempList.addAll(arrCategoryList); // adding all items in temporary category list
                arrCategoryList.clear(); // clearing category list
                for(CategoryModel categoryModel : tempList){ // retrieving all category from temporary list
                    // if category name contains query then add it to category list
                    if(categoryModel.getName().toLowerCase().contains(newText.toLowerCase())){
                        arrCategoryList.add(categoryModel); // adding category to category list
                        isSearched = true; // setting search boolean to true
                    }
                }
                categoryAdapter.notifyDataSetChanged(); // notifying adapter
                return true;
            }
        });

        // show the dialog box to add category
        bind.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                // Show the dialog box to add category
                AlertDialog.Builder builder = DialogBuilder.showDialog(ManageCategory.this, "Add Category", "");
                builder.setMessage(null); // setting message to null

                LayoutInflater inflater = getLayoutInflater(); // initializing layout inflater

                // Inflate the custom layout for the dialog
                View view = inflater.inflate(R.layout.add_category_dialog, null); // inflating layout
                final EditText edCategoryName = view.findViewById(R.id.categoryEditText); // initializing edit text

                builder.setView(view); // setting custom view

                // Set the positive and negative buttons
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoryModel = new CategoryModel(); // initializing category model
                        categoryModel.setName(edCategoryName.getText().toString()); // setting category name from textbox
                        addCategory(); // adding category to database
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // dismissing dialog
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
        // if search is true then show all category
        if(isSearched){
            arrCategoryList.clear(); // clearing category list
            arrCategoryList.addAll(Params.getOwnerModel().getArrCategory()); // adding all items in category list
            categoryAdapter.notifyDataSetChanged(); // notifying adapter
            isSearched = false; // setting search boolean to false
            return false;
        }else
            return MainToolbar.btnBack_clicked(item,context); // back button clicked
    }

    // adding category to database
    private void addCategory() {
        // Add category to database
        DatabaseReference reference = Params.getREFERENCE().child(Params.getCATEGORY()).push(); // initializing database reference
        categoryModel.setId(reference.getKey()); // setting category id

        // Add category to database
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
                        // Show error message
                        new SweetAlertDialog(ManageCategory.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("Failed to add category")
                                .show();
                    }
                });
    }

    // back press event of mobile back button
    @Override
    public void onBackPressed() {
        // if search is true then show all category
        if(isSearched){
            arrCategoryList.clear(); // clearing category list
            arrCategoryList.addAll(Params.getOwnerModel().getArrCategory()); // adding all items in category list
            categoryAdapter.notifyDataSetChanged(); // notifying adapter
            isSearched = false; // setting search boolean to false
        }
        else
            super.onBackPressed(); // back button pressed
    }
}