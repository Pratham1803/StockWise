package com.example.stockwise.fragments.product;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddProduct extends AppCompatActivity {
    private ActivityAddProductBinding bind; // activity binding
    ProductModel productModel; // object of productModel Class
    private String barCodeId; // to store the bar code numbers
    Bitmap bitmap; // to store the bytes of image
    private static final int REQUEST_IMAGE_CAPTURE = 1; // camera access
    private static final int REQUEST_IMAGE_GALLERY = 2; // gallery access

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this will pause app for the result of scanner
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        super.onCreate(savedInstanceState);

        // initializing view binding
        bind = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        productModel = new ProductModel(); // initializing object of product model

        // setup actionbar
        bind.toolbarProduct.setTitle("Scan & Add Product"); // setting title
        setSupportActionBar(bind.toolbarProduct);

        // onclick listener on imageview to change the image using camera or from gallery
        bind.imgAddProductMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });
    }

    // menu bar item selection listener
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);

        // scan button initialization from action bar and on click listener
        MenuItem btnScan = menu.findItem(R.id.scanner);

        // search and add buttons are not useful so we are unvisibiling them
        MenuItem btnSearch = menu.findItem(R.id.search);
        btnSearch.setVisible(false);

        MenuItem btnAdd = menu.findItem(R.id.addProduct);
        btnAdd.setVisible(false);

        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                ScanOptions sc = new ScanOptions();
                sc.setPrompt("App is ready for use"); // title on scanner
                sc.setBeepEnabled(true); // enable beep sound
                sc.setOrientationLocked(true);
                sc.setCaptureActivity(CaptureActivity.class);
                bar.launch(sc); // launching the scanner
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // select image from gallery or camera  using this method, when product is not available when scan
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);
        builder.setTitle("Select Image:");

        String[] options = {"Capture Photo", "Choose from Gallery"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Capture image using Camera
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        // Choose image from Gallery
                        dispatchPickImageIntent();
                        break;
                }
            }
        });

        builder.create().show();
    }

    // opening camera for taking picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    // opening local files to find the image
    private void dispatchPickImageIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_GALLERY);
        } else {
            Toast.makeText(this, "Gallery not available", Toast.LENGTH_SHORT).show();
        }
    }

    // image is taken from camera or from file, now set in imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // image from camera
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bind.imgAddProductMain.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            // image from gallery
            Uri selectedImageUri = data.getData();
            bind.imgAddProductMain.setImageURI(selectedImageUri);
        }
    }

    // reset all fields of form
    private void reset(){
        bind.imgAddProductMain.setImageDrawable(getDrawable(R.drawable.productvector)); // set default image
        bind.edProductName.setText("");
        bind.edCurrentStock.setText("");
        bind.edReorderpoint.setText("");
        bind.edPurchasePrice.setText("");
        bind.edSalePrice.setText("");
        bind.progrssBarAdd.setVisibility(View.GONE);
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar =registerForActivityResult(new ScanContract(), result-> {
        // if scanner has some result
        if(result.getContents()!=null) {
            bind.progrssBarAdd.setVisibility(View.VISIBLE); // visible the progressbar
            barCodeId = result.getContents(); // collect the barcode number and store it
            getProductDetail(); // calling method to get product details
        }
        // scanner does not have any results
        else
            Toast.makeText(this, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // get product data from api call of barcode id
    private void getProductDetail(){
        String Url = "https://barcodes1.p.rapidapi.com/?query="+barCodeId; // api url
        reset();
        try {
            // building request from okHttp module
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Url)
                    .get()
                    .addHeader("X-RapidAPI-Key", "8b95b432c8mshbc8aee1d626616ap199372jsnb71b43f2a8fc")
                    .addHeader("X-RapidAPI-Host", "barcodes1.p.rapidapi.com")
                    .build();

            // executing the request
            Response responses = client.newCall(request).execute();

            // storing the result of api call here
            String data = responses.body().string();

            // converting data into json object
            JSONObject jsonObject = new JSONObject(data);
            jsonObject = (JSONObject) jsonObject.get("product"); // collecting data from the 'product' key

            // collecting product name from 'title' key
            productModel.setName(jsonObject.get("title").toString());

            // collecting product image array from 'images' key
            JSONArray jsonArray = (JSONArray) jsonObject.get("images");
            productModel.setPicture(jsonArray.get(0).toString()); // adding first image of product from list

            Log.d("ProductData", "get: Name = "+jsonObject.get("title"));
            Log.d("ProductData", "get: Image = "+jsonObject.get("images"));

            // filling details in text box and image view
            bind.edProductName.setText(productModel.getName());
            bind.edProductName.setEnabled(false); // read only
            Glide.with(this).load(productModel.getPicture()).into(bind.imgAddProductMain);
        } catch (Exception e) {
            Log.d("ErrorMsg", "get: "+e.toString());

            // displaying dialog box to user to inform that product details not available in barcode api
            // user can add product manually by capturing picture or select pic from gallery or cancel it
            bind.edProductName.setEnabled(true); // enabling product name textbox to write
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Product Details Can't Fetched!!:");
            builder.setMessage("Add Product Manually! or\nPress Any where to exit");
            builder.setIcon(R.drawable.logotransparent);

            // positive button to add product manually
            builder.setPositiveButton("Add Product", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showImageSelectionDialog(); // method call for user option of camera or gallery
                }
            });

            // user don;t want to add product manually
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); // close dialog box
                }
            });

            builder.create().show();
        }
        bind.progrssBarAdd.setVisibility(View.GONE); // unvisibling the progressbar
    }

    // add product button clicked
    public void btnAddProductClicked(View view) {
        // store all the data from textboxes to the productModel Class object
        productModel.setName(bind.edProductName.getText().toString());
        productModel.setCurrent_stock(bind.edCurrentStock.getText().toString());
        productModel.setReorder_point(bind.edReorderpoint.getText().toString());
        productModel.setPurchase_price(bind.edPurchasePrice.getText().toString());
        productModel.setSale_price(bind.edSalePrice.getText().toString());

        // check that is there any input available or not
        if((!productModel.getName().isEmpty()) && (!productModel.getCurrent_stock().isEmpty())
                && (!productModel.getReorder_point().isEmpty()) && (!productModel.getPurchase_price().isEmpty())
                && (!productModel.getSale_price().isEmpty())){
            // all the details field, check product is already there or not
            isProductAvailable();
        }else{
            Toast.makeText(this, "Fill All the Details", Toast.LENGTH_SHORT).show();
        }
    }

    // check that is product already available or not in database
    // is not then add the product in database
    private void isProductAvailable(){
        // checking that product is available or not
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAvailable = false;
                for(DataSnapshot post: snapshot.getChildren()){
                    // if the name match of product in the database, that means product is already there in database
                    if(post.child(Params.getNAME()).getValue().toString().equals(productModel.getName())){
                        isAvailable = true;
                        break;
                    }
                }
                if(isAvailable)
                    // product is available, so just display the message
                    Toast.makeText(AddProduct.this, "Product is Already Available!!", Toast.LENGTH_SHORT).show();
                else{
                    // product is not available, so visible progress bar and upload data to the database
                    bind.progrssBarAdd.setVisibility(View.VISIBLE);
                    uploadData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    // uploading product details in firebase
    private void uploadData(){
        // Upload bitmap to Firebase Storage
        String image = productModel.getName()+".jpg"; // setting the name of image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap = ((BitmapDrawable) bind.imgAddProductMain.getDrawable()).getBitmap(); // getting bitmap from the image view
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // compressing bitmap data into image file of jpeg
        byte[] data = baos.toByteArray(); // storing byte data in list

        // uploading image of product
        Params.getSTORAGE().child(image).putBytes(data).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // getting url of the image from firebase storage
                        Params.getSTORAGE().child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // setting product url
                                productModel.setPicture(uri.toString());
                                // storing product details is database
                                Params.getREFERENCE().child(Params.getPRODUCT()).push().setValue(productModel).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // data is uploaded in database
                                                Toast.makeText(AddProduct.this, "Product Added!!", Toast.LENGTH_SHORT).show();
                                                reset(); // reseting all the fields
                                            }
                                        }
                                );
                            }
                        });
                    }
                }
        );
    }
}