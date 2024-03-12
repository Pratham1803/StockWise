package com.example.stockwise.fragments.transaction;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.stockwise.MainActivity;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivitySellProductBinding;
import com.example.stockwise.fragments.person.AddPerson;
import com.example.stockwise.fragments.person.PersonFragment;
import com.example.stockwise.model.PersonModel;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SellProduct extends AppCompatActivity {
    private ActivitySellProductBinding bind; // declaring view binding
    private Context context; // to store context
    private Calendar selectedDate;
    private ScanOptions scanner; // scanner
    private String barCodeId; // to store barcode id
    private ArrayList<PersonModel> arrPerson; // to store person data
    private ArrayList<ProductModel> arrProduct; // to store product data
    private ArrayList<String> arrPersonName; // to store product data
    private ProductSellAdapter productSellAdapter; // to store adapter
    private ArrayAdapter adapterPersonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivitySellProductBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        setContentView(bind.getRoot());

        // setting action bar title
        setSupportActionBar(bind.toolbarSellProduct);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup Spinner for Entering Person Name
        arrPerson = new ArrayList<>();
        arrPersonName = new ArrayList<>();
        adapterPersonName = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrPersonName);
        bind.spPerson.setAdapter(adapterPersonName);
        Params.getREFERENCE().child(Params.getPERSON()).child(Params.getCUSTOMER()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPersonName.clear();
                arrProduct.clear();
                arrPersonName.add("Select Customer Name");
                for (DataSnapshot post : snapshot.getChildren()) {
                    PersonModel personModel = post.getValue(PersonModel.class);
                    arrPerson.add(personModel);
                    arrPersonName.add(personModel.getName());
                }
                adapterPersonName.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // add person
        bind.btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPerson addPerson = new AddPerson(context, getLayoutInflater());
                addPerson.displayAddPersonDialog();
            }
        });

        // setup date
        selectedDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
        bind.TransactionDate.setOnClickListener(v -> OpenDialog());

        // setup add item button
        bind.btnAddItem.setOnClickListener(v -> btnAddItem_click());

        // setup recycler view
        arrProduct = new ArrayList<>();
        productSellAdapter = new ProductSellAdapter(arrProduct, context);
        bind.recyclerViewSellProductList.setAdapter(productSellAdapter);
        bind.recyclerViewSellProductList.setLayoutManager(new LinearLayoutManager(context));

        bind.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SuccessMsg", "onClick: "+arrProduct.size());
            }
        });
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }

    // open date picker dialog
    private void OpenDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // add item Clicked
    private void btnAddItem_click() {
//        if (bind.spPerson.getSelectedItemPosition() == 0) {
//            Toast.makeText(context, "Please select customer name", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (bind.DateShow.getText().toString().equals("Select Date")) {
//            Toast.makeText(context, "Please select transaction date", Toast.LENGTH_SHORT).show();
//            return;
//        }

        scanner = MainToolbar.getScanner();
        bar.launch(scanner);
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            Params.getREFERENCE().child(Params.getPRODUCT()).child(barCodeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ProductModel productModel = snapshot.getValue(ProductModel.class);
                    arrProduct.add(0,productModel);
                    productSellAdapter.notifyItemInserted(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        // scanner does not have any results
        else
            Toast.makeText(context, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });
}