package com.example.stockwise.fragments.profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.stockwise.LandingPage;
import com.example.stockwise.MainActivity;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private Context context;
    private FragmentProfileBinding bind;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProfileBinding.inflate(inflater,container,false);

        context = bind.getRoot().getContext();

        Glide.with(this).load(Params.getOwnerModel().getPicture()).into(bind.shapeableImageView);
        bind.txtProfileName.setText(Params.getOwnerModel().getName());

        bind.layoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutUser(context);
            }
        });

        return bind.getRoot();
    }

    // log out user from the app
    public void logOutUser(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to LogOut ?");

        // Set Alert Title
        builder.setTitle("Log Out !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Params.getAUTH().signOut(); // signing out current user
                context.startActivity(new Intent(context, LandingPage.class)); // redirecting user to sign in activity
                ((Activity)context).finish(); // finishing this activity
            }
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }
}