package com.example.stockwise;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogBuilder {
    public static AlertDialog.Builder showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(R.drawable.logotransparent);
        builder.setMessage(message);

        return builder;
    }

    public static SweetAlertDialog showSweetDialogProcess(Context context, String title, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        return sweetAlertDialog;
    }

    public  static SweetAlertDialog showSweetDialogSuccess(Context context, String title, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
        return sweetAlertDialog;
    }

    public static SweetAlertDialog showSweetDialogError(Context context, String title, String message) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
        return sweetAlertDialog;
    }
}
