package com.example.stockwise.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentHomeBinding;
import com.example.stockwise.databinding.FragmentProfileBinding;

public class HomeFragment extends Fragment {
    private View root;
    private Context context;
    private FragmentHomeBinding bind;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View Binding
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        context = bind.getRoot().getContext();
        root = bind.getRoot();

        // Inflate the layout for this fragment
        return root;


    }
}