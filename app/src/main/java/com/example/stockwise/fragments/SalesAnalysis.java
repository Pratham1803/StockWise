package com.example.stockwise.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivitySalesAnalysisBinding;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.ProductModel;
import com.example.stockwise.model.SelectItemModel;
import com.example.stockwise.model.TransactionModel;

import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SalesAnalysis extends AppCompatActivity {
    private ActivitySalesAnalysisBinding binding; // binding object
    private Context context; // context object
    private HashMap<String, ArrayList<DbTransactionModel>> mapTransaction; // map for transaction
//    private HashMap<String, String> products;
//    private ArrayList<Integer> arrEarning;
//    private ArrayList<Integer> arrSpending;
//    private ArrayList name;

    int totalEaring = 0; // total earning
    int totalSpending = 0; // total spending
    int profit = 0; // profit
    private Calendar selectedDate; // selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySalesAnalysisBinding.inflate(getLayoutInflater()); // inflate the layout
        context = binding.getRoot().getContext(); // get context
        setContentView(binding.getRoot()); // set the view

        // setup actionbar and adding back press button
        setSupportActionBar(binding.toolbarSalesAnalysis); // setting actionbar
        ActionBar actionBar = getSupportActionBar(); // getting actionbar
        assert actionBar != null; // checking if actionbar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

//        mapTransaction = (HashMap<String, ArrayList<DbTransactionModel>>) getIntent().getSerializableExtra("mapTransaction");
        mapTransaction = new HashMap<>(); // initialize map
        if(Params.getOwnerModel().getMapTransaction() != null) // check if map is not null
            mapTransaction.putAll(Params.getOwnerModel().getMapTransaction()); // get map from owner model
//        products = new HashMap<>();
//        arrEarning = new ArrayList<>();
//        arrSpending = new ArrayList<>();

//        for (String date : mapTransaction.keySet()) {
//            for (DbTransactionModel transaction : mapTransaction.get(date)) {
//                for (SelectItemModel itemModel : transaction.getITEM_LIST()) {
//                    if (transaction.getIsPurchase().equals("false")) {
//                        if (!products.containsKey(itemModel.getId()))
//                            products.put(itemModel.getId(), itemModel.getQuantity());
//                        else {
//                            int quantity = Integer.parseInt(products.get(itemModel.getId())) + Integer.parseInt(itemModel.getQuantity());
//                            products.put(itemModel.getId(), String.valueOf(quantity));
//                        }
//                    }
//                }
//            }
//        }

        // dialog for date
        selectedDate = Calendar.getInstance(); // get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // date format
        binding.DateShow.setText(dateFormat.format(selectedDate.getTime())); // set date to textview
        binding.DateShow.setText("All Records"); // set text to textview
        binding.TransactionHistoryDate.setOnClickListener(v -> OpenDialog()); // open dialog for date

        // setting data to pie chart

        setData("All Record"); // set data to pie chart
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context); // back press event
    }

    private void setData(String date) {
        totalEaring = 0; // initialize total earning
        totalSpending = 0; // initialize total spending
        profit = 0; // initialize profit
        if (date.equals("All Record")) { // check if date is all record
            for (String key : mapTransaction.keySet()) { // loop through map
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(key)) { // loop through transaction
                    if (dbTransactionModel.getIsPurchase().equals("true")) { // check if transaction is purchase
                        totalSpending += Integer.parseInt(dbTransactionModel.getTotal_price()); // add total spending
                    } else { // if transaction is not purchase
                        totalEaring += Integer.parseInt(dbTransactionModel.getTotal_price()); // add total earning
                        profit += calculateProfit(dbTransactionModel.getITEM_LIST()); // calculate profit
                    }
                }
            }
        } else { // if date is not all record
            if(mapTransaction.containsKey(date)) { // check if map contains date
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(date)) { // loop through transaction
                    if (dbTransactionModel.getIsPurchase().equals("true")) { // check if transaction is purchase
                        totalSpending += Integer.parseInt(dbTransactionModel.getTotal_price()); // add total spending
                    } else { // if transaction is not purchase
                        totalEaring += Integer.parseInt(dbTransactionModel.getTotal_price()); // add total earning
                        profit += calculateProfit(dbTransactionModel.getITEM_LIST()); // calculate profit
                    }
                }
            }
        }
        setDiagram(); // set diagram
    }

    // open dialog for date
    private void OpenDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth); // set selected date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // date format
                        binding.DateShow.setText(dateFormat.format(selectedDate.getTime())); // set date to textview
                        setData(dateFormat.format(selectedDate.getTime())); // set data to pie chart
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        // set button to dialog
        datePickerDialog.setButton2("All Record", (dialog, which) -> {
            binding.DateShow.setText("All Records"); // set text to textview
            setData("All Record"); // set data to pie chart
        });
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // set max date
        datePickerDialog.show(); // show dialog
    }

    @SuppressLint("SetTextI18n")
    private void setDiagram() {
        binding.txtTotalEarning.setText("₹ "+totalEaring); // set total earning
        binding.txtTotalSpending.setText("₹ "+totalSpending); // set total spending
        binding.txtProfit.setText("₹ "+profit); // set profit
        binding.pieChart.clearChart(); // clear pie chart

        // Set the data and color to the pie chart
        binding.pieChart.addPieSlice( // add pie slice
                new PieModel( // pie model
                        "Total Revenue",
                        totalEaring,
                        Color.parseColor("#008000")));
        binding.pieChart.addPieSlice(
                new PieModel(
                        "Total Expenses",
                        totalSpending,
                        Color.parseColor("#E01C29")));

        // To animate the pie chart
        binding.pieChart.startAnimation();
    }

    // calculate profit
    private int calculateProfit(List<SelectItemModel> arrProducts) {
        int totalProfit = 0; // initialize total profit
        for (SelectItemModel product : arrProducts) { // loop through products
            // calculate profit
            totalProfit += (Integer.parseInt(product.getSale_price()) - Integer.parseInt(product.getPurchase_price())) * Integer.parseInt(product.getQuantity());
        }
        return totalProfit; // return total profit
    }
}