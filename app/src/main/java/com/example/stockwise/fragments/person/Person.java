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
    private final Context context;
    private AlertDialog alertDialog;
    private SweetAlertDialog sweetAlertDialog;
    private final LayoutInflater inflater;

    public Person(Context context, LayoutInflater inflater) {
        this.context = context;
        this.inflater = inflater;
    }

    public void addPerson() {
        AlertDialog.Builder builder = DialogBuilder.showDialog(context, "Add Contact", "");
        builder.setMessage(null);

        // Inflate the custom layout for the dialog
        View view = inflater.inflate(R.layout.add_contact_dialog, null);
        EditText edContactName = view.findViewById(R.id.edContactName);
        EditText edContactNum = view.findViewById(R.id.editTextPhone);
        CountryCodePicker countryCodePicker = view.findViewById(R.id.country_codeDialog);
        countryCodePicker.registerCarrierNumberEditText(edContactNum); // collect country code
        RadioButton rbMale = view.findViewById(R.id.rbMale);
        RadioButton rbFemale = view.findViewById(R.id.rbFemale);
        Button btnCustomer = view.findViewById(R.id.btnCustomer);
        Button btnVendor = view.findViewById(R.id.btnVendor);

        builder.setView(view);
        rbMale.setChecked(true);

        edContactName.setFocusable(true);

        // focus change on contact name, when there is no any text in box
        edContactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (edContactName.getText().toString().isEmpty()) {
                        edContactName.setError("Contact name is required");
                    }
                }
            }
        });

        // on text change on contact number, when there is invalid number
        edContactNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mobileNum = edContactNum.getText().toString().replace(" ", "");
                if (mobileNum.length() > 10) {
                    edContactNum.requestFocus();
                    edContactNum.setError("Invalid contact number");
                }
            }
        });

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = countryCodePicker.getFullNumberWithPlus().replace(" ", ""); // getting full contact number
                if (edContactName.getText().toString().isEmpty() || edContactNum.getText().toString().isEmpty() || mobileNum.length() > 13) {
                    Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                PersonModel personModel = new PersonModel();
                personModel.setName(edContactName.getText().toString());
                personModel.setContact_num(mobileNum);
                personModel.setGender(rbMale.isChecked() ? rbMale.getText().toString() : rbFemale.getText().toString());
                isPersonAvailable(Params.getCUSTOMER(), personModel);
            }
        });

        btnVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = countryCodePicker.getFullNumberWithPlus().replace(" ", ""); // getting full contact number
                if (edContactName.getText().toString().isEmpty() || edContactNum.getText().toString().isEmpty() || mobileNum.length() > 13) {
                    Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                PersonModel personModel = new PersonModel();
                personModel.setName(edContactName.getText().toString());
                personModel.setContact_num(mobileNum);
                personModel.setGender(rbMale.isChecked() ? rbMale.getText().toString() : rbFemale.getText().toString());
                isPersonAvailable(Params.getVENDOR(), personModel);
            }
        });

        // Show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    // check that person given by user is there or not in database
    // if not then add, or show error
    private void isPersonAvailable(String personType, PersonModel personModel) {
        sweetAlertDialog = DialogBuilder.showSweetDialogProcess(context, "Please wait", "Checking contact number");

        DatabaseReference databaseReference;
        if (personType.equals(Params.getVENDOR()))
            databaseReference = Params.getREFERENCE().child(Params.getPERSON()).child(Params.getVENDOR());
        else
            databaseReference = Params.getREFERENCE().child(Params.getPERSON()).child(Params.getCUSTOMER());
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                boolean flag = false; // to check if contact number already exists
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PersonModel newPerson = snapshot.getValue(PersonModel.class);
                        if (newPerson.getContact_num().equals(personModel.getContact_num())) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    sweetAlertDialog.dismiss();
                    sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Contact number already exists");
                    alertDialog.dismiss();
                } else {
                    dbAddPerson(databaseReference, personModel);
                }
            }
        });
    }

    // add person to firebase
    private void dbAddPerson(DatabaseReference databaseReference, PersonModel personModel) {
        databaseReference = databaseReference.push();
        personModel.setId(databaseReference.getKey());
        databaseReference.setValue(personModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sweetAlertDialog.dismiss();
                sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Success", "Contact added successfully");
                alertDialog.dismiss();
            }
        });
    }

    // get all contacts from firebase and set in array list
    public void getAllPerson(ArrayList<PersonModel> arrAllPerson, ArrayList<PersonModel> arrAllVendors, ArrayList<PersonModel> arrAllCustomers, PersonAdapter personAdapter) {
        DatabaseReference parentRef =  Params.getREFERENCE().child(Params.getPERSON());

        // collecting all customers from firebase
        parentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrAllCustomers.clear();
                arrAllVendors.clear();
                arrAllPerson.clear();
                if (snapshot.child(Params.getCUSTOMER()).exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child(Params.getCUSTOMER()).getChildren()) {
                        PersonModel personModel = dataSnapshot.getValue(PersonModel.class);
                        arrAllCustomers.add(personModel);
                    }
                }
                if (snapshot.child(Params.getVENDOR()).exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child(Params.getVENDOR()).getChildren()) {
                        PersonModel personModel = dataSnapshot.getValue(PersonModel.class);
                        arrAllVendors.add(personModel);
                    }
                }
                arrAllPerson.addAll(arrAllCustomers);
                arrAllPerson.addAll(arrAllVendors);
                personAdapter.setLocalDataset(arrAllPerson);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: "+error.getMessage());
            }
        });
    }
}