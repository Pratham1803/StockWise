package com.example.stockwise.fragments.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
    private Calendar selectedDate; // to store selected date
    private ArrayList<PersonModel> arrPerson; // to store person data
    private ArrayList<String> arrPersonName; // to store product datar
    private ArrayAdapter adapterPersonName; // to store adapter for spinner
    private TransactionModel transactionModel; // to store transaction data
    private boolean isPurchasing; // to store whether it is purchasing or selling process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivitySellProductBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // initializing context
        transactionModel = new TransactionModel(); // initializing transaction model
        setContentView(bind.getRoot()); // setting view

        // collecting intent that it is a purchasing or selling process
        isPurchasing = getIntent().getBooleanExtra("isPurchasing", false); // fetching intent data

        // setting action bar title
        setSupportActionBar(bind.toolbarSellProduct); // setting toolbar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking action bar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

        // setting toolbar title
        if(isPurchasing) { // checking whether it is purchasing or selling process
            actionBar.setTitle("Purchase Product"); // setting title
        }

        // Setup Spinner for Entering Person Name
        arrPerson = new ArrayList<>(); // initializing array list
        arrPersonName = new ArrayList<>(); // initializing array list
        adapterPersonName = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrPersonName); // initializing adapter
        bind.spPerson.setAdapter(adapterPersonName); // setting adapter

        // Fetching Person Data from Firebase
        DatabaseReference dbRef = Params.getREFERENCE().child(Params.getPERSON()); // getting reference of person
        if(isPurchasing){ // checking whether it is purchasing or selling process
            dbRef = dbRef.child(Params.getVENDOR()); // getting reference of vendor
        }else { // if it is selling process
            dbRef = dbRef.child(Params.getCUSTOMER()); // getting reference of customer
        }

        // Fetching data from firebase
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrPersonName.clear(); // clearing array list
                arrPerson.clear(); // clearing array list

                // adding default value
                if(isPurchasing) // checking whether it is purchasing or selling process
                    arrPersonName.add("Select Vendor"); // adding vendor
                else // if it is selling process
                    arrPersonName.add("Select Customer"); // adding customer

                // fetching data from firebase
                for (DataSnapshot post : snapshot.getChildren()) { // looping through data
                    PersonModel personModel = post.getValue(PersonModel.class); // getting data
                    arrPerson.add(personModel); // adding data to array list
                    arrPersonName.add(personModel.getName()); // adding data to array list
                }
                adapterPersonName.notifyDataSetChanged(); // notifying adapter
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
                Person addPerson = new Person(context, getLayoutInflater()); // initializing person
                addPerson.addPerson(); // adding person
            }
        });

        // setup date
        selectedDate = Calendar.getInstance(); // initializing calendar
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // initializing date format
        bind.DateShow.setText(dateFormat.format(selectedDate.getTime())); // setting date
        bind.TransactionDate.setOnClickListener(v -> OpenDialog()); // opening date picker dialog

        // setup add item button
        bind.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.spPerson.getSelectedItem().toString().equals("Select Customer Name")) { // checking whether customer is selected or not
                    bind.spPerson.requestFocus(); // setting focus
                    Toast.makeText(context, "Please select customer", Toast.LENGTH_SHORT).show(); // showing toast
                    return;
                }

                transactionModel.setDate(bind.DateShow.getText().toString()); // setting date
                transactionModel.setPerson(arrPerson.get(bind.spPerson.getSelectedItemPosition()-1)); // setting person

                if(transactionModel.getDate().equals("")){ // checking whether date is selected or not
                    bind.TransactionDate.requestFocus(); // setting focus
                    Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show(); // showing toast
                    return;
                }

                // passing data to next activity
                transactionModel.setITEM_LIST(new ArrayList<>()); // initializing array list
                transactionModel.setPurchase(isPurchasing); // setting whether it is purchasing or selling process
                Intent intent = new Intent(context, Select_Items.class); // initializing intent
                intent.putExtra("transactionObj", transactionModel); // passing data
                startActivity(intent); // starting activity
            }
        });
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent resultIntent = new Intent(); // initializing intent
        // You can put extra data in the intent if needed
        setResult(Activity.RESULT_OK, resultIntent); // setting result
        finish(); // finishing activity
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent(); // initializing intent
        // You can put extra data in the intent if needed
        setResult(Activity.RESULT_OK, resultIntent); // setting result
        finish(); // finishing activity
        super.onBackPressed();
    }

    // open date picker dialog
    private void OpenDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth); // setting date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // initializing date format
                        bind.DateShow.setText(dateFormat.format(selectedDate.getTime())); // setting date
                    }
                },
                // setting date
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // setting max date
        datePickerDialog.show(); // showing dialog
    }
}