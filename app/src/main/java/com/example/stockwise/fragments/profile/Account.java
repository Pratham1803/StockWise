package com.example.stockwise.fragments.profile;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.databinding.ActivityAccountBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Account extends AppCompatActivity {
    private static final int REQUEST_IMAGE_GALLERY = 2; // request code for gallery
    private ActivityAccountBinding bind; // view binding
    private Context context; // context
    private boolean isImageChanged = false; // to check if image is changed or not
    private boolean isEditable = false; // to check if fields are editable or not
    private SweetAlertDialog sweetAlertDialog; // sweet alert dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityAccountBinding.inflate(getLayoutInflater()); // initializing view binding
        context = bind.getRoot().getContext(); // setting context
        setContentView(bind.getRoot()); // setting view
        reset(); // setting all fields non-editable

        // inserting values in fields according to user
        bind.EditUserName.setText(Params.getOwnerModel().getOwner_name()); // setting owner name
        bind.EditEmail.setText(Params.getOwnerModel().getEmail_id()); // setting email id
        bind.EditMobileNumber.setText(Params.getOwnerModel().getContact_num()); // setting contact number
        bind.EditShopName.setText(Params.getOwnerModel().getShop_name()); // setting shop name
        Glide.with(this).load(Params.getOwnerModel().getPicture()).into(bind.UserProfileImage); // setting profile image

        // on click of profile image, open gallery to select image
        bind.UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bind.EditUserName.isEnabled()) // if fields are editable, then only open gallery
                    dispatchPickImageIntent(); // open gallery
            }
        });

        // setup actionbar and adding back press button
        setSupportActionBar(bind.toolbarAccount); // setting toolbar
        ActionBar actionBar = getSupportActionBar(); // getting actionbar
        assert actionBar != null; // checking if actionbar is not null
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // setting back button
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context); // back press event
    }

    // reset all fields
    private void reset() {
        isImageChanged = false; // image is not changed
        bind.EditUserName.setEnabled(isEditable); // setting fields editable or not
        bind.EditEmail.setEnabled(isEditable); // setting fields editable or not
        bind.EditShopName.setEnabled(isEditable); // setting fields editable or not

        if (isEditable) // if fields are editable, then update button is visible
            bind.btnUpdateProfile.setVisibility(View.VISIBLE); // setting update button visible
        else // if fields are not editable, then update button is invisible
            bind.btnUpdateProfile.setVisibility(View.GONE); // setting update button invisible
    }

    // popup menu for edit and settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_edit_popup, menu);

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEdit); // getting edit button

        // on click of edit button
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                isEditable = !isEditable; // setting fields editable or not
                reset(); // reset all fields
                return true;
            }
        });

        // popup menu for settings
        MenuItem btnSettings = menu.findItem(R.id.PopUpSettings); // getting settings button
        btnSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(Account.this, Settings.class)); // open settings activity
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // opening local files to find the image
    private void dispatchPickImageIntent() {
        try {
            // open gallery to select image
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // getting image from gallery
            pickImageIntent.setType("image/*"); // setting type of image
            startActivityForResult(pickImageIntent, 2); // starting activity for result
        } catch (Exception e) { // if error occurs
            Log.d("ErrorMsg", "dispatchPickImageIntent: " + e.getMessage()); // log error message
            Toast.makeText(this, "Can't open gallery", Toast.LENGTH_SHORT).show(); // show toast message
        }
    }

    // image is taken from camera or from file, now set in imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // image from gallery
        super.onActivityResult(requestCode, resultCode, data); // getting result from activity
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) { // if image is selected
            // image from gallery
            Uri selectedImageUri = data.getData(); // getting image uri
            bind.UserProfileImage.setImageURI(selectedImageUri); // setting image in image view
            isImageChanged = true; // image is changed
        }
    }

    // on click of update profile button
    public void btnUpdateProfileClicked(View view) {
        // get all values from fields
        String name = bind.EditUserName.getText().toString(); // getting owner name
        String email = bind.EditEmail.getText().toString(); // getting email id
        String shopName = bind.EditShopName.getText().toString(); // getting shop name

        // update values in database
        Params.getOwnerModel().setOwner_name(name); // setting owner name
        Params.getOwnerModel().setEmail_id(email); // setting email id
        Params.getOwnerModel().setShop_name(shopName); // setting shop name

        sweetAlertDialog = DialogBuilder.showSweetDialogProcess(context, "Updating Profile...", ""); // showing sweet alert dialog
        bind.btnUpdateProfile.setText("Please Wait..."); // setting text of update button

        // if image is changed, then upload image to firebase storage
        if (isImageChanged) {
            String image = Params.getOwnerModel().getId() + ".jpg"; // setting the name of image
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); // creating byte array output stream

            Bitmap bitmap = ((BitmapDrawable) bind.UserProfileImage.getDrawable()).getBitmap(); // getting bitmap from the image view
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // compressing bitmap data into image file of jpeg
            byte[] data = baos.toByteArray(); // storing byte data in list

            // uploading image to firebase storage
            Params.getSTORAGE().child(image).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // getting download url of image
                    Params.getSTORAGE().child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // setting the image url in owner model
                            Params.getOwnerModel().setPicture(uri.toString()); // setting the image url in owner modelMediaStore.Images
                            updateText(); // updating text of profile
                            Log.d("SuccessMsg", "onSuccess: Image Updated : " + image);
                        }
                    });
                }
            });
        } else // if image is not changed
            updateText(); // updating text of profile
    }

    // updating Text of profile
    private void updateText() {
        // uploading owner model to firebase database
        Params.getREFERENCE().child(Params.getOwnerName()).setValue(Params.getOwnerModel().getOwner_name()); // setting owner name
        Params.getREFERENCE().child(Params.getEmailId()).setValue(Params.getOwnerModel().getEmail_id()); // setting email id
        Params.getREFERENCE().child(Params.getShopName()).setValue(Params.getOwnerModel().getShop_name()); // setting shop name
        Params.getREFERENCE().child(Params.getProfilePic()).setValue(Params.getOwnerModel().getPicture()) // setting profile picture
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        sweetAlertDialog.dismiss(); // dismissing sweet alert dialog
                        sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Profile Updated", ""); // showing sweet alert dialog
                        isEditable = false; // setting fields non-editable
                        reset(); // set all fields non-editable
                        bind.btnUpdateProfile.setText("UPDATE"); // setting text of update button
                    }
                });
    }
}