package com.example.stockwise.fragments.person;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentPersonBinding;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.fragments.category.ManageCategory;
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.PersonModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PersonFragment extends Fragment {
    private Context context; // to store context
    private FragmentPersonBinding bind; // bind view
    String[] ContactFilter = {"All Contacts", "Customers", "Vendors"};
    private AlertDialog alertDialog; // to store alert dialog
    private SweetAlertDialog sweetAlertDialog; // to store sweet alert dialog

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentPersonBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext();

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, ContactFilter);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterContacts.setAdapter(ad);

        // click on add contact button
        bind.btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                showAddPersonDialog();
            }
        });

        // Inflate the layout for this fragment
        return bind.getRoot();
    }

    // add person button click
    private void showAddPersonDialog(){
        AlertDialog.Builder builder = DialogBuilder.showDialog(context, "Add Contact", "");
        builder.setMessage(null);

        LayoutInflater inflater = getLayoutInflater();

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
                    if(edContactName.getText().toString().isEmpty()) {
                        edContactName.setError("Contact name is required");
                    }
                }
            }
        });

        // on text change on contact number, when there is invalid number
        edContactNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mobileNum = edContactNum.getText().toString().replace(" ","");
                if(mobileNum.length() > 10) {
                    edContactNum.requestFocus();
                    edContactNum.setError("Invalid contact number");
                }
            }
        });

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = countryCodePicker.getFullNumberWithPlus().replace(" ",""); // getting full contact number
                if(edContactName.getText().toString().isEmpty() || edContactNum.getText().toString().isEmpty() || mobileNum.length() > 13){
                    Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                PersonModel personModel = new PersonModel();
                personModel.setName(edContactName.getText().toString());
                personModel.setContact_num(mobileNum);
                personModel.setGender(rbMale.isChecked() ? rbMale.getText().toString() : rbFemale.getText().toString());
                isPersonAvailable(Params.getCUSTOMER(),personModel);
            }
        });

        btnVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNum = countryCodePicker.getFullNumberWithPlus().replace(" ",""); // getting full contact number
                if (edContactName.getText().toString().isEmpty() || edContactNum.getText().toString().isEmpty() || mobileNum.length() > 13){
                    Toast.makeText(context, "Invalid Details!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                PersonModel personModel = new PersonModel();
                personModel.setName(edContactName.getText().toString());
                personModel.setContact_num(mobileNum);
                personModel.setGender(rbMale.isChecked() ? rbMale.getText().toString() : rbFemale.getText().toString());
                isPersonAvailable(Params.getVENDOR(),personModel);
            }
        });

        // Show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    // check that person given by user is there or not in database
    // if not then add, or show error
    private void isPersonAvailable(String personType,PersonModel personModel){
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText("Please wait");
        sweetAlertDialog.show();

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
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Contact number already exists")
                            .show();
                    sweetAlertDialog.cancel();
                    alertDialog.dismiss();
                }else {
                    dbAddPerson(databaseReference,personModel);
                }
            }
        });
    }

    // add person to firebase
    private void dbAddPerson(DatabaseReference databaseReference, PersonModel personModel){
        databaseReference = databaseReference.push();
        personModel.setId(databaseReference.getKey());
        databaseReference.setValue(personModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setContentText("Contact added successfully")
                        .show();
                sweetAlertDialog.cancel();
                alertDialog.dismiss();
            }
        });
    }
}