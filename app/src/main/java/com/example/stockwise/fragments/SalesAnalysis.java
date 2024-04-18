package com.example.stockwise.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

public class SalesAnalysis extends AppCompatActivity {
    private ActivitySalesAnalysisBinding binding;
    private Context context;
    // variable for our bar chart
    private HashMap<String, ArrayList<DbTransactionModel>> mapTransaction;
    private HashMap<String, String> products;
    private ArrayList<Integer> arrEarning;
    private ArrayList<Integer> arrSpending;
    private ArrayList name;

    int totalEaring = 0;
    int totalSpending = 0;
    int profit = 0;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySalesAnalysisBinding.inflate(getLayoutInflater());
        context = binding.getRoot().getContext();
        setContentView(binding.getRoot());

        // setup actionbar and adding back press button
        setSupportActionBar(binding.toolbarSalesAnalysis);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

//        mapTransaction = (HashMap<String, ArrayList<DbTransactionModel>>) getIntent().getSerializableExtra("mapTransaction");
        mapTransaction = new HashMap<>();
        mapTransaction.putAll(Params.getOwnerModel().getMapTransaction());
        products = new HashMap<>();
        arrEarning = new ArrayList<>();
        arrSpending = new ArrayList<>();

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
        selectedDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        binding.DateShow.setText(dateFormat.format(selectedDate.getTime()));
        binding.DateShow.setText("All Records");
        binding.TransactionHistoryDate.setOnClickListener(v -> OpenDialog());

        // setting data to pie chart

        setData("All Record");
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }

    private void setData(String date) {
        totalEaring = 0;
        totalSpending = 0;
        profit = 0;
        if (date.equals("All Record")) {
            for (String key : mapTransaction.keySet()) {
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(key)) {
                    if (dbTransactionModel.getIsPurchase().equals("true")) {
                        totalSpending += Integer.parseInt(dbTransactionModel.getTotal_price());
                    } else {
                        totalEaring += Integer.parseInt(dbTransactionModel.getTotal_price());
                        profit += calculateProfit(dbTransactionModel.getITEM_LIST());
                    }
                }
            }
        } else {
            if(mapTransaction.containsKey(date)) {
                for (DbTransactionModel dbTransactionModel : mapTransaction.get(date)) {
                    if (dbTransactionModel.getIsPurchase().equals("true")) {
                        totalSpending += Integer.parseInt(dbTransactionModel.getTotal_price());
                    } else {
                        totalEaring += Integer.parseInt(dbTransactionModel.getTotal_price());
                        profit += calculateProfit(dbTransactionModel.getITEM_LIST());
                    }
                }
            }
        }
        setDiagram();
    }

    private void OpenDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        binding.DateShow.setText(dateFormat.format(selectedDate.getTime()));
                        setData(dateFormat.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setButton2("All Record", (dialog, which) -> {
            binding.DateShow.setText("All Records");
            setData("All Record");
        });
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setDiagram() {
        binding.txtTotalEarning.setText("₹ "+totalEaring);
        binding.txtTotalSpending.setText("₹ "+totalSpending);
        binding.txtProfit.setText("₹ "+profit);
        binding.pieChart.clearChart();
        // Set the data and color to the pie chart
        binding.pieChart.addPieSlice(
                new PieModel(
                        "Total Earning",
                        totalEaring,
                        Color.parseColor("#66BB6A")));
        binding.pieChart.addPieSlice(
                new PieModel(
                        "Total Spending",
                        totalSpending,
                        Color.parseColor("#FFA726")));

        // To animate the pie chart
        binding.pieChart.startAnimation();
    }

    // calculate profit
    private int calculateProfit(List<SelectItemModel> arrProducts) {
        int totalProfit = 0;
        for (SelectItemModel product : arrProducts) {
            totalProfit += (Integer.parseInt(product.getSale_price()) - Integer.parseInt(product.getPurchase_price())) * Integer.parseInt(product.getQuantity());
        }
        return totalProfit;
    }
}