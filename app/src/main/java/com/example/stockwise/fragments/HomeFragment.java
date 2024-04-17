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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.stockwise.model.SelectItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private View root;
    private Context context;
    private FragmentHomeBinding bind;
    private ArrayList<ProductModel> arrAllProduct;
    private ArrayList<ProductModel> arrUnAvailableProduct;
    private ArrayList<ProductModel> arrAtReorderPointProduct;
    private ArrayList<CategoryModel> arrCategory;
    private ArrayList<DbTransactionModel> arrTransaction;
    private ArrayList<String> arrDates;
    private String today_date;
    private LocalDate today;
    private DateTimeFormatter dateTimeFormatter;
    private HashMap<String, ArrayList<DbTransactionModel>> mapTransaction;

    public HomeFragment() {
        //initializing arraylist
        arrAllProduct = new ArrayList<>();
        arrUnAvailableProduct = new ArrayList<>();
        arrAtReorderPointProduct = new ArrayList<>();
        arrCategory = new ArrayList<>();
        arrTransaction = new ArrayList<>();
        arrDates = new ArrayList<>(Arrays.asList("Today", "Yesterday", "Last 7 days"));
        mapTransaction = new HashMap<>();

        Params.getOwnerModel().setArrAllProduct(arrAllProduct);
        Params.getOwnerModel().setArrUnAvailableProduct(arrUnAvailableProduct);
        Params.getOwnerModel().setArrAtReorderPointProduct(arrAtReorderPointProduct);
        Params.getOwnerModel().setArrCategory(arrCategory);
        Params.getOwnerModel().setArrTransactions(arrTransaction);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View Binding
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        context = bind.getRoot().getContext();
        root = bind.getRoot();

        today = LocalDate.now();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        today_date = today.format(dateTimeFormatter);

        // setting Spinner
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrDates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spDates.setAdapter(adapter);
        bind.spDates.setSelection(0);
        bind.spDates.setOnItemSelectedListener(this);

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

    // filter transactions change
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String currentValue = arrDates.get(i);

        if (mapTransaction.isEmpty()) {
            return;
        }

        int saleQuantity = 0;
        int purchaseQuantity = 0;
        int totalEarning = 0;
        int totalPurchase = 0;
        int totalProfit = 0;

        if (currentValue.equals(arrDates.get(0))) {
            if(mapTransaction.containsKey(today_date)) {
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(today_date)) {
                    if (dbTransactionModel.getIsPurchase().equals("true")) {
                        purchaseQuantity += dbTransactionModel.getITEM_LIST().size();
                        totalPurchase += Integer.parseInt(dbTransactionModel.getTotal_price());
                    } else {
                        saleQuantity += dbTransactionModel.getITEM_LIST().size();
                        totalEarning += Integer.parseInt(dbTransactionModel.getTotal_price());
                        totalProfit += calculateProfit(dbTransactionModel.getITEM_LIST());
                    }
                }
            }
        } else if (currentValue.equals(arrDates.get(1))) {
            String yesterday_date = today.minusDays(1).format(dateTimeFormatter);
            if(mapTransaction.containsKey(yesterday_date)) {
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(yesterday_date)) {
                    if (dbTransactionModel.getDate().equals(yesterday_date)) {
                        if (dbTransactionModel.getIsPurchase().equals("true")) {
                            purchaseQuantity += dbTransactionModel.getITEM_LIST().size();
                            totalPurchase += Integer.parseInt(dbTransactionModel.getTotal_price());
                        } else {
                            saleQuantity += dbTransactionModel.getITEM_LIST().size();
                            totalEarning += Integer.parseInt(dbTransactionModel.getTotal_price());
                            totalProfit += calculateProfit(dbTransactionModel.getITEM_LIST());
                        }
                    }
                }
            }
        } else if (currentValue.equals(arrDates.get(2))) {
            LocalDate tempDate = today;
            for(int j = 0; j < 7 ;j++){
                String temp = tempDate.minusDays(j).format(dateTimeFormatter);
                if(mapTransaction.containsKey(temp)){
                    for (DbTransactionModel dbTransactionModel : mapTransaction.get(temp)) {
                        if (dbTransactionModel.getIsPurchase().equals("true")) {
                            purchaseQuantity += dbTransactionModel.getITEM_LIST().size();
                            totalPurchase += Integer.parseInt(dbTransactionModel.getTotal_price());
                        } else {
                            saleQuantity += dbTransactionModel.getITEM_LIST().size();
                            totalEarning += Integer.parseInt(dbTransactionModel.getTotal_price());
                            totalProfit += calculateProfit(dbTransactionModel.getITEM_LIST());
                        }
                    }
                }
            }
        }
        bind.txtQuanSaled.setText(String.valueOf(saleQuantity));
        bind.txtQuanPurchased.setText(String.valueOf(purchaseQuantity));
        bind.txtEarning.setText("₹ " + totalEarning);
        bind.txtSpent.setText("₹ " + totalPurchase);

        if (totalProfit < 0) {
            bind.txtProfit.setTextColor(getResources().getColor(R.color.red));
        } else {
            bind.txtProfit.setTextColor(getResources().getColor(R.color.SuccessGreen));
        }
        bind.txtProfit.setText("₹ " + totalProfit);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // calculate profit
    private int calculateProfit(List<SelectItemModel> arrProducts) {
        int totalProfit = 0;
        for (SelectItemModel product : arrProducts) {
            totalProfit += (Integer.parseInt(product.getSale_price()) - Integer.parseInt(product.getPurchase_price()));
        }
        return totalProfit;
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrTransaction.clear();
                mapTransaction.clear();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            DbTransactionModel dbTransactionModel = post.getValue(DbTransactionModel.class);
                            arrTransaction.add(dbTransactionModel);
                        }
                    }
                    int totalPrice = 0;

                    for(DbTransactionModel dbTransactionModel : arrTransaction){
                        if(dbTransactionModel.getDate().equals(today_date) && dbTransactionModel.getIsPurchase().equals("false"))
                            totalPrice += Integer.parseInt(dbTransactionModel.getTotal_price());
                    }
                    bind.TotalProfitShow.setText("₹ " +totalPrice);
                    sortTransactions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "Collecting Transactions : " + error.getMessage());
            }
        });
    }

    // sort the list
    private void sortTransactions() {
        for (DbTransactionModel dbTransactionModel : arrTransaction) {
            if (mapTransaction.containsKey(dbTransactionModel.getDate())) {
                mapTransaction.get(dbTransactionModel.getDate()).add(dbTransactionModel);
            } else {
                ArrayList<DbTransactionModel> temp = new ArrayList<>();
                temp.add(dbTransactionModel);
                mapTransaction.put(dbTransactionModel.getDate(), temp);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Create a TreeMap with a custom comparator
        TreeMap<String, ArrayList<DbTransactionModel>> sortedMap = new TreeMap<>((date1, date2) -> {
            try {
                return sdf.parse(date1).compareTo(sdf.parse(date2));
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse dates", e);
            }
        });

        sortedMap.putAll(mapTransaction);
        mapTransaction.clear();
        mapTransaction.putAll(sortedMap);

        arrTransaction.clear();
        for (String key : sortedMap.keySet()) {
            arrTransaction.addAll(sortedMap.get(key));
        }
        Collections.reverse(arrTransaction);
        Params.getOwnerModel().setArrTransactions(arrTransaction);
    }
}