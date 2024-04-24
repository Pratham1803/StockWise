package com.example.stockwise;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogBuilder {
    // Method to show alert dialog
    public static AlertDialog.Builder showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Create a new alert dialog
        builder.setTitle(title); // Set the title of the alert dialog
        builder.setIcon(R.drawable.logotransparent); // Set the icon of the alert dialog
        builder.setMessage(message); // Set the message of the alert dialog

        return builder; // Return the alert dialog
    }

    // Method to show sweet alert dialog with progress
    public static SweetAlertDialog showSweetDialogProcess(Context context, String title, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE); // Create a new sweet alert dialog
        sweetAlertDialog.setTitleText(title); // Set the title of the sweet alert dialog
        sweetAlertDialog.setContentText(message); // Set the message of the sweet alert dialog
        sweetAlertDialog.setCancelable(false); // Set the sweet alert dialog to not be cancelable
        sweetAlertDialog.show(); // Show the sweet alert dialog
        return sweetAlertDialog; // Return the sweet alert dialog
    }

    // Method to show sweet alert dialog with success
    public  static SweetAlertDialog showSweetDialogSuccess(Context context, String title, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE); // Create a new sweet alert dialog
        sweetAlertDialog.setTitleText(title); // Set the title of the sweet alert dialog
        sweetAlertDialog.setContentText(message); // Set the message of the sweet alert dialog
        sweetAlertDialog.show(); // Show the sweet alert dialog
        return sweetAlertDialog; // Return the sweet alert dialog
    }

    // Method to show sweet alert dialog with error
    public static SweetAlertDialog showSweetDialogError(Context context, String title, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE); // Create a new sweet alert dialog
        sweetAlertDialog.setTitleText(title); // Set the title of the sweet alert dialog
        sweetAlertDialog.setContentText(message); // Set the message of the sweet alert dialog
        sweetAlertDialog.show(); // Show the sweet alert dialog
        return sweetAlertDialog; // Return the sweet alert dialog
    }
}