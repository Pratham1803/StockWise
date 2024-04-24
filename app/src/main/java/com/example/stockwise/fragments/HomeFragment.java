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

import com.example.stockwise.DialogBuilder;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private View root; // root view
    private Context context; // context
    private FragmentHomeBinding bind; // view binding
    private ArrayList<ProductModel> arrAllProduct; // all product list
    private ArrayList<ProductModel> arrUnAvailableProduct; // out of stock product list
    private ArrayList<ProductModel> arrAtReorderPointProduct; // at reorder point product list
    private ArrayList<CategoryModel> arrCategory; // category list
    private ArrayList<DbTransactionModel> arrTransaction; // transaction list
    private ArrayList<String> arrDates; // dates list
    private String today_date; // today's date
    private LocalDate today; // today's date
    private DateTimeFormatter dateTimeFormatter; // date time formatter
    private HashMap<String, ArrayList<DbTransactionModel>> mapTransaction; // transaction map

    public HomeFragment() {
        //initializing arraylist
        arrAllProduct = new ArrayList<>(); // all product list
        arrUnAvailableProduct = new ArrayList<>(); // out of stock product list
        arrAtReorderPointProduct = new ArrayList<>(); // at reorder point product list
        arrCategory = new ArrayList<>(); // category list
        arrTransaction = new ArrayList<>(); // transaction list
        arrDates = new ArrayList<>(Arrays.asList("Today", "Yesterday", "Last 7 days")); // dates list
        mapTransaction = new HashMap<>(); // transaction map

        Params.getOwnerModel().setArrAllProduct(arrAllProduct); // setting all product list
        Params.getOwnerModel().setArrUnAvailableProduct(arrUnAvailableProduct); // setting out of stock product list
        Params.getOwnerModel().setArrAtReorderPointProduct(arrAtReorderPointProduct); // setting at reorder point product list
        Params.getOwnerModel().setArrCategory(arrCategory); // setting category list
        Params.getOwnerModel().setArrTransactions(arrTransaction); // setting transaction list
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View Binding
        bind = FragmentHomeBinding.inflate(inflater, container, false); // view binding
        context = bind.getRoot().getContext(); // context
        root = bind.getRoot(); // root view

        today = LocalDate.now(); // today's date
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // date time formatter
        today_date = today.format(dateTimeFormatter); // today's date

        // setting Spinner
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrDates); // setting spinner adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // setting spinner dropdown view
        bind.spDates.setAdapter(adapter); // setting spinner adapter
        bind.spDates.setSelection(0); // setting spinner selection
        bind.spDates.setOnItemSelectedListener(this); // setting spinner on item selected listener

        dbGetAllProducts(); // getting all products
        dbGetAllCategory(); // getting all category
        dbGetTransactions(); // getting all transactions

        // setting click listener
        bind.layoutTotalCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageCategory.class); // intent to manage category
                startActivity(intent); // starting activity
            }
        });

        // setting click listener
        bind.layoutSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapTransaction.isEmpty()) { // if transaction is empty
                    // show error dialog
                    SweetAlertDialog alertDialog = DialogBuilder.showSweetDialogError(context, "StockWise", "No Transaction Found");
                    alertDialog.setConfirmButton("OK", sweetAlertDialog -> { // setting confirm button
                        alertDialog.dismissWithAnimation(); // dismiss dialog
                    });
                    return;
                }
                Intent intent = new Intent(context, SalesAnalysis.class); // intent to sales analysis
                intent.putExtra("mapTransaction", mapTransaction); // passing transaction map
                startActivity(intent); // starting activity
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    // filter transactions change
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String currentValue = arrDates.get(i); // getting current value

        // if transaction is empty
        if (mapTransaction.isEmpty()) {
            return;
        }

        int saleQuantity = 0; // sale quantity
        int purchaseQuantity = 0; // purchase quantity
        int totalEarning = 0; // total earning
        int totalPurchase = 0; // total purchase
        int totalProfit = 0; // total profit

        // if current value is today
        if (currentValue.equals(arrDates.get(0))) {
            if (mapTransaction.containsKey(today_date)) { // if transaction contains today's date
                // for each transaction in today's date
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(today_date)) {
                    if (dbTransactionModel.getIsPurchase().equals("true")) { // if transaction is purchase
                        purchaseQuantity += dbTransactionModel.getITEM_LIST().size(); // adding product
                        totalPurchase += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total purchase
                    } else { // if transaction is sale
                        saleQuantity += dbTransactionModel.getITEM_LIST().size(); // adding product
                        totalEarning += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total earning
                        totalProfit += calculateProfit(dbTransactionModel.getITEM_LIST()); // calculating profit
                    }
                }
            }
        } else if (currentValue.equals(arrDates.get(1))) { // if current value is yesterday
            String yesterday_date = today.minusDays(1).format(dateTimeFormatter); // getting yesterday's date
            if (mapTransaction.containsKey(yesterday_date)) { // if transaction contains yesterday's date
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(yesterday_date)) { // for each transaction in yesterday's date
                    if (dbTransactionModel.getDate().equals(yesterday_date)) { // if transaction is yesterday
                        if (dbTransactionModel.getIsPurchase().equals("true")) {    // if transaction is purchase
                            purchaseQuantity += dbTransactionModel.getITEM_LIST().size(); // adding product
                            totalPurchase += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total purchase
                        } else { // if transaction is sale
                            saleQuantity += dbTransactionModel.getITEM_LIST().size(); // adding product
                            totalEarning += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total earning
                            totalProfit += calculateProfit(dbTransactionModel.getITEM_LIST()); // calculating profit
                        }
                    }
                }
            }
        } else if (currentValue.equals(arrDates.get(2))) { // if current value is last 7 days
            LocalDate tempDate = today; // getting today's date
            for (int j = 0; j < 7; j++) { // for each date in last 7 days
                String temp = tempDate.minusDays(j).format(dateTimeFormatter); // getting date
                if (mapTransaction.containsKey(temp)) { // if transaction contains date
                    for (DbTransactionModel dbTransactionModel : mapTransaction.get(temp)) { // for each transaction in date
                        if (dbTransactionModel.getIsPurchase().equals("true")) { // if transaction is purchase
                            purchaseQuantity += dbTransactionModel.getITEM_LIST().size(); // adding product
                            totalPurchase += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total purchase
                        } else { // if transaction is sale
                            saleQuantity += dbTransactionModel.getITEM_LIST().size(); // adding product
                            totalEarning += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total earning
                            totalProfit += calculateProfit(dbTransactionModel.getITEM_LIST()); // calculating profit
                        }
                    }
                }
            }
        }
        bind.txtQuanSaled.setText(String.valueOf(saleQuantity)); // setting sale quantity
        bind.txtQuanPurchased.setText(String.valueOf(purchaseQuantity)); // setting purchase quantity
        bind.txtEarning.setText("₹ " + totalEarning); // setting total earning
        bind.txtSpent.setText("₹ " + totalPurchase); // setting total purchase

        if (totalProfit < 0) { // if total profit is less than 0
            bind.txtProfit.setTextColor(getResources().getColor(R.color.red)); // setting text color to red
        } else { // if total profit is greater than 0
            bind.txtProfit.setTextColor(getResources().getColor(R.color.profit)); // setting text color to profit
        }
        bind.txtProfit.setText("₹ " + totalProfit); // setting total profit
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // calculate profit
    private int calculateProfit(List<SelectItemModel> arrProducts) {
        int totalProfit = 0; // total profit
        for (SelectItemModel product : arrProducts) { // for each product in product list
            // calculating profit
            totalProfit += (Integer.parseInt(product.getSale_price()) - Integer.parseInt(product.getPurchase_price())) * Integer.parseInt(product.getQuantity());
        }
        return totalProfit; // returning total profit
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

                // for each product in snapshot
                for (DataSnapshot post : snapshot.getChildren()) {
                    if (post.exists()) { // if product exists
                        ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                        assert newProduct != null; // checking if product is null
                        newProduct.setCategory_id(newProduct.getCategory()); // setting category id
                        arrAllProduct.add(newProduct); // adding product in product's arraylist

                        if (newProduct.getIsOutOfStock().equals("true")) { // if product is out of stock
                            arrUnAvailableProduct.add(newProduct); // adding product in out of stock product list
                        }
                        if (newProduct.getIsReorderPointReached().equals("true")) { // if product is at reorder point
                            arrAtReorderPointProduct.add(newProduct); // adding product in at reorder point product list
                        }
                    }
                }

                Params.getOwnerModel().setArrAllProduct(arrAllProduct); // setting all product list
                Params.getOwnerModel().setArrUnAvailableProduct(arrUnAvailableProduct); // setting out of stock product list
                Params.getOwnerModel().setArrAtReorderPointProduct(arrAtReorderPointProduct); // setting at reorder point product list

                bind.ShowOutOfStock.setText(String.valueOf(arrUnAvailableProduct.size())); // setting out of stock product count
                bind.ShowReorderStock.setText(String.valueOf(arrAtReorderPointProduct.size())); // setting at reorder point product count
                bind.TotalItemShow.setText(String.valueOf(arrAllProduct.size())); // setting total product count
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
                arrCategory.clear(); // deleting all category from the list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) { // for each category in snapshot
                    CategoryModel newCategory = dataSnapshot.getValue(CategoryModel.class); // storing category details in categoryModule class object
                    arrCategory.add(newCategory); // adding category in category's arraylist
                }
                Params.getOwnerModel().setArrCategory(arrCategory); // setting category list
                bind.TotalCategoryShow.setText(String.valueOf(arrCategory.size())); // setting category count
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
                arrTransaction.clear(); // deleting all transactions from the list
                mapTransaction.clear(); // deleting all transactions from the map
                if (snapshot.hasChildren()) { // if snapshot has children
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) { // for each transaction in snapshot from date child
                        for (DataSnapshot post : dataSnapshot.getChildren()) { // for each transaction in post
                            // storing transaction details in dbTransactionModel class object
                            DbTransactionModel dbTransactionModel = post.getValue(DbTransactionModel.class);
                            arrTransaction.add(dbTransactionModel); // adding transaction in transaction's arraylist
                        }
                    }
                    int totalPrice = 0; // total price
                    int mainProfit = 0; // main profit

                    // for each transaction in transaction list
                    for (DbTransactionModel dbTransactionModel : arrTransaction) {
                        // if transaction is today's date and is not purchase
                        if (dbTransactionModel.getDate().equals(today_date) && dbTransactionModel.getIsPurchase().equals("false"))
                            totalPrice += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding total price
                    }

                    // for each transaction in transaction list
                    for (DbTransactionModel dbTransactionModel : arrTransaction) {
                        if(dbTransactionModel.getIsPurchase().equals("false")) // if transaction is sale
                            mainProfit += Integer.parseInt(dbTransactionModel.getTotal_price()); // adding main price
                    }
                    bind.TotalProfitShow.setText("₹ " + totalPrice); // setting total price
                    bind.SalesShow.setText("Total Sales : ₹ "+ mainProfit); // setting total sales
                    sortTransactions(); // sort the list
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
        // for each transaction in transaction list
        for (DbTransactionModel dbTransactionModel : arrTransaction) {
            if (mapTransaction.containsKey(dbTransactionModel.getDate())) { // if transaction contains date
                mapTransaction.get(dbTransactionModel.getDate()).add(dbTransactionModel); // adding transaction
            } else { // if transaction does not contain date
                ArrayList<DbTransactionModel> temp = new ArrayList<>(); // creating new arraylist
                temp.add(dbTransactionModel); // adding transaction
                mapTransaction.put(dbTransactionModel.getDate(), temp); // putting transaction in map
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // date format

        // Create a TreeMap with a custom comparator
        TreeMap<String, ArrayList<DbTransactionModel>> sortedMap = new TreeMap<>((date1, date2) -> {
            try {
                return sdf.parse(date1).compareTo(sdf.parse(date2)); // compare dates
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse dates", e);
            }
        });

        sortedMap.putAll(mapTransaction); // putting all transactions in sorted map
        mapTransaction.clear(); // deleting all transactions from the map
        mapTransaction.putAll(sortedMap); // putting all transactions in map again from sorted map

        arrTransaction.clear(); // deleting all transactions from the list
        for (String key : sortedMap.keySet()) { // for each key in sorted map
            arrTransaction.addAll(sortedMap.get(key)); // adding transaction
        }
        Collections.reverse(arrTransaction); // reverse the list
        Params.getOwnerModel().setArrTransactions(new ArrayList<>()); // setting transaction list
        Params.getOwnerModel().getArrTransactions().addAll(arrTransaction); // adding transaction
        Params.getOwnerModel().setMapTransaction(new HashMap<>()); // setting transaction map
        Params.getOwnerModel().getMapTransaction().putAll(mapTransaction); // putting all transactions in map
    }
}