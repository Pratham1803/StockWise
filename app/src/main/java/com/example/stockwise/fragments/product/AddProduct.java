package com.example.stockwise.fragments.product;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddProduct extends AppCompatActivity {
    private ActivityAddProductBinding bind; // activity binding
    ProductModel productModel; // object of productModel Class
    private String barCodeId; // to store the bar code numbers
    Bitmap bitmap; // to store the bytes of image
    ArrayList<CategoryModel> arrCategory; // to store the category list
    private static final int REQUEST_IMAGE_CAPTURE = 1; // camera access
    private static final int REQUEST_IMAGE_GALLERY = 2; // gallery access
    private ScanOptions scanner; // scanner fields declaration
    private SweetAlertDialog sweetAlertDialog; // alert dialog box declaration
    private AlertDialog.Builder builder; // alert dialog box builder
    private boolean isReorderPointReached = false; // to check the reorder point reached or not
    private boolean isOutOfStock = false; // to check the product is out of stock or not
    private boolean isUpdating = false; // to check the product is updating or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this will pause app for the result of scanner
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog().build());
        super.onCreate(savedInstanceState);

        // initializing view binding
        bind = ActivityAddProductBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot()); // setting view binding
        productModel = new ProductModel(); // initializing object of product model

        // setup actionbar and adding back press button
        setSupportActionBar(bind.toolbarProduct); // setting toolbar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking action bar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

        // setting spinner for category
        arrCategory = new ArrayList<>(); // initializing category list
        arrCategory.add(new CategoryModel("1", "Select Category", null)); // adding default category
        arrCategory.addAll(Params.getOwnerModel().getArrCategory()); // adding all the category in the list

        ArrayList<String> arrCategoryName = new ArrayList<>(); // initializing category name list
        for (CategoryModel categoryModel : arrCategory) // adding category name in the list
            arrCategoryName.add(categoryModel.getName());

        // setting adapter for spinner of category
        ArrayAdapter adapter = new ArrayAdapter(AddProduct.this, android.R.layout.simple_spinner_dropdown_item, arrCategoryName);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line); // setting dropdown view
        bind.spCategory.setAdapter(adapter); // setting adapter in spinner

        // onclick listener on imageview to change the image using camera or from gallery
        bind.imgAddProductMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog(); // method call for user option of camera or gallery
            }
        });

        // current stock input field focus change listener
        bind.edCurrentStock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // if focus is lost
                if (!hasFocus) {
                    // check that current stock is less than 1 or not
                    try {
                        int stock = Integer.parseInt(bind.edCurrentStock.getText().toString()); // getting current stock
                        int reorder = Integer.parseInt(bind.edReorderpoint.getText().toString()); // getting reorder point
                        checkForProductQuantity(stock, reorder);
                    } catch (Exception e) { // if any error rise
                        Log.d("ErrorMsg", "onFocusChange: " + e.getMessage());
                    }
                }
            }
        });

        // reorder point input field focus change listener
        bind.edReorderpoint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // if focus is lost
                if (!hasFocus) {
                    try {
                        int stock = Integer.parseInt(bind.edCurrentStock.getText().toString()); // getting current stock
                        int reorder = Integer.parseInt(bind.edReorderpoint.getText().toString()); // getting reorder point
                        checkForProductQuantity(stock, reorder);
                    } catch (Exception e) { // if any error rise
                        Log.d("ErrorMsg", "onFocusChange: " + e.getMessage());
                    }
                }
            }
        });

        // if product is updating then set the screen for update
        if (getIntent().getSerializableExtra("productObj") != null) {
            isUpdating = true; // setting updating status
            productModel = (ProductModel) getIntent().getSerializableExtra("productObj"); // getting product object
            setUpdateScreen(); // setting screen for update
        }
    }

    private void checkForProductQuantity(int currentStock, int reorderPoint) {
        if (currentStock < 1) { // if stock is less than 1
            bind.txtHeading.setText("Product is Out of Stock!!"); // setting heading
            bind.txtHeading.setVisibility(View.VISIBLE); // making heading visible
            bind.edCurrentStock.setTextColor(getColor(R.color.red)); // changing text color to red
            Toast.makeText(AddProduct.this, "Out of Stock", Toast.LENGTH_SHORT).show(); // displaying toast message
            isOutOfStock = true; // setting out of stock status
            isReorderPointReached = false; // setting reorder point status
        } else {
            if (currentStock < reorderPoint) { // if stock is less than reorder point
                bind.edCurrentStock.setTextColor(getColor(R.color.red)); // changing text color to red
                Toast.makeText(AddProduct.this, "Insufficient Stock", Toast.LENGTH_SHORT).show(); // displaying toast message
                bind.txtHeading.setText("Insufficient Stock!! less Than Reorder Point"); // setting heading
                bind.txtHeading.setVisibility(View.VISIBLE); // making heading visible
                isReorderPointReached = true; // setting reorder point status
                isOutOfStock = false; // setting out of stock status
            }
        }

        if (currentStock >= reorderPoint) { // if stock is greater than reorder point
            bind.txtHeading.setVisibility(View.GONE); // making heading invisible
            bind.edCurrentStock.setTextColor(getColor(R.color.black)); // changing text color to black
            isReorderPointReached = false; // setting reorder point status
            isOutOfStock = false; // setting out of stock status
        }

        Log.d("SuccessMsg", "checkForProductQuantity: isOutOfStock : "+ isOutOfStock);
        Log.d("SuccessMsg", "checkForProductQuantity: isReorderPointReached : "+ isReorderPointReached);
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, AddProduct.this); // back press event
    }

    // setting screen for update
    private void setUpdateScreen() {
        bind.edCurrentStock.setText(productModel.getCurrent_stock()); // setting current stock
        bind.edBarCodeNum.setText(productModel.getId()); // setting barcode number
        bind.edProductName.setText(productModel.getName()); // setting product name
        bind.edPurchasePrice.setText(productModel.getPurchase_price()); // setting purchase price
        bind.edReorderpoint.setText(productModel.getReorder_point()); // setting reorder point
        bind.edSalePrice.setText(productModel.getSale_price()); // setting sale price

        bind.btnAddProduct.setText("Update Product"); // changing button text
        Glide.with(AddProduct.this).load(productModel.getPicture()).into(bind.imgAddProductMain); // setting image in image view

        bind.edCurrentStock.setEnabled(false); // disabling current stock input field
        bind.edBarCodeNum.setEnabled(false); // disabling barcode number input field
        bind.edPurchasePrice.setEnabled(false); // disabling purchase price input field
        bind.edSalePrice.setEnabled(false); // disabling sale price input field

        // setting category spinner
        for (int i = 0; i < arrCategory.size(); i++) {
            // if category is same as product category
            if (arrCategory.get(i).getName().equals(productModel.getCategory())) {
                bind.spCategory.setSelection(i); // setting category in spinner
                break; // break the loop
            }
        }

        bind.edCurrentStock.setTextColor(getColor(R.color.TextGrey)); // changing text color to grey
        bind.edBarCodeNum.setTextColor(getColor(R.color.TextGrey)); // changing text color to grey
        bind.edPurchasePrice.setTextColor(getColor(R.color.TextGrey)); // changing text color to grey
        bind.edSalePrice.setTextColor(getColor(R.color.TextGrey)); // changing text color to grey
    }

    // menu bar item selection listener
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu); // setting toolbar menu

        // scan button initialization from action bar and on click listener
        MenuItem btnScan = menu.findItem(R.id.scanner);

        // search and add buttons are not useful so we are unvisibiling them
        MenuItem btnSearch = menu.findItem(R.id.search);
        btnSearch.setVisible(false); // making search button invisible

        MenuItem btnAdd = menu.findItem(R.id.addProduct); // add product button
        btnAdd.setVisible(false); // making add product button invisible

        // scan button on click listener
        btnScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if (isUpdating) { // if product is updating
                    // displaying toast message
                    Toast.makeText(AddProduct.this, "Can't Scan in Update Mode", Toast.LENGTH_SHORT).show();
                    return true;
                }
                reset(); // reset all the fields
                scanner = MainToolbar.getScanner(); // getting scanner options
                bar.launch(scanner); // launching the scanner
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // select image from gallery or camera  using this method, when product is not available when scan
    private void showImageSelectionDialog() {
        builder = DialogBuilder.showDialog(AddProduct.this, "Select Image Source :", ""); // dialog box builder
        builder.setMessage(null); // setting message to null

        String[] options = {"Capture Photo", "Choose from Gallery"}; // options for dialog box

        // setting options in dialog box
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) { // switch case for options
                    case 0: // if user select camera
                        dispatchTakePictureIntent(); // open camera
                        break;
                    case 1: // if user select gallery
                        dispatchPickImageIntent(); // open gallery
                        break;
                }
            }
        });

        builder.create().show(); // show dialog box
    }

    // opening camera for taking picture
    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // intent for camera
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE); // start camera
        } catch (Exception e) {
            // if any error rise
            Toast.makeText(AddProduct.this, "Can not Open Camera", Toast.LENGTH_SHORT).show();
            Log.d("ErrorMsg", "dispatchTakePictureIntent: " + e.getMessage());
        }
    }

    // opening local files to find the image
    private void dispatchPickImageIntent() {
        try {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // intent for gallery
            pickImageIntent.setType("image/*"); // setting type of image
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_GALLERY); // start gallery
        } catch (Exception e) {
            // if any error rise
            Toast.makeText(AddProduct.this, "Can not Open Gallery", Toast.LENGTH_SHORT).show();
            Log.d("ErrorMsg", "dispatchPickImageIntent: " + e.getMessage());
        }
    }

    // image is taken from camera or from file, now set in imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // calling super method

        // if image is taken from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // image from camera
            Bundle extras = data.getExtras(); // getting extras
            Bitmap imageBitmap = (Bitmap) extras.get("data"); // getting image from extras
            bind.imgAddProductMain.setImageBitmap(imageBitmap); // setting image in image view
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) { // if image is taken from gallery
            // image from gallery is selected
            Uri selectedImageUri = data.getData(); // getting image uri
            bind.imgAddProductMain.setImageURI(selectedImageUri); // setting image in image view
        }
    }

    // reset all fields of form
    private void reset() {
        bind.imgAddProductMain.setImageDrawable(getDrawable(R.drawable.addimg)); // set default image
        bind.edProductName.setText(""); // set product name to null
        bind.edCurrentStock.setText(""); // set current stock to null
        bind.edBarCodeNum.setText(""); // set barcode number to null
        bind.edReorderpoint.setText(""); // set reorder point to null
        bind.edPurchasePrice.setText(""); // set purchase price to null
        bind.edSalePrice.setText(""); // set sale price to null
        bind.txtHeading.setVisibility(View.GONE); // make heading invisible
    }

    // scanner result
    ActivityResultLauncher<ScanOptions> bar = registerForActivityResult(new ScanContract(), result -> {
        // if scanner has some result
        if (result.getContents() != null) {
            barCodeId = result.getContents(); // collect the barcode number and store it
            checkBarCodeNum(); // check the barcode number
        }
        // scanner does not have any results
        else Toast.makeText(this, "Unable to Scan!!", Toast.LENGTH_LONG).show();
    });

    // check the barcode number
    private boolean checkBarCodeNum() {
        builder = DialogBuilder.showDialog(this, "Scanner Result!!:", "Is this, Correct Result? :\n" + barCodeId); // dialog box builder

        // positive button to add product manually
        builder.setPositiveButton("Fetch Product Details", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // close dialog box
                sweetAlertDialog = DialogBuilder.showSweetDialogProcess(AddProduct.this, "Fetching Product Details", "Please Wait..."); // show progress dialog
                try {
                    getProductDetail(); // get product details from api
                } catch (JSONException e) {
                    throw new RuntimeException(e); // if any error rise
                }
            }
        });

        // user don't want to add product manually
        builder.setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bar.launch(scanner); // launch scanner again
            }
        });

        // user want to add product manually
        builder.setNeutralButton("Add Product Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bind.edBarCodeNum.setText(barCodeId); //setting barcode number in text view
                dialog.cancel(); // close dialog box
                showImageSelectionDialog(); // method call for user option of camera or gallery
            }
        });

        builder.create().show(); // show dialog box
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
                Map<String, String> params = new HashMap<>(); // header parameters
                params.put("X-RapidAPI-Key", "8b95b432c8mshbc8aee1d626616ap199372jsnb71b43f2a8fc"); // api key
                params.put("X-RapidAPI-Host", "barcodes1.p.rapidapi.com"); // api host
                return params;
            }
        };

        // setting retry policy for api call
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest); // adding request in queue
    }

    // API can't fetch the product details || Error Rise
    private void productApiErrorHandel(String errorMsg) {
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

        builder.create().show(); // show dialog box
    }

    // add product button clicked
    public void btnAddProductClicked(View view) {
        // store all the data from textboxes to the productModel Class object
        productModel.setId(bind.edBarCodeNum.getText().toString()); // setting barcode number
        productModel.setName(bind.edProductName.getText().toString()); // setting product name
        productModel.setCurrent_stock(bind.edCurrentStock.getText().toString()); // setting current stock
        productModel.setReorder_point(bind.edReorderpoint.getText().toString()); // setting reorder point
        productModel.setPurchase_price(bind.edPurchasePrice.getText().toString()); // setting purchase price
        productModel.setSale_price(bind.edSalePrice.getText().toString()); // setting sale price
        productModel.setCategory(arrCategory.get(bind.spCategory.getSelectedItemPosition()).getId()); // setting category

        // check that is there any input available or not
        if ((!productModel.getName().isEmpty()) && (!productModel.getCurrent_stock().isEmpty()) && (!productModel.getReorder_point().isEmpty()) && (!productModel.getPurchase_price().isEmpty()) && (!productModel.getSale_price().isEmpty()) && (!productModel.getId().isEmpty()) && (!productModel.getCategory().equals("Select Category"))) {
            // all the details field, check product is already there or not
            isProductAvailable();
        } else {
            Toast.makeText(this, "Fill All the Details", Toast.LENGTH_SHORT).show();
        }
    }

    // check that is product already available or not in database
    // is not then add the product in database
    private void isProductAvailable() {
        // check that product is already available or not
        if (!isUpdating) { // if product is not updating
            for (ProductModel productModel : Params.getOwnerModel().getArrAllProduct()) // check all the product in the list
                if (productModel.getId().equals(bind.edBarCodeNum.getText().toString())) { // if product is already available
                    Toast.makeText(AddProduct.this, "Product is Already Available!!", Toast.LENGTH_SHORT).show(); // display toast message
                    return;
                }
        } else { // if product is updating
            if (Integer.parseInt(productModel.getCurrent_stock()) < 1) { // if current stock is less than 1
                isOutOfStock = true; // setting out of stock status
            }
            if (Integer.parseInt(productModel.getCurrent_stock()) < Integer.parseInt(productModel.getReorder_point())
                    && Integer.parseInt(productModel.getCurrent_stock()) > 0) { // if current stock is less than reorder point
                isReorderPointReached = true; // setting reorder point status
            }
        }

        // product is not available, so visible progress bar and upload data to the database
        sweetAlertDialog = DialogBuilder.showSweetDialogProcess(AddProduct.this, "Adding Product", "Please Wait...");
        uploadData(); // method call to upload data in database
    }

    // uploading product details in firebase
    private void uploadData() {
        // Upload bitmap to Firebase Storage
        String image = productModel.getId() + ".jpg"; // setting the name of image
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // initializing byte array output stream

        bitmap = ((BitmapDrawable) bind.imgAddProductMain.getDrawable()).getBitmap(); // getting bitmap from the image view
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // compressing bitmap data into image file of jpeg
        byte[] data = baos.toByteArray(); // storing byte data in list

        productModel.setIsOutOfStock(String.valueOf(isOutOfStock)); // setting the out of stock status
        productModel.setIsReorderPointReached(String.valueOf(isReorderPointReached)); // setting the reorder point status

        // uploading image of product
        Params.getSTORAGE().child(Params.getPRODUCT()).child(image).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // getting url of the image from firebase storage
                Params.getSTORAGE().child(Params.getPRODUCT()).child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // setting product url
                        productModel.setPicture(uri.toString());
                        // storing product details is database
                        DatabaseReference reference = Params.getREFERENCE().child(Params.getPRODUCT()).child(productModel.getId());
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
                Log.d("ErrorMsg", "onFailure: Product Not Upload : " + e.getMessage());
            }
        });
    }

    private void addProductToCategory() {
        CategoryModel categoryModel = arrCategory.get(bind.spCategory.getSelectedItemPosition()); // getting category object

        if (categoryModel.getArrProducts() == null) // if category has no product
            categoryModel.setArrProducts(new ArrayList<>()); // initializing product list
        categoryModel.getArrProducts().add(productModel.getId()); // adding product id in the list

        // adding product id in the category
        Params.getREFERENCE().child(Params.getCATEGORY()).child(categoryModel.getId()).setValue(categoryModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sweetAlertDialog.cancel(); // cancel the progress dialog
                String msg = isUpdating ? "Product Updated Successfully" : "Product Added Successfully"; // success message
                sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(AddProduct.this, "Success", msg); // success dialog box
                reset(); // reset all the fields
                sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent resultIntent = new Intent(); // intent object
                        // You can put extra data in the intent if needed
                        setResult(Activity.RESULT_OK, resultIntent); // setting result
                        finish(); // finish the activity
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sweetAlertDialog.cancel(); // cancel the progress dialog
                sweetAlertDialog = DialogBuilder.showSweetDialogError(AddProduct.this, "Error", "Failed to add product"); // error dialog box
                Log.d("ErrorMsg", "onFailure: " + e.getMessage()); // error message
            }
        });
    }
}