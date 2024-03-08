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

    public static ScanOptions getScanner(){
        scanner = new ScanOptions();
        scanner.setPrompt("App is ready for use"); // title on scanner
        scanner.setBeepEnabled(true); // enable beep sound
        scanner.setOrientationLocked(true);
        scanner.setCaptureActivity(ScannerOrientation.class);
        return scanner;
    }

    public static void searchProduct_Barcode(ArrayList<ProductModel> arrAllProduct, ProductAdapter productAdapter,String barCodeId){
        ArrayList<ProductModel> arrProductSearch = new ArrayList<ProductModel>();
        arrProductSearch.addAll(arrAllProduct);
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrProductSearch.clear();
                for (DataSnapshot post : snapshot.getChildren()) {
                    if (post.child(Params.getBarCode()).getValue().toString().equals(barCodeId)) {
                        ProductModel productModel = post.getValue(ProductModel.class);
                        productModel.setId(post.getKey().toString());
                        arrProductSearch.add(productModel);
                    }
                }
                productAdapter.setLocalDataSet(arrProductSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ErrorMsg", "onCancelled: "+error.getMessage());
            }
        });
    }

    public static boolean btnBack_clicked(MenuItem item,Context context){
        switch (item.getItemId()) {
            case android.R.id.home:
                ((Activity)context).finish();
                return true;
        }
        return true;
    }
}
