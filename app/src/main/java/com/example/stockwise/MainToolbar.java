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
import com.example.stockwise.fragments.transaction.SelectItem_Adapter;
import com.example.stockwise.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

public class MainToolbar {
    private static ScanOptions scanner; // scanner
    private static ArrayList<ProductModel> arrProductSearch; // search product

    // scanner options
    public static ScanOptions getScanner(){
        scanner = new ScanOptions(); // scanner
        scanner.setPrompt("Scan The Barcode Of Your Product"); // title on scanner
        scanner.setBeepEnabled(true); // enable beep sound
        scanner.setOrientationLocked(true); // lock orientation
        scanner.setCaptureActivity(ScannerOrientation.class); // set orientation
        return scanner; // return scanner
    }

    // search product by name
    public static void searchProduct_Barcode(ArrayList<ProductModel> arrAllProduct, ProductAdapter productAdapter,String barCodeId){
        arrProductSearch = new ArrayList<ProductModel>(); // search product
        for(ProductModel productModel: arrAllProduct){ // loop through all product
            if(productModel.getId().equals(barCodeId)){ // if product id is equal to bar code id
                arrProductSearch.add(productModel); // add product to search product
            }
        }
        productAdapter.setLocalDataSet(arrProductSearch); // set search product to adapter
    }

    // search product by bar code
    public static void searchProduct_Transaction_Barcode(ArrayList<ProductModel> arrAllProduct, SelectItem_Adapter productAdapter, String barCodeId){
        arrProductSearch = new ArrayList<ProductModel>(); // search product
        for(ProductModel productModel: arrAllProduct){ // loop through all product
            if(productModel.getId().equals(barCodeId)){ // if product id is equal to bar code id
                arrProductSearch.add(productModel); // add product to search product
            }
        }
        productAdapter.setLocalDataSet(arrProductSearch); // set search product to adapter
    }

    // search product by name
    public static void btnSearch(String query,ArrayList<ProductModel> arrAllProduct, ProductAdapter productAdapter){
        ArrayList<ProductModel> arrProductSearch = new ArrayList<ProductModel>(); // search product

        for(ProductModel productModel: arrAllProduct){ // loop through all product
            if(productModel.getName().toLowerCase().contains(query.toLowerCase())){ // if product name contains query
                arrProductSearch.add(productModel); // add product to search product
            }
        }
        productAdapter.setLocalDataSet(arrProductSearch); // set search product to adapter
    }

    // search product by name
    public static void btnSearch_Transaction(String query,ArrayList<ProductModel> arrAllProduct, SelectItem_Adapter productAdapter){
        ArrayList<ProductModel> arrProductSearch = new ArrayList<ProductModel>(); // search product

        for(ProductModel productModel: arrAllProduct){ // loop through all product
            if(productModel.getName().toLowerCase().contains(query.toLowerCase())){ // if product name contains query
                arrProductSearch.add(productModel); // add product to search product
            }
        }
        productAdapter.setLocalDataSet(arrProductSearch); // set search product to adapter
    }

    // Back button clicked
    public static boolean btnBack_clicked(MenuItem item,Context context){
        switch (item.getItemId()) {  // switch case
            case android.R.id.home: // if back button clicked
                ((Activity)context).finish(); // finish activity
        }
        return true;
    }
}