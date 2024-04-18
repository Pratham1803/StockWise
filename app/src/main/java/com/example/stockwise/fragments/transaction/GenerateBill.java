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
    private ActivityGenerateBillBinding binding;
    private Context context;
    private DbTransactionModel dbTransactionModel;
    private TransactionModel transactionModel;
    private List<SelectItemModel> lsProduct;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGenerateBillBinding.inflate(getLayoutInflater());
        context = binding.getRoot().getContext();
        setContentView(binding.getRoot());

        if(getIntent().getStringExtra("CallFrom").equals("History")){
            binding.btnProceedBill.setVisibility(View.GONE);
            dbTransactionModel = (DbTransactionModel) getIntent().getSerializableExtra("dbTransactionObj");
        } else if (getIntent().getStringExtra("CallFrom").equals("Transaction")) {
            transactionModel = (TransactionModel) getIntent().getSerializableExtra("transactionObj");
            assert transactionModel != null;
            dbTransactionModel = transactionModel.getDbTransactionModel();

            int totalPrice = 0;
            for (SelectItemModel item : dbTransactionModel.getITEM_LIST()) {
                if (transactionModel.isPurchase())
                    totalPrice += Integer.parseInt(item.getPurchase_price()) * Integer.parseInt(item.getQuantity());
                else
                    totalPrice += Integer.parseInt(item.getSale_price()) * Integer.parseInt(item.getQuantity());
            }
            dbTransactionModel.setTotal_price(String.valueOf(totalPrice));
            binding.txtTotalAmount.setText(String.valueOf(totalPrice));
        }


        assert dbTransactionModel != null;
        lsProduct = dbTransactionModel.getITEM_LIST();

        int quantity = 0;
        for (SelectItemModel itemModel: lsProduct) {
            quantity += Integer.parseInt(itemModel.getQuantity());
        }

        setSupportActionBar(binding.toolbarBill);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(Params.getOwnerModel().getShop_name());
        actionBar.setHomeAsUpIndicator(R.drawable.leftarrowvector); // changing customize back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        String billNum = String.valueOf(lsProduct.get(0).getId().substring(0,2) + dbTransactionModel.getDate().replace("-",""));
        binding.txtBillNoShow.setText(billNum);
        binding.txtShopName.setText(Params.getOwnerModel().getShop_name());
        binding.txtPhoneNumberShow.setText(Params.getOwnerModel().getContact_num());
        binding.txtTodayDate.setText(dbTransactionModel.getDate());
        binding.txtTotalItems.setText(String.valueOf(dbTransactionModel.getITEM_LIST().size()));
        binding.txtTotalQuantity.setText(String.valueOf(quantity));
        binding.txtPersonName.setText(getIntent().getStringExtra("Name"));
        binding.txtTotalAmount.setText(dbTransactionModel.getTotal_price());

        // set adapter to the list view
        boolean isPurchase = dbTransactionModel.getIsPurchase().equals("true");
        ProductList_Adapter adapter = new ProductList_Adapter(lsProduct, context,isPurchase);
        binding.lsProductView.setLayoutManager(new LinearLayoutManager(context));
        binding.lsProductView.setAdapter(adapter);

        if (isPurchase) {
            binding.txtBillType.setText("Purchase Bill");
            binding.txtPersonType.setText("Vendor : ");
            binding.txtTotalAmount.setTextColor(getResources().getColor(R.color.red));
        }else {
            binding.txtBillType.setText("Sale Bill");
            binding.txtPersonType.setText("Customer : ");
            binding.txtTotalAmount.setTextColor(getResources().getColor(R.color.SuccessGreen));
        }

        Params.getREFERENCE().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child(Params.getGstNum()).exists())
                                binding.txtGstShow.setText(snapshot.child(Params.getGstNum()).getValue().toString());
                            if (snapshot.child(Params.getADDRESS()).exists())
                                binding.txtShopAddressShow.setText(snapshot.child(Params.getADDRESS()).getValue().toString());
                            if (snapshot.child(Params.getCinNum()).exists())
                                binding.txtCinShow.setText(snapshot.child(Params.getCinNum()).getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ErrorMsg", "onCancelled: " + error.getMessage());
                    }
                }
        );
        
        binding.btnProceedBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAddTransaction();
            }
        });
    }

    private void dbAddTransaction() {
        DatabaseReference ref = Params.getREFERENCE().child(Params.getTRANSACTION()).child(transactionModel.getDate()).push();
        transactionModel.getDbTransactionModel().setId(ref.getKey());

        ref.setValue(transactionModel.getDbTransactionModel()).addOnSuccessListener(aVoid -> {
            dbUpdateProduct();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_LONG).show();
            Log.d("ErrorMsg", "dbAddTransaction: " + e.getMessage());
        });
    }

    private void dbUpdateProduct() {
        DatabaseReference ref = Params.getREFERENCE().child(Params.getPRODUCT());

        Map<String, Object> itemMap = new HashMap<>();
        for (ProductModel item : transactionModel.getITEM_LIST()) {
            itemMap.put(item.getId() + "/" + Params.getCurrentStock(), item.getCurrent_stock());
            if (Objects.equals(item.getCurrent_stock(), "0"))
                itemMap.put(item.getId() + "/isOutOfStock", "true");
            else if (Integer.parseInt(item.getCurrent_stock()) < Integer.parseInt(item.getReorder_point())) {
                itemMap.put(item.getId() + "/isOutOfStock", "false");
                itemMap.put(item.getId() + "/isReorderPointReached", "true");
            }else{
                itemMap.put(item.getId() + "/isOutOfStock", "false");
                itemMap.put(item.getId() + "/isReorderPointReached", "false");
            }
        }

        ref.updateChildren(itemMap).addOnSuccessListener(aVoid -> {
            sweetAlertDialog = DialogBuilder.showSweetDialogSuccess(context, "Transaction Added", "Transaction has been added successfully");
            sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    sentSms();
                    Intent intent = new Intent(context, SellProduct.class);
                    setResult(Activity.RESULT_OK, intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_LONG).show();
            Log.d("ErrorMsg", "dbUpdateProduct: " + e.getMessage());
        });
    }

    private void sentSms() {
        try {
            // Get the default instance of SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);

            // Phone number to which SMS to be send
            String phoneNumber = transactionModel.getPerson().getContact_num();

            // SMS text to be sent
            StringBuilder message = new StringBuilder("StockWise - Transaction Succeed!!\n");
            message.append("Shop : ").append(Params.getOwnerModel().getShop_name());
            message.append("\nDate: ").append(transactionModel.getDbTransactionModel().getDate()).append("\n\nItems: \n");

            for (SelectItemModel item : transactionModel.getDbTransactionModel().getITEM_LIST()) {
                if (transactionModel.isPurchase())
                    message.append(item.getName()).append(" : ").append(item.getQuantity()).append(" x Rs.").append(item.getPurchase_price()).append(" = Rs.").append(Integer.parseInt(item.getQuantity()) * Integer.parseInt(item.getPurchase_price())).append("\n");
                else
                    message.append(item.getName()).append(" : ").append(item.getQuantity()).append(" x Rs.").append(item.getSale_price()).append(" = Rs.").append(Integer.parseInt(item.getQuantity()) * Integer.parseInt(item.getSale_price())).append("\n");
            }
            message.append("\nTotal Price: ").append(transactionModel.getDbTransactionModel().getTotal_price());
            message.append("\n\n").append("Thank you for your business!!");

            smsManager.sendTextMessage(phoneNumber, null, message.toString(), null, null);
        } catch (Exception e) {
            Log.d("ErrorMsg", "sentSms: " + e.getMessage());
        }
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }
}