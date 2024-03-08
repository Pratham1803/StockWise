package com.example.stockwise.ProfileNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stockwise.MainActivity;
import com.example.stockwise.MenuScreens.Settings;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAccountBinding;
import com.example.stockwise.databinding.ActivitySettingsBinding;
import com.example.stockwise.databinding.FragmentProfileBinding;
import com.example.stockwise.fragments.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Account extends AppCompatActivity {
    private ActivityAccountBinding bind; // view binding
    private boolean isImageChanged = false; // to check if image is changed or not
    private SweetAlertDialog sweetAlertDialog; // sweet alert dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityAccountBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());
        reset(false); // setting all fields non-editable

        // inserting values in fields according to user
        bind.EditUserName.setText(Params.getOwnerModel().getOwner_name());
        bind.EditEmail.setText(Params.getOwnerModel().getEmail_id());
        bind.EditMobileNumber.setText(Params.getOwnerModel().getContact_num());
        bind.EditShopName.setText(Params.getOwnerModel().getShop_name());
        Glide.with(this).load(Params.getOwnerModel().getPicture()).into(bind.UserProfileImage); // setting profile image

        // on click of profile image, open gallery to select image
        bind.UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bind.EditUserName.isEnabled()) // if fields are editable, then only open gallery
                    dispatchPickImageIntent();
            }
        });

        // setup actionbar and adding back press button
        setSupportActionBar(bind.toolbarAccount);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // reset all fields
    private void reset(boolean isEditable) {
        isImageChanged = false; // image is not changed
        bind.EditUserName.setEnabled(isEditable);
        bind.EditEmail.setEnabled(isEditable);
        bind.EditShopName.setEnabled(isEditable);
    }

    // popup menu for edit and settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_edit_popup, menu);

        // popup menu for edit
        MenuItem btnEdit = menu.findItem(R.id.PopUpEdit);
        btnEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                reset(true);
                return true;
            }
        });

        // popup menu for settings
        MenuItem btnSettings = menu.findItem(R.id.PopUpSettings);
        btnSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(Account.this, Settings.class));
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // opening local files to find the image
    private void dispatchPickImageIntent() {
        try {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageIntent.setType("image/*");
            startActivityForResult(pickImageIntent, 2);
        }catch (Exception e){
            Log.d("ErrorMsg", "dispatchPickImageIntent: "+e.getMessage());
            Toast.makeText(this, "Can't open gallery", Toast.LENGTH_SHORT).show();
        }
    }

    // image is taken from camera or from file, now set in imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // image from gallery
        Uri selectedImageUri = data.getData();
        bind.UserProfileImage.setImageURI(selectedImageUri);
        isImageChanged = true; // image is changed
    }

    // on click of update profile button
    public void btnUpdateProfileClicked(View view) {
        // get all values from fields
        String name = bind.EditUserName.getText().toString();
        String email = bind.EditEmail.getText().toString();
        String shopName = bind.EditShopName.getText().toString();

        // update values in database
        Params.getOwnerModel().setOwner_name(name);
        Params.getOwnerModel().setEmail_id(email);
        Params.getOwnerModel().setShop_name(shopName);

        sweetAlertDialog = new SweetAlertDialog(Account.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Updating Profile");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        bind.button.setText("Please Wait...");

        if (isImageChanged) {
            String image = Params.getOwnerModel().getId() + ".jpg"; // setting the name of image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Bitmap bitmap = ((BitmapDrawable) bind.UserProfileImage.getDrawable()).getBitmap(); // getting bitmap from the image view
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // compressing bitmap data into image file of jpeg
            byte[] data = baos.toByteArray(); // storing byte data in list

            // uploading image to firebase storage
            Params.getSTORAGE().child(image).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Params.getSTORAGE().child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Params.getOwnerModel().setPicture(uri.toString()); // setting the image url in owner modelMediaStore.Images
                            updateText();
                            Log.d("SuccessMsg", "onSuccess: Image Updated : " + image);
                        }
                    });
                }
            });
        }else
            updateText();
    }

    // updating Text of profile
    private void updateText(){
        // uploading owner model to firebase database
        Params.getREFERENCE().child(Params.getOwnerName()).setValue(Params.getOwnerModel().getOwner_name());
        Params.getREFERENCE().child(Params.getEmailId()).setValue(Params.getOwnerModel().getEmail_id());
        Params.getREFERENCE().child(Params.getShopName()).setValue(Params.getOwnerModel().getShop_name());
        Params.getREFERENCE().child(Params.getProfilePic()).setValue(Params.getOwnerModel().getPicture())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        sweetAlertDialog.cancel();
                        sweetAlertDialog = new SweetAlertDialog(Account.this, SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setTitleText("Profile Updated");
                        sweetAlertDialog.show();
                        reset(false); // set all fields non-editable
                        bind.button.setText("UPDATE");
                    }
                });
    }
}