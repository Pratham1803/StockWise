package com.example.stockwise;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.stockwise.Params;
import com.example.stockwise.fragments.product.ProductAdapter;
import com.example.stockwise.fragments.product.ScannerOrientation;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class MainToolbar {
    private static ScanOptions scanner;
    private static ArrayList<ProductModel> arrProductSearch;

    public static ScanOptions getScanner(){
        scanner = new ScanOptions();
        scanner.setPrompt("Scan The Barcode Of Your Product"); // title on scanner
        scanner.setBeepEnabled(true); // enable beep sound
        scanner.setOrientationLocked(true);
        scanner.setCaptureActivity(ScannerOrientation.class);
        return scanner;
    }

    public static void searchProduct_Barcode(ArrayList<ProductModel> arrAllProduct, ProductAdapter productAdapter,String barCodeId){
        arrProductSearch = new ArrayList<ProductModel>();
        for(ProductModel productModel: arrAllProduct){
            if(productModel.getId().equals(barCodeId)){
                arrProductSearch.add(productModel);
            }
        }
        productAdapter.setLocalDataSet(arrProductSearch);
    }

    public static void btnSearch(String query,ArrayList<ProductModel> arrAllProduct, ProductAdapter productAdapter){
        ArrayList<ProductModel> arrProductSearch = new ArrayList<ProductModel>();

        for(ProductModel productModel: arrAllProduct){
            if(productModel.getName().toLowerCase().contains(query.toLowerCase())){
                arrProductSearch.add(productModel);
            }
        }
        productAdapter.setLocalDataSet(arrProductSearch);
    }

    public static boolean btnBack_clicked(MenuItem item,Context context){
        switch (item.getItemId()) {
            case android.R.id.home:
                ((Activity)context).finish();
        }
        return true;
    }
}