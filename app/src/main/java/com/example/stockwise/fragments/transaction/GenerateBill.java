package com.example.stockwise.fragments.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.stockwise.MainToolbar;
import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityGenerateBillBinding;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.SelectItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GenerateBill extends AppCompatActivity {
    private ActivityGenerateBillBinding binding;
    private Context context;
    private DbTransactionModel parentTransactionModel;
    private List<SelectItemModel> lsProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGenerateBillBinding.inflate(getLayoutInflater());
        context = binding.getRoot().getContext();
        setContentView(binding.getRoot());

        if(getIntent().getStringExtra("CallFrom").equals("History")){
            binding.btnProceedBill.setVisibility(View.GONE);
        }

        parentTransactionModel = (DbTransactionModel) getIntent().getSerializableExtra("transactionObj");
        assert parentTransactionModel != null;
        lsProduct = parentTransactionModel.getITEM_LIST();

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

        String billNum = String.valueOf(lsProduct.get(0).getId().substring(0,2) + parentTransactionModel.getDate().replace("-",""));
        binding.txtBillNoShow.setText(billNum);
        binding.txtShopName.setText(Params.getOwnerModel().getShop_name());
        binding.txtPhoneNumberShow.setText(Params.getOwnerModel().getContact_num());
        binding.txtTodayDate.setText(parentTransactionModel.getDate());
        binding.txtTotalItems.setText(String.valueOf(parentTransactionModel.getITEM_LIST().size()));
        binding.txtTotalAmount.setText(String.valueOf(parentTransactionModel.getTotal_price()));
        binding.txtTotalQuantity.setText(String.valueOf(quantity));
        binding.txtPersonName.setText(getIntent().getStringExtra("Name"));

        // set adapter to the list view
        boolean isPurchase = parentTransactionModel.getIsPurchase().equals("true");
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
    }

    // back press event of actionbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainToolbar.btnBack_clicked(item, context);
    }
}