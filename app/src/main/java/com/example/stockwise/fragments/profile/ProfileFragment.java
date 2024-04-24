package com.example.stockwise.fragments.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.stockwise.LandingPage;
import com.example.stockwise.Params;
import com.example.stockwise.databinding.FragmentProfileBinding;
import com.example.stockwise.fragments.SalesAnalysis;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProfileFragment extends Fragment {
    private Context context; // context of the activity
    private FragmentProfileBinding bind; // view binding
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProfileBinding.inflate(inflater,container,false); // inflating view binding
        context = bind.getRoot().getContext(); // setting view binding

        Glide.with(this).load(Params.getOwnerModel().getPicture()).into(bind.shapeableImageView); // setting profile image in image view
        bind.txtProfileName.setText(Params.getOwnerModel().getOwner_name()); // setting name of owner in textview

        // setting onclick listener on, logout field
        bind.layoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser(context); // calling log out user function
            }
        });

        // setting onclick listener on, manage shop field
        bind.LayoutManageShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Manage_Shop.class)); // redirecting user to manage shop activity
            }
        });

        // setting onclick listener on, notifications field
        bind.LayoutNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,Notifications.class)); // redirecting user to notifications activity
            }
        });

        // setting onclick listener on, layout settings field
        bind.LayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Settings.class)); // redirecting user to settings activity
            }
        });

        // setting onclick listener on, layout sales analysis field
        bind.layoutSalesAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SalesAnalysis.class); // redirecting user to sales analysis activity
                startActivity(intent); // starting activity
            }
        });

        // setting onclick listener on, layout account field
        bind.LayoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Account.class)); // redirecting user to Account activity
            }
        });

        return bind.getRoot();
    }

    // log out user from the app
    public void logOutUser(Context context){
        SweetAlertDialog pDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE); // creating sweet alert dialog
        pDialog.setTitleText("Log Out !"); // setting title of dialog
        pDialog.setContentText("Are you sure want to Logout?"); // setting content of dialog
        pDialog.setCancelable(false); // setting dialog non cancellable
        pDialog.setConfirmText("Yes"); // setting confirm text
        pDialog.setConfirmButtonBackgroundColor(Color.parseColor("#E01C29")); // setting confirm button background color
        pDialog.setCancelText("No"); // setting cancel text
        pDialog.setCancelButtonBackgroundColor(Color.parseColor("#1A6AEA")); // setting cancel button background color
        pDialog.setContentTextSize(20); // setting content text size

        // setting onclick listener on confirm button
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Params.getAUTH().signOut(); // signing out current user
                context.startActivity(new Intent(context, LandingPage.class)); // redirecting user to sign in activity
                 // finishing this activity
            }
        });

        // setting onclick listener on cancel button
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss(); // dismissing dialog
            }
        });
        pDialog.show(); // showing dialog
    }
}