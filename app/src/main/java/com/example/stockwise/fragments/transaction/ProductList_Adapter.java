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
    private final Context context;
    private final List<SelectItemModel> lsProduct;
    private final boolean isPurchase;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtHsnNum;
        private final TextView txtProductName;
        private final TextView txtQuantity;
        private final TextView txtRate;
        private final TextView txtTotal;

        public ViewHolder(@NonNull View currentItemView) {
            super(currentItemView);
            txtHsnNum = currentItemView.findViewById(R.id.txtHSNShow);
            txtProductName = currentItemView.findViewById(R.id.txtProductNameShow);
            txtQuantity = currentItemView.findViewById(R.id.txtQtyShow);
            txtRate = currentItemView.findViewById(R.id.txtRateShow);
            txtTotal = currentItemView.findViewById(R.id.txtValueShow);
        }

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
        this.lsProduct = lsProduct;
        this.context = context;
        this.isPurchase = isPurchase;
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
        String hsnNum = lsProduct.get(position).getId().toString().substring(0, 4);
        holder.getTxtHsnNum().setText(hsnNum);
        holder.getTxtProductName().setText(lsProduct.get(position).getName());
        holder.getTxtQuantity().setText(lsProduct.get(position).getQuantity());

        if (isPurchase) {
            holder.getTxtRate().setText(lsProduct.get(position).getPurchase_price());
            holder.getTxtTotal().setText(String.valueOf(Integer.parseInt(lsProduct.get(position).getQuantity()) * Integer.parseInt(lsProduct.get(position).getPurchase_price())));
        } else {
            holder.getTxtRate().setText(lsProduct.get(position).getSale_price());
            holder.getTxtTotal().setText(String.valueOf(Integer.parseInt(lsProduct.get(position).getQuantity()) * Integer.parseInt(lsProduct.get(position).getSale_price())));
        }
    }

    @Override
    public int getItemCount() {
        return lsProduct.size();
    }
}