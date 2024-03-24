package com.example.stockwise.fragments.transaction;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.databinding.ActivityProductListBinding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        setContentView(bind.getRoot());

        Intent intent = getIntent();
        transactionModel = (TransactionModel) intent.getSerializableExtra("transactionObj");
        assert transactionModel != null;
        transactionModel.setDbTransactionModel(new DbTransactionModel());
        transactionModel.getDbTransactionModel().setPerson_id(transactionModel.getPerson().getId());
        transactionModel.getDbTransactionModel().setDate(transactionModel.getDate());
        transactionModel.getDbTransactionModel().setIsPurchase(transactionModel.isPurchase() ? "true" : "false");

        // setting visibilities
        bind.linearLayoutCategory.setVisibility(View.GONE);
        bind.btnProceed.setVisibility(View.VISIBLE);

        // setting action bar title
        setSupportActionBar(bind.toolbarCategory);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Select Items"); // setting action bar title
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button in action bar

        // setting recycler view
        arrAllProduct = new ArrayList<>();
        selectItemAdapter = new SelectItem_Adapter(arrAllProduct, context, transactionModel); // initializing adapter
        bind.recyclerProductCategory.setAdapter(selectItemAdapter); // setting adapter to recycler view
        bind.recyclerProductCategory.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager to recycler view

        dbGetAllProducts();

        // process button clicked
        bind.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transactionModel.getITEM_LIST().size() > 0) {
                    sweetAlertDialog = DialogBuilder.showSweetDialogProcess(context, "Adding Transaction", "Please wait...");
                    dbAddTransaction();
                } else {
                    Toast.makeText(context, "Please select at least one item", Toast.LENGTH_LONG).show();
                }
            }
        });
    }// End OnCreate

    private void dbAddTransaction() {
        DatabaseReference ref = Params.getREFERENCE().child(Params.getTRANSACTION()).child(transactionModel.getDate()).push();
        transactionModel.getDbTransactionModel().setId(ref.getKey());

        int totalPrice = 0;
        for(SelectItemModel item : transactionModel.getDbTransactionModel().getITEM_LIST()) {
            totalPrice += Integer.parseInt(item.getPrice())*Integer.parseInt(item.getQuantity());
        }

        transactionModel.getDbTransactionModel().setTotal_price(String.valueOf(totalPrice));

        ref.setValue(transactionModel.getDbTransactionModel()).addOnSuccessListener(aVoid -> {
            dbUpdateProduct();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_LONG).show();
            Log.d("ErrorMsg", "dbAddTransaction: " + e.getMessage());
        });
    }

    private void dbUpdateProduct() {
        DatabaseReference ref = Params.getREFERENCE().child(Params.getPRODUCT());

        Map<String, Object> itemMap = new HashMap<>();
        for(ProductModel item : transactionModel.getITEM_LIST()) {
            itemMap.put(item.getId()+"/"+Params.getCurrentStock(), item.getCurrent_stock());
            if(Objects.equals(item.getCurrent_stock(), "0"))
                itemMap.put(item.getId()+"/isOutOfStock", "true");
            else if (Integer.parseInt(item.getCurrent_stock()) < Integer.parseInt(item.getReorder_point()))
                itemMap.put(item.getId()+"/isReorderPointReached", "true");
        }

        ref.updateChildren(itemMap).addOnSuccessListener(aVoid -> {
            sweetAlertDialog.dismissWithAnimation();
            sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Transaction Added", "Transaction has been added successfully");
            sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    finish();
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_LONG).show();
            Log.d("ErrorMsg", "dbUpdateProduct: " + e.getMessage());
        });

    }

    //  back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (isProductScanned) {
            isProductScanned = false;
            selectItemAdapter.setLocalDataSet(arrAllProduct);
        } else
            MainToolbar.btnBack_clicked(item, context);
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

                for (DataSnapshot post : snapshot.getChildren()) {
                    ProductModel newProduct = post.getValue(ProductModel.class); // storing product details in productModule class object
                    assert newProduct != null;
                    newProduct.setCategory_id(newProduct.getCategory());
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
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            MainToolbar.searchProduct_Transaction_Barcode(arrAllProduct, selectItemAdapter, barCodeId);
            isProductScanned = true; // setting product scanned status to true
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // setting listener to, create action bar
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        // getting addProduct button item from actionbar
        MenuItem btnAddProduct = menu.findItem(R.id.addProduct);
        btnAddProduct.setVisible(false); // hiding add product button from action bar
        MenuItem btnSearch = menu.findItem(R.id.search);
        MenuItem btnScan = menu.findItem(R.id.scanner);
        SearchView searchView = (SearchView) btnSearch.getActionView();

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                scanner = MainToolbar.getScanner();
                bar.launch(scanner); // launching the scanner
                return true;
            }
        });

        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainToolbar.btnSearch_Transaction(query.toLowerCase(), arrAllProduct, selectItemAdapter);
                Log.d("SuccessMsg", "onQueryTextSubmit: Text = " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    MainToolbar.btnSearch_Transaction(newText.toLowerCase(), arrAllProduct, selectItemAdapter);
                    Log.d("SuccessMsg", "onQueryTextChange: Text = " + newText);
                } else if (newText.length() == 0) {
                    selectItemAdapter.setLocalDataSet(arrAllProduct);
                }
                return true;
            }
        });

        // setting on click lister in add product item
        btnAddProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // starting activity of add product screen
                startActivity(new Intent(context, AddProduct.class));
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (isProductScanned) {
            isProductScanned = false;
            selectItemAdapter.setLocalDataSet(arrAllProduct);
        } else
            super.onBackPressed();
    }
}