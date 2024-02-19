package com.example.stockwise.fragments.product;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAddProductBinding;
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddProduct extends AppCompatActivity {
    private ActivityAddProductBinding bind;
    ProductModel productModel;
    private String barCodeId; // to store the bar code numbers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        super.onCreate(savedInstanceState);

        bind = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        productModel = new ProductModel();

        // setup actionbar
        bind.toolbarProduct.setTitle("Scan & Add Product");
        setSupportActionBar(bind.toolbarProduct);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);

        MenuItem btnScan = menu.findItem(R.id.scanner);
        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                ScanOptions sc=new ScanOptions();
                sc.setPrompt("App is ready for use");
                sc.setBeepEnabled(true);
                sc.setOrientationLocked(true);
                sc.setCaptureActivity(CaptureActivity.class);
                bar.launch(sc);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar =registerForActivityResult(new ScanContract(), result-> {
        if(result.getContents()!=null) {
            Toast.makeText(this, "Wait a While!!", Toast.LENGTH_SHORT).show();
            bind.progrssBarAdd.setVisibility(View.VISIBLE);
            barCodeId = result.getContents();
            getProductDetail();
        }
        else
            Toast.makeText(this, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // get product data from api call of barcode id
    private void getProductDetail(){
        String Url = "https://api.barcodelookup.com/v3/products?barcode="+barCodeId+"&formatted=y&key=gyjvlv8szmafys678c5zarsiykx9yo";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Url)
                    .build();

            Response responses = client.newCall(request).execute();

            String data = responses.body().string();

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = (JSONArray) jsonObject.get("products");

            // collecting product name
            jsonObject = (JSONObject) jsonArray.get(0);
            productModel.setName(jsonObject.get("title").toString());

            // collecting product image
            jsonArray = (JSONArray) jsonObject.get("images");
            productModel.setPicture(jsonArray.get(0).toString());

            Log.d("ProductData", "get: Name = "+jsonObject.get("title"));
            Log.d("ProductData", "get: Image = "+jsonObject.get("images"));

            // filling details
            bind.edProductName.setText(productModel.getName());
            Glide.with(this).load(productModel.getPicture()).into(bind.imgAddProductMain);
        } catch (Exception e) {
            Log.d("ErrorMsg", "get: "+e.toString());
            Toast.makeText(this, "No Result"+e.toString(), Toast.LENGTH_LONG).show();
        }
        bind.progrssBarAdd.setVisibility(View.GONE);
    }

    // add product clicked
    public void btnAddProductClicked(View view) {
        // store all the data from textboxes to the productModel Class object
        productModel.setName(bind.edProductName.getText().toString());
        productModel.setCurrent_stock(bind.edCurrentStock.getText().toString());
        productModel.setReorder_point(bind.edReorderpoint.getText().toString());
        productModel.setPurchase_price(bind.edPurchasePrice.getText().toString());
        productModel.setSale_price(bind.edSalePrice.getText().toString());

        // check that is there any input available or not
        if((productModel.getName().isEmpty()) && (productModel.getCurrent_stock().isEmpty())
                && (productModel.getReorder_point().isEmpty()) && (productModel.getPurchase_price().isEmpty())
                && (productModel.getSale_price().isEmpty())){
            Toast.makeText(this, "Fill All the Details", Toast.LENGTH_SHORT).show();
        }else{

            Params.getREFERENCE().child(Params.getPRODUCT()).push().setValue(productModel).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddProduct.this, "Product Added!!", Toast.LENGTH_SHORT).show();
                            bind.edProductName.setText("");
                            bind.edCurrentStock.setText("");
                            bind.edReorderpoint.setText("");
                            bind.edPurchasePrice.setText("");
                            bind.edSalePrice.setText("");
                        }
                    }
            );
        }
    }
}