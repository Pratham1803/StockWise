package com.example.stockwise.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.stockwise.MainActivity;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentHomeBinding;
import com.example.stockwise.databinding.FragmentProfileBinding;
import com.example.stockwise.fragments.category.ManageCategory;
import com.example.stockwise.fragments.product.ProductFragment;
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View root;
    private Context context;
    private FragmentHomeBinding bind;
    private ArrayList<ProductModel> arrAllProduct;
    private ArrayList<ProductModel> arrUnAvailableProduct;
    private ArrayList<ProductModel> arrAtReorderPointProduct;
    private ArrayList<CategoryModel> arrCategory;
    private ArrayList<DbTransactionModel> arrTransaction;
    private String today_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View Binding
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        context = bind.getRoot().getContext();
        root = bind.getRoot();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        today_date = today.format(formatter);

        //initializing arraylist
        arrAllProduct = new ArrayList<>();
        arrUnAvailableProduct = new ArrayList<>();
        arrAtReorderPointProduct = new ArrayList<>();
        arrCategory = new ArrayList<>();
        arrTransaction = new ArrayList<>();

        dbGetAllProducts();
        dbGetAllCategory();
        dbGetTransactions();

        bind.layoutTotalCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageCategory.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return root;

    }

    // get all products from firebase
    private void dbGetAllProducts() {
        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                arrAllProduct.clear(); // deleting all products from the list
                arrUnAvailableProduct.clear(); // deleting all products from the list
                arrAtReorderPointProduct.clear(); // deleting all products from the list

                for (DataSnapshot post : snapshot.getChildren()) {
                    if (post.exists()) {
                        ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                        assert newProduct != null;
                        newProduct.setCategory_id(newProduct.getCategory());
                        arrAllProduct.add(newProduct); // adding product in product's arraylist

                        if (newProduct.getIsOutOfStock().equals("true")) {
                            arrUnAvailableProduct.add(newProduct);
                        }
                        if (newProduct.getIsReorderPointReached().equals("true")) {
                            arrAtReorderPointProduct.add(newProduct);
                        }
                    }
                }

                Params.getOwnerModel().setArrAllProduct(arrAllProduct);
                Params.getOwnerModel().setArrUnAvailableProduct(arrUnAvailableProduct);
                Params.getOwnerModel().setArrAtReorderPointProduct(arrAtReorderPointProduct);

                bind.ShowOutOfStock.setText(String.valueOf(arrUnAvailableProduct.size()));
                bind.ShowReorderStock.setText(String.valueOf(arrAtReorderPointProduct.size()));
                bind.TotalItemShow.setText(String.valueOf(arrAllProduct.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
            }
        });
    }

    // collecting all category
    private void dbGetAllCategory() {
        Params.getREFERENCE().child(Params.getCATEGORY()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrCategory.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel newCategory = dataSnapshot.getValue(CategoryModel.class);
                    arrCategory.add(newCategory);
                }
                Params.getOwnerModel().setArrCategory(arrCategory);
                bind.TotalCategoryShow.setText(String.valueOf(arrCategory.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
            }
        });
    }

    // collecting transaction history
    // collect transactions history
    private void dbGetTransactions() {
        Params.getREFERENCE().child(Params.getTRANSACTION()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrTransaction.clear();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            DbTransactionModel dbTransactionModel = post.getValue(DbTransactionModel.class);
                            arrTransaction.add(dbTransactionModel);
                        }
                    }
                }

                Params.getOwnerModel().setArrTransactions(arrTransaction);
                int totalPrice = 0;

                for(DbTransactionModel dbTransactionModel : arrTransaction){
                    if(dbTransactionModel.getDate().equals(today_date))
                        totalPrice += Integer.parseInt(dbTransactionModel.getTotal_price());
                }
                bind.TotalProfitShow.setText("₹ " +totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "Collecting Transactions : " + error.getMessage());
            }
        });
    }
}