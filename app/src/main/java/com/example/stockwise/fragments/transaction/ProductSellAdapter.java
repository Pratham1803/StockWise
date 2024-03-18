package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stockwise.R;
import com.example.stockwise.model.PersonModel;
import com.example.stockwise.model.ProductModel;

import java.util.ArrayList;
import java.util.Random;

public class ProductSellAdapter extends RecyclerView.Adapter<ProductSellAdapter.ViewHolder> {
    private ArrayList<ProductModel> localDataSet;
    private final Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProductImage;
        private final ImageView imgRemove;
        private final TextView txtProductName;
//        private final TextView txtProductPrice;
        private final TextView txtProductQuantity;

        public ViewHolder(View view) {
            super(view);
            imgProductImage = view.findViewById(R.id.imgProductSell);
            imgRemove = view.findViewById(R.id.imgRemove);
            txtProductName = view.findViewById(R.id.txtProductNameSell);
            txtProductQuantity = view.findViewById(R.id.txtQuantityShow);
        }

        public ImageView getImgRemove() {
            return imgRemove;
        }

        public ImageView getImgProductImage() {
            return imgProductImage;
        }

        public TextView getTxtProductName() {
            return txtProductName;
        }

//        public TextView getTxtProductPrice() {
//            return txtProductPrice;
//        }

        public TextView getTxtProductQuantity() {
            return txtProductQuantity;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    // Constructor of ProductAdapter class having two arguments of datalist and context
    public ProductSellAdapter(ArrayList<ProductModel> dataSet, Context context) {
        this.localDataSet = dataSet; // initialing dataset
        this.context = context; // initialing context
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ProductSellAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.select_item_layout, viewGroup, false);

        return new ProductSellAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ProductSellAdapter.ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // setting product name
        viewHolder.getTxtProductName().setText(localDataSet.get(position).getName());

        // setting product price
//        String price = "Price : " + localDataSet.get(position).getSale_price();
//        viewHolder.getTxtProductPrice().setText(price);

        // setting product quantity
        viewHolder.getTxtProductQuantity().setText(String.valueOf(localDataSet.get(position).getCurrent_stock()));

        // setting product image
        Glide.with(context).load(localDataSet.get(position).getPicture()).into(viewHolder.getImgProductImage());

        // setting remove button
        viewHolder.getImgRemove().setOnClickListener(v -> { removeItem(position);});
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    // remove product from list
    public void removeItem(int position) {
        localDataSet.remove(position);
        notifyItemRemoved(position);
    }
}