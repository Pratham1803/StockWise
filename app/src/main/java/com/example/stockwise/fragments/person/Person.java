package com.example.stockwise.fragments.person;

import android.content.Context;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.model.PersonModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Person {
    private final Context context; // context of activity
    private AlertDialog alertDialog; // alert dialog
    private SweetAlertDialog sweetAlertDialog; // sweet alert dialog
    private final LayoutInflater inflater; // layout inflater

    // constructor
    public Person(Context context, LayoutInflater inflater) {
        this.context = context; // setting context
        this.inflater = inflater; // setting layout inflater
    }

    // add person to firebase
    public void addPerson() {
        AlertDialog.Builder builder = DialogBuilder.showDialog(context, "Add Contact", ""); // create alert dialog
        builder.setMessage(null); // set message to null

        // Inflate the custom layout for the dialog
        View view = inflater.inflate(R.layout.add_contact_dialog, null); // inflating layout
        EditText edContactName = view.findViewById(R.id.edContactName); // getting contact name
        EditText edContactNum = view.findViewById(R.id.editTextPhone); // getting contact number
        CountryCodePicker countryCodePicker = view.findViewById(R.id.country_codeDialog); // getting country code
        countryCodePicker.registerCarrierNumberEditText(edContactNum); // collect country code with contact number
        RadioButton rbMale = view.findViewById(R.id.rbMale); // getting radio button Male
        RadioButton rbFemale = view.findViewById(R.id.rbFemale); // getting radio button Female
        Button btnCustomer = view.findViewById(R.id.btnCustomer); // getting button Customer
        Button btnVendor = view.findViewById(R.id.btnVendor); // getting button Vendor

        builder.setView(view); // set view to alert dialog
        rbMale.setChecked(true); // set radio male checked

        edContactName.setFocusable(true); // focus on contact name

        // focus change on contact name, when there is no any text in box
        edContactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // when focus is lost
                    if (edContactName.getText().toString().isEmpty()) { // if no text in box
                        edContactName.setError("Contact name is required"); // show error message
                    }
                }
            }
        });

        // on text change on contact number, when there is invalid number
        edContactNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mobileNum = edContactNum.getText().toString().replace(" ", ""); // getting contact number
                if (mobileNum.length() > 10) { // if contact number is greater than 10
                    edContactNum.requestFocus(); // focus on contact number
                    edContactNum.setError("Invalid contact number"); // show error message
                }
                if (mobileNum.length() > 10) { // if contact number is equal to 10
                    edContactNum.requestFocus(); // focus on contact number
                    edContactNum.setError("Invalid contact number"); // show error message
                }
            }
        });

        // on click on customer button
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = countryCodePicker.getFullNumberWithPlus().replace(" ", ""); // getting full contact number

                // if contact name or contact number is empty or contact number is greater than 13
                if (edContactName.getText().toString().isEmpty() || edContactNum.getText().toString().isEmpty() || mobileNum.length() > 13) {
                    Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show(); // show error message
                    return;
                }

                // create person model
                PersonModel personModel = new PersonModel();
                personModel.setName(edContactName.getText().toString()); // set name to person model
                personModel.setContact_num(mobileNum); // set contact number to person model
                personModel.setGender(rbMale.isChecked() ? rbMale.getText().toString() : rbFemale.getText().toString()); // set gender to person model
                isPersonAvailable(Params.getCUSTOMER(), personModel); // check that person given by user is there or not in database
            }
        });

        // on click on vendor button
        btnVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = countryCodePicker.getFullNumberWithPlus().replace(" ", ""); // getting full contact number

                // if contact name or contact number is empty or contact number is greater than 13
                if (edContactName.getText().toString().isEmpty() || edContactNum.getText().toString().isEmpty() || mobileNum.length() > 13) {
                    Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show(); // show error message
                    return;
                }

                // create person model
                PersonModel personModel = new PersonModel();
                personModel.setName(edContactName.getText().toString()); // set name to person model
                personModel.setContact_num(mobileNum); // set contact number to person model
                personModel.setGender(rbMale.isChecked() ? rbMale.getText().toString() : rbFemale.getText().toString()); // set gender
                isPersonAvailable(Params.getVENDOR(), personModel); // check that person given by user is there or not in database
            }
        });

        // Show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    // check that person given by user is there or not in database
    // if not then add, or show error
    private void isPersonAvailable(String personType, PersonModel personModel) {
        // show process dialog
        sweetAlertDialog = DialogBuilder.showSweetDialogProcess(context, "Please wait", "Checking contact number");

        DatabaseReference databaseReference; // database reference
        if (personType.equals(Params.getVENDOR())) // if person type is vendor
            databaseReference = Params.getREFERENCE().child(Params.getPERSON()).child(Params.getVENDOR()); // get vendor reference
        else // if person type is customer
            databaseReference = Params.getREFERENCE().child(Params.getPERSON()).child(Params.getCUSTOMER()); // get customer reference

        // get all data from firebase
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                boolean flag = false; // to check if contact number already exists
                if (dataSnapshot.exists()) { // if data exists
                    // check if contact number already exists in database
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PersonModel newPerson = snapshot.getValue(PersonModel.class); // get person model

                        // if contact number and name already exists
                        if (newPerson.getContact_num().equals(personModel.getContact_num()) && newPerson.getName().equals(personModel.getName())) {
                            flag = true; // set flag to true
                            break; // break the loop
                        }
                    }
                }

                if (flag) {
                    // if contact number already exists
                    sweetAlertDialog.dismiss(); // dismiss process dialog
                    // show error dialog
                    sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Contact number already exists");
                    alertDialog.dismiss(); // dismiss alert dialog
                } else { // if contact number does not exists
                    dbAddPerson(databaseReference, personModel); // add person to firebase
                }
            }
        });
    }

    // add person to firebase
    private void dbAddPerson(DatabaseReference databaseReference, PersonModel personModel) {
        databaseReference = databaseReference.push(); // push data to database
        personModel.setId(databaseReference.getKey()); // set id to person model

        // add person to firebase
        databaseReference.setValue(personModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sweetAlertDialog.dismiss(); // dismiss process dialog

                // show success dialog
                sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Success", "Contact added successfully");
                alertDialog.dismiss(); // dismiss alert dialog
            }
        });
    }

    // get all contacts from firebase and set in array list
    public void getAllPerson(ArrayList<PersonModel> arrAllPerson, ArrayList<PersonModel> arrAllVendors, ArrayList<PersonModel> arrAllCustomers, PersonAdapter personAdapter) {
        DatabaseReference parentRef =  Params.getREFERENCE().child(Params.getPERSON()); // get reference of person

        // collecting all customers from firebase
        parentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrAllCustomers.clear(); // clear all customers
                arrAllVendors.clear(); // clear all vendors
                arrAllPerson.clear(); // clear all persons

                // get all customers and vendors from firebase
                if (snapshot.child(Params.getCUSTOMER()).exists()) { // if customers exists
                    // get all customers
                    for (DataSnapshot dataSnapshot : snapshot.child(Params.getCUSTOMER()).getChildren()) {
                        PersonModel personModel = dataSnapshot.getValue(PersonModel.class); // get person model
                        arrAllCustomers.add(personModel); // add person model to array list of customer
                    }
                }
                // get all vendors from firebase
                if (snapshot.child(Params.getVENDOR()).exists()) { // if vendors exists
                    // get all vendors
                    for (DataSnapshot dataSnapshot : snapshot.child(Params.getVENDOR()).getChildren()) {
                        PersonModel personModel = dataSnapshot.getValue(PersonModel.class); // get person model
                        arrAllVendors.add(personModel); // add person model to array list of vendor
                    }
                }
                arrAllPerson.addAll(arrAllCustomers); // add all customers to all persons
                arrAllPerson.addAll(arrAllVendors); // add all vendors to all persons
                personAdapter.setLocalDataset(arrAllPerson); // set all persons to adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // show error message
                Log.d("ErrorMsg", "onCancelled: "+error.getMessage());
            }
        });
    }
}