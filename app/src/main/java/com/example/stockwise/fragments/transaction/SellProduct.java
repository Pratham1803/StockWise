package com.example.stockwise.fragments.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivitySellProductBinding;
import com.example.stockwise.fragments.person.Person;
import com.example.stockwise.model.PersonModel;
import com.example.stockwise.model.TransactionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SellProduct extends AppCompatActivity {
    private ActivitySellProductBinding bind; // declaring view binding
    private Context context; // to store context
    private Calendar selectedDate;
    private ArrayList<PersonModel> arrPerson; // to store person data
    private ArrayList<String> arrPersonName; // to store product datar
    private ArrayAdapter adapterPersonName;
    private TransactionModel transactionModel;
    private boolean isPurchasing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivitySellProductBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        transactionModel = new TransactionModel(); // initializing transaction model
        setContentView(bind.getRoot());

        // collecting intent that it is a purchasing or selling process
        isPurchasing = getIntent().getBooleanExtra("isPurchasing", false);

        // setting action bar title
        setSupportActionBar(bind.toolbarSellProduct);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(isPurchasing) {
            actionBar.setTitle("Purchase Product");
        }

        // Setup Spinner for Entering Person Name
        arrPerson = new ArrayList<>();
        arrPersonName = new ArrayList<>();
        adapterPersonName = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrPersonName);
        bind.spPerson.setAdapter(adapterPersonName);

        // Fetching Person Data from Firebase
        DatabaseReference dbRef = Params.getREFERENCE().child(Params.getPERSON());
        if(isPurchasing){
            dbRef = dbRef.child(Params.getVENDOR());
        }else {
            dbRef = dbRef.child(Params.getCUSTOMER());
        }
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPersonName.clear();
                arrPerson.clear();

                if(isPurchasing)
                    arrPersonName.add("Select Vendor");
                else
                    arrPersonName.add("Select Customer");

                for (DataSnapshot post : snapshot.getChildren()) {
                    PersonModel personModel = post.getValue(PersonModel.class);
                    arrPerson.add(personModel);
                    arrPersonName.add(personModel.getName());
                }
                adapterPersonName.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: "+error.getMessage());
            }
        });

        // add person
        bind.btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person addPerson = new Person(context, getLayoutInflater());
                addPerson.addPerson();
            }
        });

        // setup date
        selectedDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        bind.DateShow.setText(dateFormat.format(selectedDate.getTime()));
        bind.TransactionDate.setOnClickListener(v -> OpenDialog());

        // setup add item button
        bind.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.spPerson.getSelectedItem().toString().equals("Select Customer Name")) {
                    bind.spPerson.requestFocus();
                    Toast.makeText(context, "Please select customer", Toast.LENGTH_SHORT).show();
                    return;
                }

                transactionModel.setDate(bind.DateShow.getText().toString());
                transactionModel.setPerson(arrPerson.get(bind.spPerson.getSelectedItemPosition()-1));

                if(transactionModel.getDate().equals("")){
                    bind.TransactionDate.requestFocus();
                    Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show();
                    return;
                }

                transactionModel.setITEM_LIST(new ArrayList<>());
                transactionModel.setPurchase(isPurchasing);
                Intent intent = new Intent(context, Select_Items.class);
                intent.putExtra("transactionObj", transactionModel);
                startActivity(intent);
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
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}