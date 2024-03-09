package com.example.stockwise.fragments.product;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAddProductBinding;
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddProduct extends AppCompatActivity {
    private ActivityAddProductBinding bind; // activity binding
    ProductModel productModel; // object of productModel Class
    private String barCodeId; // to store the bar code numbers
    Bitmap bitmap; // to store the bytes of image
    private static final int REQUEST_IMAGE_CAPTURE = 1; // camera access
    private static final int REQUEST_IMAGE_GALLERY = 2; // gallery access
    private ScanOptions scanner; // scanner fields declaration
    private SweetAlertDialog sweetAlertDialog; // alert dialog box declaration
    private AlertDialog.Builder builder; // alert dialog box builder
    private boolean isReorderPointReached = false; // to check the reorder point reached or not
    private boolean isOutOfStock = false; // to check the product is out of stock or not
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this will pause app for the result of scanner
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog().build());
        super.onCreate(savedInstanceState);

        // initializing view binding
        bind = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        productModel = new ProductModel(); // initializing object of product model

        // setup actionbar and adding back press button
        setSupportActionBar(bind.toolbarProduct);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        // setting spinner for category
        Params.getREFERENCE().child(Params.getCATEGORY()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> arrCategory = new ArrayList<>();
                arrCategory.add("Select Category");
                for (DataSnapshot post : snapshot.getChildren()) {
                    arrCategory.add(post.child(Params.getNAME()).getValue().toString());
                }
                ArrayAdapter adapter = new ArrayAdapter(AddProduct.this, android.R.layout.simple_spinner_dropdown_item, arrCategory);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                bind.spCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // onclick listener on imageview to change the image using camera or from gallery
        bind.imgAddProductMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });

        // current stock input field focus change listener
        bind.edCurrentStock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    try {
                        int stock = Integer.parseInt(bind.edCurrentStock.getText().toString());
                        if(stock<1){
                            bind.txtHeading.setText("Product is Out of Stock!!");
                            bind.txtHeading.setVisibility(View.VISIBLE);
                            bind.edCurrentStock.setTextColor(getColor(R.color.red));
                            Toast.makeText(AddProduct.this, "Out of Stock", Toast.LENGTH_SHORT).show();
                            isOutOfStock = true;
                        }else {
                            bind.txtHeading.setVisibility(View.GONE);
                            bind.edCurrentStock.setTextColor(getColor(R.color.black));
                            isOutOfStock = false;
                        }
                    } catch (Exception e) {
                        Log.d("ErrorMsg", "onFocusChange: "+e.getMessage());
                    }
                }
            }
        });

        // reorder point input field focus change listener
        bind.edReorderpoint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    try {
                        int stock = Integer.parseInt(bind.edCurrentStock.getText().toString());
                        int reorder = Integer.parseInt(bind.edReorderpoint.getText().toString());

                        if (stock < reorder) {
                            bind.edCurrentStock.setTextColor(getColor(R.color.red));
                            Toast.makeText(AddProduct.this, "Insufficient Stock", Toast.LENGTH_SHORT).show();
                            bind.txtHeading.setText("Insufficient Stock!! less Than Reorder Point");
                            bind.txtHeading.setVisibility(View.VISIBLE);
                            isReorderPointReached = true;
                        } else {
                            bind.txtHeading.setVisibility(View.GONE);
                            bind.edCurrentStock.setTextColor(getColor(R.color.black));
                            isReorderPointReached = false;
                        }
                    } catch (Exception e) {
                        Log.d("ErrorMsg", "onFocusChange: "+e.getMessage());
                    }
                }
            }
        });
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item,AddProduct.this);
    }

    // menu bar item selection listener
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

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
                reset();
                scanner = MainToolbar.getScanner(); // getting scanner options
                bar.launch(scanner); // launching the scanner
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // select image from gallery or camera  using this method, when product is not available when scan
    private void showImageSelectionDialog() {
        builder = DialogBuilder.showDialog(AddProduct.this, "Select Image Source :", "");
        builder.setMessage(null);

        String[] options = {"Capture Photo", "Choose from Gallery"};
        builder.setItems(options,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePictureIntent(); // open camera
                        break;
                    case 1:
                        dispatchPickImageIntent(); // open gallery
                        break;
                }
            }
        });

        builder.create().show();
    }

    // opening camera for taking picture
    private void dispatchTakePictureIntent() {
        try{
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }catch (Exception e){
            Toast.makeText(AddProduct.this, "Can not Open Camera", Toast.LENGTH_SHORT).show();
            Log.d("ErrorMsg", "dispatchTakePictureIntent: "+e.getMessage());
        }
    }

    // opening local files to find the image
    private void dispatchPickImageIntent() {
        try {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageIntent.setType("image/*");
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_GALLERY);
        }catch (Exception e){
            Toast.makeText(AddProduct.this, "Can not Open Gallery", Toast.LENGTH_SHORT).show();
            Log.d("ErrorMsg", "dispatchPickImageIntent: "+e.getMessage());
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
    private void reset() {
        bind.imgAddProductMain.setImageDrawable(getDrawable(R.drawable.addimg)); // set default image
        bind.edProductName.setText("");
        bind.edCurrentStock.setText("");
        bind.edBarCodeNum.setText("");
        bind.edReorderpoint.setText("");
        bind.edPurchasePrice.setText("");
        bind.edSalePrice.setText("");
        bind.txtHeading.setVisibility(View.GONE);
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            checkBarCodeNum();
        }
        // scanner does not have any results
        else Toast.makeText(this, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    private boolean checkBarCodeNum() {
        builder = DialogBuilder.showDialog(this, "Scanner Result!!:", "Is this, Correct Result? :\n" + barCodeId);

        // positive button to add product manually
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sweetAlertDialog = new SweetAlertDialog(AddProduct.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Fetching Product Details!!").setContentText("Please Wait...");
                sweetAlertDialog.setCancelable(false); // setting un cancelable
                sweetAlertDialog.show();
                try {
                    getProductDetail();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // user don't want to add product manually
        builder.setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bar.launch(scanner);
            }
        });

        // set cancel button
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
        return true;
    }

    // get product data from api call of barcode id
    private void getProductDetail() throws JSONException {
        String Url = "https://barcodes1.p.rapidapi.com/?query=" + barCodeId; // api url
        RequestQueue queue = Volley.newRequestQueue(this); // request queue
        bind.edBarCodeNum.setText(barCodeId); //setting barcode number in text view

        // Request a json response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Response is received
                try {
                    JSONObject jsonObject = (JSONObject) response.get("product"); // collecting data from the 'product' key

                    // collecting product name from 'title' key
                    productModel.setName(jsonObject.get("title").toString());
                    // collecting product image array from 'images' key
                    JSONArray jsonArray = (JSONArray) jsonObject.get("images");
                    productModel.setPicture(jsonArray.get(0).toString()); // adding first image of product from list
                    Log.d("ProductData", "get: Name = " + jsonObject.get("title"));
                    Log.d("ProductData", "get: Image = " + jsonObject.get("images"));

                    // filling details in text box and image view
                    bind.edProductName.setText(productModel.getName());
                    Glide.with(AddProduct.this).load(productModel.getPicture()).into(bind.imgAddProductMain);
                    sweetAlertDialog.cancel(); // cancel the progress dialog
                } catch (Exception e) {
                    productApiErrorHandel(e.getMessage()); // this method will handel the error
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                productApiErrorHandel(error.toString()); // this method will handel the error
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-RapidAPI-Key", "8b95b432c8mshbc8aee1d626616ap199372jsnb71b43f2a8fc");
                params.put("X-RapidAPI-Host", "barcodes1.p.rapidapi.com");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    // API can't fetch the product details || Error Rise
    private void productApiErrorHandel(String errorMsg){
        Log.d("ErrorMsg", "get: Volley error : " + errorMsg);
        sweetAlertDialog.cancel(); // cancel the progress dialog

        // displaying dialog box to user to inform that product details not available in barcode api
        // user can add product manually by capturing picture or select pic from gallery or cancel it
        bind.edProductName.setEnabled(true); // enabling product name textbox to write
        builder = DialogBuilder.showDialog(AddProduct.this, "Product Details Can't Fetched", "Add Product Manually! or\nPress Any where to exit");

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

    // add product button clicked
    public void btnAddProductClicked(View view) {
        // store all the data from textboxes to the productModel Class object
        productModel.setName(bind.edProductName.getText().toString());
        productModel.setCurrent_stock(bind.edCurrentStock.getText().toString());
        productModel.setReorder_point(bind.edReorderpoint.getText().toString());
        productModel.setPurchase_price(bind.edPurchasePrice.getText().toString());
        productModel.setSale_price(bind.edSalePrice.getText().toString());
        productModel.setBarCodeNum(bind.edBarCodeNum.getText().toString());
        productModel.setCategory(bind.spCategory.getSelectedItem().toString());

        // check that is there any input available or not
        if ((!productModel.getName().isEmpty()) && (!productModel.getCurrent_stock().isEmpty()) && (!productModel.getReorder_point().isEmpty()) && (!productModel.getPurchase_price().isEmpty()) && (!productModel.getSale_price().isEmpty()) && (!productModel.getBarCodeNum().isEmpty()) && (!productModel.getCategory().equals("Select Category"))) {
            // all the details field, check product is already there or not
            isProductAvailable();
        } else {
            Toast.makeText(this, "Fill All the Details", Toast.LENGTH_SHORT).show();
        }
    }

    // check that is product already available or not in database
    // is not then add the product in database
    private void isProductAvailable() {
        // checking that product is available or not
        Params.getREFERENCE().child(Params.getPRODUCT()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAvailable = false;
                for (DataSnapshot post : snapshot.getChildren()) {
                    // if the name match of product in the database, that means product is already there in database
                    if (post.child(Params.getBarCode()).getValue().toString().equals(productModel.getBarCodeNum())) {
                        isAvailable = true;
                        break;
                    }
                }
                if (isAvailable)
                    // product is available, so just display the message
                    Toast.makeText(AddProduct.this, "Product is Already Available!!", Toast.LENGTH_SHORT).show();
                else {
                    // product is not available, so visible progress bar and upload data to the database
                    sweetAlertDialog = new SweetAlertDialog(AddProduct.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Adding Product!!").setContentText("Please Wait...");
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();
                    uploadData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // uploading product details in firebase
    private void uploadData() {
        // Upload bitmap to Firebase Storage
        String image = productModel.getName() + ".jpg"; // setting the name of image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap = ((BitmapDrawable) bind.imgAddProductMain.getDrawable()).getBitmap(); // getting bitmap from the image view
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // compressing bitmap data into image file of jpeg
        byte[] data = baos.toByteArray(); // storing byte data in list

        productModel.setIsOutOfStock(String.valueOf(isOutOfStock)); // setting the out of stock status
        productModel.setIsReorderPointReached(String.valueOf(isReorderPointReached)); // setting the reorder point status

        // uploading image of product
        Params.getSTORAGE().child(image).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // getting url of the image from firebase storage
                Params.getSTORAGE().child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // setting product url
                        productModel.setPicture(uri.toString());
                        // storing product details is database
                        DatabaseReference reference = Params.getREFERENCE().child(Params.getPRODUCT()).push();
                        productModel.setId(reference.getKey());
                        reference.setValue(productModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                addProductToCategory(); // adding product id in it's category
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ErrorMsg", "onFailure: Product Not Upload : "+e.getMessage());
            }
        });
    }

    private void addProductToCategory(){
        DatabaseReference reference = Params.getREFERENCE().child(Params.getCATEGORY()); // getting reference of category
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot post : snapshot.getChildren()) {
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setName(post.child("name").getValue().toString());
                    categoryModel.setNumOfProducts(post.child("numOfProducts").getValue().toString());
                    categoryModel.setId(post.getKey());

                    // finding the category of product from database
                    if (categoryModel.getName().equals(productModel.getCategory())) {
                        // uploading product id in category
                        reference.child(categoryModel.getId()).child(Params.getArrProducts()).push().setValue(productModel.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // updating num of products in category
                                        int numProduct = Integer.parseInt(categoryModel.getNumOfProducts());
                                        reference.child(categoryModel.getId()).child("numOfProducts").setValue(String.valueOf(numProduct+1));
                                        // data is uploaded in database
                                        sweetAlertDialog.cancel();
                                        sweetAlertDialog = new SweetAlertDialog(AddProduct.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Product Added Successfully!!");
                                        sweetAlertDialog.show();
                                        reset(); // resetting all the fields
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("ErrorMsg", "onFailure: "+e.getMessage());
                                        sweetAlertDialog.cancel();
                                        sweetAlertDialog = new SweetAlertDialog(AddProduct.this, SweetAlertDialog.ERROR_TYPE).setTitleText("Product Not Added!!");
                                        sweetAlertDialog.show();
                                        reset();
                                    }
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}