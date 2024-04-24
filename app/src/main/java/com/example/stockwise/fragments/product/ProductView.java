package com.example.stockwise.fragments.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityProductViewBinding;
import com.example.stockwise.model.CategoryModel;
import com.example.stockwise.model.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductView extends AppCompatActivity {
    private ActivityProductViewBinding bind; // view binding
    private Context context; // context of the view
    private ProductModel parentProduct; // product object
    private AlertDialog.Builder builder; // alert dialog builder
    private SweetAlertDialog sweetAlertDialog; // sweet alert dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityProductViewBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // getting context of the view
        setContentView(bind.getRoot()); // setting the view

        // getting the product details from the intent
        Intent intent = getIntent(); // getting the intent
        parentProduct = (ProductModel) intent.getSerializableExtra("productObj"); // getting the product object

        // setting Action bar
        bind.toolbarProductView.setTitle(parentProduct.getName()); // setting title of the action bar
        setSupportActionBar(bind.toolbarProductView); // setting action bar
        ActionBar actionBar = getSupportActionBar(); // getting action bar
        assert actionBar != null; // checking if action bar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button

        // collecting Category
        Params.getREFERENCE().child(Params.getCATEGORY()).child(parentProduct.getCategory_id()).child(Params.getNAME()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                // setting category
                parentProduct.setCategory(dataSnapshot.getValue().toString()); // setting category
                bind.txtItemCategory.setText(parentProduct.getCategory()); // setting category
            }
        });

        // setting product details
        bind.txtProductViewName.setText(parentProduct.getName()); // setting product name
        bind.txtSerialNumberView.setText(parentProduct.getId()); // setting product id
        Glide.with(context).load(parentProduct.getPicture()).into(bind.imgProductImageView); // setting product image
        bind.txtSellingPrice.setText("Rs. " + parentProduct.getSale_price()); // setting selling price
        bind.txtPurchasePrice.setText("Rs. " + parentProduct.getPurchase_price()); // setting purchase price
        bind.txtShopNameShow.setText(Params.getOwnerModel().getShop_name()); // setting shop name
        bind.txtSkuShow.setText(parentProduct.getId()); // setting sku
        bind.txtCurrentStockShow.setText(parentProduct.getCurrent_stock()); // setting current stock
        bind.txtReorderPoint.setText(parentProduct.getReorder_point()); // setting reorder point
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context); // back press event
    }

    // popup menu for Edit and Delete the products
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_view_popup, menu); // inflating the menu

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEditProduct); // getting the edit button

        // edit button click event
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                Intent intent = new Intent(context, AddProduct.class); // intent to open the AddProduct activity
                intent.putExtra("productObj", parentProduct); // sending the product object to the AddProduct activity
                startActivity(intent); // starting the activity
                return true;
            }
        });

        // popup menu for settings
        MenuItem btnDelete = menu.findItem(R.id.PopUpDeleteProduct); // getting the delete button
        btnDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // alert dialog for delete the product
                builder = DialogBuilder.showDialog(context, "Delete Product", "Are you sure you want to delete this product?");

                // delete button click event
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    sweetAlertDialog = DialogBuilder.showSweetDialogProcess(context, "Deleting Product", "Please Wait..."); // sweet alert dialog for process
                    // deleting the product from the database
                    Params.getREFERENCE().child(Params.getPRODUCT()).child(parentProduct.getId()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) { // if task is successful
                                        deleteProductImg(); // delete the product image
                                        Log.d("SuccessMsg", "onSuccess: Product Deleted From Real Time Database"); // log message
                                    } else if (task.isCanceled()) { // if task is cancelled
                                        sweetAlertDialog.dismiss(); // dismiss the sweet alert dialog
                                        sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to delete the product"); // sweet alert dialog for error
                                        Log.d("ErrorMsg", "onComplete: Error in deleting the product from the database : " + task.getException().getMessage()); // log message
                                    }
                                }
                            });
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // no button click event
                builder.show(); // show the alert dialog
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // delete Product Image
    private void deleteProductImg() {
        // delete the Image from the storage
        String image = parentProduct.getId() + ".jpg"; // setting the name of image

        // deleting the image from the storage
        Params.getSTORAGE().child(Params.getPRODUCT()).child(image).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){ // if task is successful
                    deleteProductFromCategory(); // delete the product from the category
                    Log.d("SuccessMsg", "onComplete: Product Image Deleted");
                } else if(task.isCanceled()){ // if task is cancelled
                    sweetAlertDialog.dismiss(); // dismiss the sweet alert dialog
                    sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to Delete the Product"); // sweet alert dialog for error
                    Log.d("ErrorMsg", "onComplete: Error in deleting the product image : " + task.getException().getMessage()); // log message
                }
            }
        });
    }

    // delete product from category
    private void deleteProductFromCategory() {
        // delete the product from the category
        DatabaseReference categoryRef = Params.getREFERENCE().child(Params.getCATEGORY()).child(parentProduct.getCategory_id());
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CategoryModel categoryModel = snapshot.getValue(CategoryModel.class); // getting the category model
                assert categoryModel != null; // checking if category model is not null
                categoryModel.getArrProducts().remove(parentProduct.getId()); // removing the product from the category

                // updating the category
                categoryRef.setValue(categoryModel).addOnSuccessListener(aVoid -> {
                    sweetAlertDialog.dismissWithAnimation(); // dismiss the sweet alert dialog
                    sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Success", "Product Deleted Successfully"); // sweet alert dialog for success
                    sweetAlertDialog.setOnDismissListener(dialog -> finish()); // finish the activity
                }).addOnFailureListener(e -> { // if failed to delete the product
                    sweetAlertDialog.dismiss(); // dismiss the sweet alert dialog
                    sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to delete the product"); // sweet alert dialog for error
                    Log.e("ErrorMsg", "deleteProduct: Complete " + e.getMessage()); // log message
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                sweetAlertDialog.dismiss(); // dismiss the sweet alert dialog
                sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to delete the product"); // sweet alert dialog for error
                Log.e("ErrorMsg", "deleteProduct: " + error.getMessage()); // log message
            }
        });
    }
}