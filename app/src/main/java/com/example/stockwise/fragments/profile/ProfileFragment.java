package com.example.stockwise.fragments.profile;

import android.app.Activity;
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
import com.example.stockwise.ProfileNavigation.Account;
import com.example.stockwise.databinding.FragmentProfileBinding;

import com.example.stockwise.MenuScreens.Settings;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProfileFragment extends Fragment {
    private Context context;
    private FragmentProfileBinding bind;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProfileBinding.inflate(inflater,container,false);
        context = bind.getRoot().getContext(); // setting view binding

        Glide.with(this).load(Params.getOwnerModel().getPicture()).into(bind.shapeableImageView); // setting profile image in image view
        bind.txtProfileName.setText(Params.getOwnerModel().getOwner_name()); // setting name of owner in textview

        // setting onclick listener on, logout field
        bind.layoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser(context);
            }
        });

        bind.LayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Settings.class)); // redirecting user to settings activity
            }
        });

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

        SweetAlertDialog pDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Log Out !");
        pDialog.setContentText("Are you sure want to Logout?");
        pDialog.setCancelable(false);
        pDialog.setConfirmText("Yes");
        pDialog.setConfirmButtonBackgroundColor(Color.parseColor("#E01C29"));
        pDialog.setCancelText("No");
        pDialog.setCancelButtonBackgroundColor(Color.parseColor("#1A6AEA"));
        pDialog.setContentTextSize(20);
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Params.getAUTH().signOut(); // signing out current user
                context.startActivity(new Intent(context, LandingPage.class)); // redirecting user to sign in activity
                ((Activity)context).finish(); // finishing this activity
            }
        });
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
            }
        });
        pDialog.show();
    }

}