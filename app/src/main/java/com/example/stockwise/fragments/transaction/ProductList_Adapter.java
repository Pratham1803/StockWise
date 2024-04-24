package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockwise.R;
import com.example.stockwise.model.SelectItemModel;

import java.util.ArrayList;
import java.util.List;

public class ProductList_Adapter extends RecyclerView.Adapter<ProductList_Adapter.ViewHolder> {
    private final Context context; // context
    private final List<SelectItemModel> lsProduct; // data source of the list adapter
    private final boolean isPurchase; // to check if it is purchase or sale

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtHsnNum; // hsn number
        private final TextView txtProductName; // product name
        private final TextView txtQuantity; // quantity
        private final TextView txtRate; // rate
        private final TextView txtTotal; // total

        public ViewHolder(@NonNull View currentItemView) {
            super(currentItemView);
            txtHsnNum = currentItemView.findViewById(R.id.txtHSNShow); // get the reference of the view objects
            txtProductName = currentItemView.findViewById(R.id.txtProductNameShow); // get the reference of the view objects
            txtQuantity = currentItemView.findViewById(R.id.txtQtyShow); // get the reference of the view objects
            txtRate = currentItemView.findViewById(R.id.txtRateShow); // get the reference of the view objects
            txtTotal = currentItemView.findViewById(R.id.txtValueShow); // get the reference of the view objects
        }

        // getter methods for the view objects
        public TextView getTxtHsnNum() {
            return txtHsnNum;
        }

        public TextView getTxtProductName() {
            return txtProductName;
        }

        public TextView getTxtQuantity() {
            return txtQuantity;
        }

        public TextView getTxtRate() {
            return txtRate;
        }

        public TextView getTxtTotal() {
            return txtTotal;
        }
    }

    // constructor of adapter class
    public ProductList_Adapter(List<SelectItemModel> lsProduct, Context context, boolean isPurchase) {
        this.lsProduct = lsProduct; // initialize the data source
        this.context = context; // initialize the context
        this.isPurchase = isPurchase; // initialize the isPurchase
    }

    @NonNull
    @Override
    public ProductList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bill_listview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductList_Adapter.ViewHolder holder, int position) {
        String hsnNum = lsProduct.get(position).getId().toString(); // get the hsn number
        hsnNum = hsnNum.substring(hsnNum.length() - 4); // get the last 4 digits of the hsn number

        holder.getTxtHsnNum().setText(hsnNum); // set the hsn number
        holder.getTxtProductName().setText(lsProduct.get(position).getName()); // set the product name
        holder.getTxtQuantity().setText(lsProduct.get(position).getQuantity()); // set the quantity

        if (isPurchase) { // if it is purchase
            holder.getTxtRate().setText(lsProduct.get(position).getPurchase_price()); // set the purchase price

            // set the total
            holder.getTxtTotal().setText(String.valueOf(Integer.parseInt(lsProduct.get(position).getQuantity()) * Integer.parseInt(lsProduct.get(position).getPurchase_price())));
        } else { // if it is sale
            holder.getTxtRate().setText(lsProduct.get(position).getSale_price()); // set the sale price

            // set the total
            holder.getTxtTotal().setText(String.valueOf(Integer.parseInt(lsProduct.get(position).getQuantity()) * Integer.parseInt(lsProduct.get(position).getSale_price())));
        }
    }

    @Override
    public int getItemCount() {
        return lsProduct.size();
    }
}