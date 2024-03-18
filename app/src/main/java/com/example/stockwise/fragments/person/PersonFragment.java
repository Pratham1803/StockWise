package com.example.stockwise.fragments.person;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.stockwise.Params;

import com.example.stockwise.databinding.FragmentPersonBinding;
import com.example.stockwise.model.PersonModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PersonFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private Context context; // to store context
    private Person person; // Object Of Person Class
    private FragmentPersonBinding bind; // bind view
    String[] ContactFilter = {"All Contacts", "Customers", "Vendors"};
    private AlertDialog alertDialog; // to store alert dialog
    private SweetAlertDialog sweetAlertDialog; // to store sweet alert dialog
    private PersonAdapter personAdapter; // to store person adapter
    private ArrayList<PersonModel> arrAllPerson; // to store person list
    private ArrayList<PersonModel> arrAllVendors; // to store Vendors list
    private ArrayList<PersonModel> arrAllCustomers; // to store Customers list

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentPersonBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext();
        person = new Person(context, inflater); // initialing person object

        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, ContactFilter);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterContacts.setAdapter(ad);
        bind.FilterContacts.setOnItemSelectedListener(this);

        // click on add contact button
        bind.btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                person.addPerson();
            }
        });

        // set recycler view
        arrAllPerson = new ArrayList<PersonModel>(); // initializing Array list of personModule
        arrAllVendors = new ArrayList<PersonModel>(); // initializing Array list of personModule
        arrAllCustomers = new ArrayList<PersonModel>(); // initializing Array list of personModule
        personAdapter = new PersonAdapter(arrAllPerson,context); // initializing personAdapter
        bind.recyclerContacts.setLayoutManager(new LinearLayoutManager(context)); // setting layout manager of recycler view
        bind.recyclerContacts.setAdapter(personAdapter); // setting adapter to the recycler view
        bind.recyclerContacts.setNestedScrollingEnabled(false);

        // collecting all contacts from firebase and set in adapter
        person.getAllPerson(arrAllPerson, arrAllVendors, arrAllCustomers, personAdapter);

        // Inflate the layout for this fragment
        return bind.getRoot();
    }

    // Filter of contacts select listener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String currentSelection = ContactFilter[position];
        if(currentSelection.equals(ContactFilter[0])){
            personAdapter.setLocalDataset(arrAllPerson);
        }else if(currentSelection.equals(ContactFilter[1])){
            personAdapter.setLocalDataset(arrAllCustomers);
        }else if (currentSelection.equals(ContactFilter[2])){
            personAdapter.setLocalDataset(arrAllVendors);
        }
        personAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}