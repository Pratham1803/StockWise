package com.example.stockwise.fragments.transaction;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.databinding.ActivityProductListBinding;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stockwise.R;
import com.example.stockwise.fragments.product.AddProduct;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.ProductModel;
import com.example.stockwise.model.SelectItemModel;
import com.example.stockwise.model.TransactionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Select_Items extends AppCompatActivity {
    private ActivityProductListBinding bind; // declaring view binding
    private Context context; // to store context
    private ScanOptions scanner; // scanner
    private boolean isProductScanned = false; // to store product scanned status
    private String barCodeId; // to store barcode id
    private ArrayList<ProductModel> arrAllProduct; // to store product data
    private SelectItem_Adapter selectItemAdapter; // to store adapter
    private TransactionModel transactionModel; // to store transaction data
    private SweetAlertDialog sweetAlertDialog; // to store sweet alert dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProductListBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        setContentView(bind.getRoot()); // setting view

        Intent intent = getIntent(); // getting intent
        transactionModel = (TransactionModel) intent.getSerializableExtra("transactionObj"); // getting transaction object from intent
        assert transactionModel != null; // checking transaction object is not null
        transactionModel.setDbTransactionModel(new DbTransactionModel()); // initializing db transaction model
        transactionModel.getDbTransactionModel().setPerson_id(transactionModel.getPerson().getId()); // setting person id
        transactionModel.getDbTransactionModel().setDate(transactionModel.getDate()); // setting date
        transactionModel.getDbTransactionModel().setIsPurchase(transactionModel.isPurchase() ? "true" : "false"); // setting is purchase

        // setting visibilities
        bind.linearLayoutCategory.setVisibility(View.GONE); // hiding category layout
        bind.btnProceed.setVisibility(View.VISIBLE); // showing proceed button

        // setting action bar title
        setSupportActionBar(bind.toolbarCategory); // setting action bar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking action bar is not null
        actionBar.setTitle("Select Items"); // setting action bar title
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button in action bar

        // setting recycler view
        arrAllProduct = new ArrayList<>(); // initializing product arraylist
        selectItemAdapter = new SelectItem_Adapter(arrAllProduct, context, transactionModel); // initializing adapter
        bind.recyclerProductCategory.setAdapter(selectItemAdapter); // setting adapter to recycler view
        bind.recyclerProductCategory.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager to recycler view

        dbGetAllProducts(); // getting all products from firebase

        // process button clicked
        bind.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if any product is selected or not
                if (transactionModel.getITEM_LIST().size() > 0) { // if product is selected
                    Intent intent = new Intent(context, GenerateBill.class); // starting generate bill activity
                    intent.putExtra("CallFrom", "Transaction"); // passing call from
                    intent.putExtra("Name", transactionModel.getPerson().getName()); // passing person name
                    intent.putExtra("transactionObj", transactionModel); // passing transaction object
                    startActivity(intent); // starting activity
                    finish(); // finishing current activity
                } else { // if no product is selected
                    Toast.makeText(context, "Please select at least one item", Toast.LENGTH_LONG).show(); // showing toast message
                }
            }
        });
    }// End OnCreate


    //  back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (isProductScanned) { // if product is scanned
            isProductScanned = false; // setting product scanned status to false
            selectItemAdapter.setLocalDataSet(arrAllProduct); // setting product list to adapter
        } else // if product is not scanned
            MainToolbar.btnBack_clicked(item, context); // calling back button clicked method
        return true;
    }

    // collect all products
    private void dbGetAllProducts() {
        // collecting product list from firebase
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // in snapshot we have all the products list, so we are getting it one by one using for each loop
                arrAllProduct.clear(); // deleting all products from the list

                for (DataSnapshot post : snapshot.getChildren()) { // getting product one by one
                    ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                    assert newProduct != null; // checking product is not null
                    newProduct.setCategory_id(newProduct.getCategory()); // setting category id
                    arrAllProduct.add(newProduct); // adding product in product's arraylist

                    selectItemAdapter.notifyItemInserted(arrAllProduct.size()); // notifying adapter that new product is added
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
            }
        });
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) { // if scanner has some result
            barCodeId = result.getContents(); // collect the barcode number and store it
            MainToolbar.searchProduct_Transaction_Barcode(arrAllProduct, selectItemAdapter, barCodeId); // search product by barcode
            isProductScanned = true; // setting product scanned status to true
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // setting listener to, create action bar
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu); // inflating toolbar menu
        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct); // getting add product button from action bar
        btnAddProduct.setVisible(false); // hiding add product button from action bar
        MenuItem btnSearch = menu.findItem(R.id.search); // getting search button from action bar
        MenuItem btnScan = menu.findItem(R.id.scanner); // getting scanner button from action bar
        SearchView searchView = (SearchView) btnSearch.getActionView(); // getting search view from search button

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text); // getting search edit text
        searchEditText.setTextColor(getResources().getColor(R.color.white)); // setting text color
        searchEditText.setHintTextColor(getResources().getColor(R.color.white)); // setting hint text color

        // setting on click listener in scanner button
        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                scanner = MainToolbar.getScanner(); // getting scanner
                bar.launch(scanner); // launching the scanner
                return true;
            }
        });

        assert searchView != null; // checking search view is not null

        // setting on query text listener in search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searching product by name
                MainToolbar.btnSearch_Transaction(query.toLowerCase(), arrAllProduct, selectItemAdapter); // searching product by name
                Log.d("SuccessMsg", "onQueryTextSubmit: Text = " + query); // showing log message
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // searching product by name
                if (newText.length() > 1) { // if text length is greater than 1
                    MainToolbar.btnSearch_Transaction(newText.toLowerCase(), arrAllProduct, selectItemAdapter); // searching product by name
                    Log.d("SuccessMsg", "onQueryTextChange: Text = " + newText); // showing log message
                } else if (newText.length() == 0) { // if text length is 0
                    selectItemAdapter.setLocalDataSet(arrAllProduct); // setting product list to adapter
                }
                return true;
            }
        });

        // setting on click lister in add product item
        btnAddProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // starting activity of add product screen
                startActivity(new Intent(context, AddProduct.class)); // starting add product activity
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        // if product is scanned
        if (isProductScanned) { // if product is scanned
            isProductScanned = false; // setting product scanned status to false
            selectItemAdapter.setLocalDataSet(arrAllProduct); // setting product list to adapter
        } else // if product is not scanned
            super.onBackPressed(); // calling super method
    }
}