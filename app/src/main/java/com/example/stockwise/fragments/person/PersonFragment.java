package com.example.stockwise.fragments.person;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentPersonBinding;
import com.example.stockwise.databinding.FragmentProductBinding;
import com.example.stockwise.fragments.category.ManageCategory;
import com.example.stockwise.model.CategoryModel;

public class PersonFragment extends Fragment {
    private Context context; // to store context

    private FragmentPersonBinding bind; // bind view
    String[] ContactFilter = { "All Contacts","Customers","Vendors"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentPersonBinding.inflate(inflater, container, false); // initialing view binding
        context = bind.getRoot().getContext();


        bind.btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                AlertDialog.Builder builder = DialogBuilder.showDialog(context, "Add New Contact", "");
                builder.setMessage(null);

                LayoutInflater inflater = getLayoutInflater();

                // Inflate the custom layout for the dialog
                View view = inflater.inflate(R.layout.add_contact_dialog, null);
                 EditText edContactName = view.findViewById(R.id.edContactName);

                builder.setView(view);

//                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });

//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });

                // Show the AlertDialog
                builder.create().show();
            }
        });


        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, ContactFilter);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        bind.FilterContacts.setAdapter(ad);

        // Inflate the layout for this fragment
        return bind.getRoot();
    }
}