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
    private Context context;
    private ProductModel parentProduct;
    private AlertDialog.Builder builder;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        bind = ActivityProductViewBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // getting context of the view
        setContentView(bind.getRoot());

        // getting the product details from the intent
        Intent intent = getIntent();
        parentProduct = (ProductModel) intent.getSerializableExtra("productObj");

        // setting Action bar
        bind.toolbarProductView.setTitle(parentProduct.getName());
        setSupportActionBar(bind.toolbarProductView); // setting action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        // collecting Category
        Params.getREFERENCE().child(Params.getCATEGORY()).child(parentProduct.getCategory_id()).child(Params.getNAME()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                parentProduct.setCategory(dataSnapshot.getValue().toString());
                bind.txtItemCategory.setText(parentProduct.getCategory());
            }
        });

        // setting product details
        bind.txtProductViewName.setText(parentProduct.getName());
        bind.txtSerialNumberView.setText(parentProduct.getBarCodeNum());
        Glide.with(context).load(parentProduct.getPicture()).into(bind.imgProductImageView);
        bind.txtSellingPrice.setText("Rs. " + parentProduct.getSale_price());
        bind.txtPurchasePrice.setText("Rs. " + parentProduct.getPurchase_price());
        bind.txtShopNameShow.setText(Params.getOwnerModel().getShop_name());
        bind.txtSkuShow.setText(parentProduct.getBarCodeNum());
        bind.txtCurrentStockShow.setText(parentProduct.getCurrent_stock());
        bind.txtReorderPoint.setText(parentProduct.getReorder_point());
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }

    // popup menu for Edit and Delete the products
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_view_popup, menu);

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEditProduct);
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                return true;
            }
        });

        // popup menu for settings
        MenuItem btnDelete = menu.findItem(R.id.PopUpDeleteProduct);
        btnDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                builder = DialogBuilder.showDialog(context, "Delete Product", "Are you sure you want to delete this product?");
                sweetAlertDialog = DialogBuilder.showSweetDialogProcess(context, "Deleting Product", "Please Wait...");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // deleting the product from the database
                    Params.getREFERENCE().child(Params.getPRODUCT()).child(parentProduct.getId()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteProductImg();
                                        Log.d("SuccessMsg", "onSuccess: Product Deleted From Real Time Database");
                                    } else if (task.isCanceled()) {
                                        sweetAlertDialog.dismiss();
                                        sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to delete the product");
                                        Log.d("ErrorMsg", "onComplete: Error in deleting the product from the database : " + task.getException().getMessage());
                                    }
                                }
                            });
                });

                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // delete Product Image
    private void deleteProductImg() {
        // delete the Image from the storage
        String image = parentProduct.getName() + ".jpg"; // setting the name of image
        Params.getSTORAGE().child(Params.getPRODUCT()).child(image).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteProductFromCategory();
                    Log.d("SuccessMsg", "onComplete: Product Image Deleted");
                } else if(task.isCanceled()){
                    sweetAlertDialog.dismiss();
                    sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to Delete the Product");
                    Log.d("ErrorMsg", "onComplete: Error in deleting the product image : " + task.getException().getMessage());
                }
            }
        });
    }

    // delete product from category
    private void deleteProductFromCategory() {
        DatabaseReference categoryRef = Params.getREFERENCE().child(Params.getCATEGORY()).child(parentProduct.getCategory_id());
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CategoryModel categoryModel = snapshot.getValue(CategoryModel.class);
                assert categoryModel != null;
                categoryModel.getArrProducts().remove(parentProduct.getId());

                categoryRef.setValue(categoryModel).addOnSuccessListener(aVoid -> {
                    sweetAlertDialog.dismissWithAnimation();
                    sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Success", "Product Deleted Successfully");
                    sweetAlertDialog.setOnDismissListener(dialog -> finish());
                }).addOnFailureListener(e -> {
                    sweetAlertDialog.dismiss();
                    sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to delete the product");
                    Log.e("ErrorMsg", "deleteProduct: Complete " + e.getMessage());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                sweetAlertDialog.dismiss();
                sweetAlertDialog = DialogBuilder.showSweetDialogError(context, "Error", "Failed to delete the product");
                Log.e("ErrorMsg", "deleteProduct: " + error.getMessage());
            }
        });
    }
}