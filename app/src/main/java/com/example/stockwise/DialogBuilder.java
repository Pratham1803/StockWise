package com.example.stockwise;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogBuilder {
    public static AlertDialog.Builder showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(R.drawable.logotransparent);
        builder.setMessage(message);

        return builder;
    }
}
