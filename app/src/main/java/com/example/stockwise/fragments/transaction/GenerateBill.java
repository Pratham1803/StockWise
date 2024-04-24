package com.example.stockwise.fragments.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.stockwise.DialogBuilder;
import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityGenerateBillBinding;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.ProductModel;
import com.example.stockwise.model.SelectItemModel;
import com.example.stockwise.model.TransactionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GenerateBill extends AppCompatActivity {
    private ActivityGenerateBillBinding binding; // binding object
    private Context context; // context object
    private DbTransactionModel dbTransactionModel; // dbTransactionModel object
    private TransactionModel transactionModel; // transactionModel object
    private List<SelectItemModel> lsProduct; // lsProduct object
    private SweetAlertDialog sweetAlertDialog; // sweetAlertDialog object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGenerateBillBinding.inflate(getLayoutInflater()); // inflate the layout
        context = binding.getRoot().getContext(); // get the context
        setContentView(binding.getRoot()); // set the layout

        // check the call from activity
        if(getIntent().getStringExtra("CallFrom").equals("History")){ // if call from history
            binding.btnProceedBill.setVisibility(View.GONE); // hide the proceed button
            dbTransactionModel = (DbTransactionModel) getIntent().getSerializableExtra("dbTransactionObj"); // get the dbTransactionModel object
        } else if (getIntent().getStringExtra("CallFrom").equals("Transaction")) { // if call from transaction
            transactionModel = (TransactionModel) getIntent().getSerializableExtra("transactionObj"); // get the transactionModel object
            assert transactionModel != null; // check the transactionModel object is not null
            dbTransactionModel = transactionModel.getDbTransactionModel(); // get the dbTransactionModel object

            int totalPrice = 0; // initialize the totalPrice

            // calculate the total price
            for (SelectItemModel item : dbTransactionModel.getITEM_LIST()) { // loop through the item list
                if (transactionModel.isPurchase()) // if purchase
                    totalPrice += Integer.parseInt(item.getPurchase_price()) * Integer.parseInt(item.getQuantity()); // calculate the total price
                else // if sale
                    totalPrice += Integer.parseInt(item.getSale_price()) * Integer.parseInt(item.getQuantity()); // calculate the total price
            }
            dbTransactionModel.setTotal_price(String.valueOf(totalPrice)); // set the total price to dbTransactionModel
            binding.txtTotalAmount.setText(String.valueOf(totalPrice)); // set the total price
        }

        assert dbTransactionModel != null; // check the dbTransactionModel object is not null
        lsProduct = dbTransactionModel.getITEM_LIST(); // get the item list

        int quantity = 0; // initialize the quantity

        // calculate the quantity
        for (SelectItemModel itemModel: lsProduct) { // loop through the item list
            quantity += Integer.parseInt(itemModel.getQuantity()); // calculate the quantity
        }

        setSupportActionBar(binding.toolbarBill); // set the toolbar
        ActionBar actionBar = getSupportActionBar(); // get the action bar
        assert actionBar != null; // check the action bar is not null
        actionBar.setTitle(Params.getOwnerModel().getShop_name()); // set the title
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true); // set the display home as up enabled

        // set the values to the text views
        String billNum = String.valueOf(lsProduct.get(0).getId().substring(0,2) + dbTransactionModel.getDate().replace("-","")); // generate the bill number
        binding.txtBillNoShow.setText(billNum); // set the bill number
        binding.txtShopName.setText(Params.getOwnerModel().getShop_name()); // set the shop name
        binding.txtPhoneNumberShow.setText(Params.getOwnerModel().getContact_num()); // set the phone number
        binding.txtTodayDate.setText(dbTransactionModel.getDate()); // set the date
        binding.txtTotalItems.setText(String.valueOf(dbTransactionModel.getITEM_LIST().size())); // set the total items
        binding.txtTotalQuantity.setText(String.valueOf(quantity)); // set the total quantity
        binding.txtPersonName.setText(getIntent().getStringExtra("Name")); // set the person name
        binding.txtTotalAmount.setText(dbTransactionModel.getTotal_price()); // set the total price

        // set adapter to the list view
        boolean isPurchase = dbTransactionModel.getIsPurchase().equals("true"); // check the transaction is purchase or not
        ProductList_Adapter adapter = new ProductList_Adapter(lsProduct, context,isPurchase); // create the adapter object
        binding.lsProductView.setLayoutManager(new LinearLayoutManager(context)); // set the layout manager
        binding.lsProductView.setAdapter(adapter); // set the adapter

        // set the bill type and person type
        if (isPurchase) { // if purchase
            binding.txtBillType.setText("Vendor Invoice"); // set the bill type
            binding.txtPersonType.setText("Vendor : "); // set the person type
        }else { // if sale
            binding.txtBillType.setText("Customer Invoice"); // set the bill type
            binding.txtPersonType.setText("Customer : "); // set the person type
        }

        // get the shop details
        Params.getREFERENCE().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) { // if snapshot exists
                            if (snapshot.child(Params.getGstNum()).exists()) // check the gst number exists
                                binding.txtGstShow.setText(snapshot.child(Params.getGstNum()).getValue().toString()); // set the gst number
                            if (snapshot.child(Params.getADDRESS()).exists()) // check the address exists
                                binding.txtShopAddressShow.setText(snapshot.child(Params.getADDRESS()).getValue().toString()); // set the address
                            if (snapshot.child(Params.getCinNum()).exists()) // check the cin number exists
                                binding.txtCinShow.setText(snapshot.child(Params.getCinNum()).getValue().toString()); // set the cin number
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
                    }
                }
        );

        // on click listener for proceed button
        binding.btnProceedBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAddTransaction(); // add the transaction to the database
            }
        });
    }

    // add the transaction to the database
    private void dbAddTransaction() {
        // get the reference
        DatabaseReference ref = Params.getREFERENCE().child(Params.getTRANSACTION()).child(transactionModel.getDate()).push();
        transactionModel.getDbTransactionModel().setId(ref.getKey()); // set the id

        // add the transaction to the database
        ref.setValue(transactionModel.getDbTransactionModel()).addOnSuccessListener(aVoid -> {
            dbUpdateProduct(); // update the product
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_LONG).show(); // show the toast message
            Log.d("ErrorMsg", "dbAddTransaction: " + e.getMessage());
        });
    }

    // update the product
    private void dbUpdateProduct() {
        DatabaseReference ref = Params.getREFERENCE().child(Params.getPRODUCT()); // get the reference

        Map<String, Object> itemMap = new HashMap<>(); // create the map object
        for (ProductModel item : transactionModel.getITEM_LIST()) { // loop through the item list
            itemMap.put(item.getId() + "/" + Params.getCurrentStock(), item.getCurrent_stock()); // put the current stock
            if (Objects.equals(item.getCurrent_stock(), "0")) // check the current stock is 0
                itemMap.put(item.getId() + "/isOutOfStock", "true"); // set the out of stock
            else if (Integer.parseInt(item.getCurrent_stock()) < Integer.parseInt(item.getReorder_point())) { // check the current stock is less than reorder point
                itemMap.put(item.getId() + "/isOutOfStock", "false"); // set the out of stock
                itemMap.put(item.getId() + "/isReorderPointReached", "true"); // set the reorder point reached
            }else{ // if not
                itemMap.put(item.getId() + "/isOutOfStock", "false"); // set the out of stock
                itemMap.put(item.getId() + "/isReorderPointReached", "false"); // set the reorder point reached
            }
        }

        // update the product
        ref.updateChildren(itemMap).addOnSuccessListener(aVoid -> {
            sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Transaction Added", "Transaction has been added successfully"); // show the success dialog

            // on click listener for ok button
            sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation(); // dismiss the dialog
                    sentSms(); // send the sms
                    Intent intent = new Intent(context, SellProduct.class); // create the intent object
                    setResult(Activity.RESULT_OK, intent); // set the result
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // set the flags
                    startActivity(intent); // start the activity
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_LONG).show(); // show the toast message
            Log.d("ErrorMsg", "dbUpdateProduct: " + e.getMessage());
        });
    }

    // send the sms
    private void sentSms() {
        try {
            // Get the default instance of SmsManager
            SmsManager smsManager = SmsManager.getDefault(); // get the sms manager
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1); // request the permission

            // Phone number to which SMS to be send
            String phoneNumber = transactionModel.getPerson().getContact_num(); // get the phone number

            // SMS text to be sent
            // create the message
            StringBuilder message = new StringBuilder("StockWise - ");
            if(transactionModel.isPurchase()) // if purchase
                message.append("Vendor Bill!!\n").append(transactionModel.getPerson().getName());
            else // if sale
                message.append("Customer Bill!!\n").append(transactionModel.getPerson().getName());

            message.append("\nGST Number : ").append(binding.txtGstShow.getText().toString()); // append the gst number
            message.append("\nCIN Number : ").append(binding.txtCinShow.getText().toString()); // append the cin number
            message.append("\n\nBill No: ").append(binding.txtBillNoShow.getText().toString()); // append the bill number
            message.append("Shop : ").append(Params.getOwnerModel().getShop_name()); // append the shop name
            message.append("\nDate: ").append(transactionModel.getDbTransactionModel().getDate()).append("\n\nItems: \n"); // append the date

            // loop through the item list
            for (SelectItemModel item : transactionModel.getDbTransactionModel().getITEM_LIST()) {
                if (transactionModel.isPurchase()) // if purchase
                    // append the item details
                    message.append(item.getName()).append(" : ").append(item.getQuantity()).append(" x Rs.").append(item.getPurchase_price()).append(" = Rs.").append(Integer.parseInt(item.getQuantity()) * Integer.parseInt(item.getPurchase_price())).append("\n");
                else // if sale
                    // append the item details
                    message.append(item.getName()).append(" : ").append(item.getQuantity()).append(" x Rs.").append(item.getSale_price()).append(" = Rs.").append(Integer.parseInt(item.getQuantity()) * Integer.parseInt(item.getSale_price())).append("\n");
            }
            message.append("\nTotal Price: ").append(transactionModel.getDbTransactionModel().getTotal_price()); // append the total price
            message.append("\n\n").append("Thank you for your business!!"); // append the thank you message

            smsManager.sendTextMessage(phoneNumber, null, message.toString(), null, null); // send the message
        } catch (Exception e) {
            // If an error occurred, log the error
            Log.d("ErrorMsg", "sentSms: " + e.getMessage());
        }
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context); // call the back button clicked
    }
}