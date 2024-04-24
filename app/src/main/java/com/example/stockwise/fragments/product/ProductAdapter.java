package com.example.stockwise.fragments.product;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stockwise.R;
import com.example.stockwise.model.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<ProductModel> localDataSet; // dataset of product
    private final Context context; // context of the activity

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtProductName; // to display product name
        private final TextView txtCurrentStock; // to display current stock
        private final TextView txtPrice; // to display price
        private final ImageView imgProduct; // display image
        private final TextView txtSKU_num; // Product Barcode Number

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            txtProductName = (TextView) view.findViewById(R.id.txtProductName); // getting view of product name textView
            txtCurrentStock = (TextView) view.findViewById(R.id.txtCurrentStock); // getting view of product stock textView
            txtPrice = (TextView) view.findViewById(R.id.txtPrice); // getting view of product price textView
            txtSKU_num = (TextView) view.findViewById(R.id.txtSerialNumber); // getting view of product Bar code textView
            imgProduct = (ImageView) view.findViewById(R.id.imgProduct); // getting view of product Image ImageView
        }

        // defining getters to return the views of all four views
        public TextView getTxtSKU_num() {
            return txtSKU_num;
        }

        public TextView getTxtProductName() {
            return txtProductName;
        }

        public TextView getTxtCurrentStock() {
            return txtCurrentStock;
        }

        public TextView getTxtPrice() {
            return txtPrice;
        }

        public ImageView getImgProduct() {
            return imgProduct;
        }

        @Override
        public void onClick(View v) { // on click of any item
            int pos = getAbsoluteAdapterPosition(); // getting the position of the clicked item
            ProductModel product = localDataSet.get(pos); // getting the product object of the clicked item
            Intent intent = new Intent(context, ProductView.class); // creating intent to open ProductView activity
            intent.putExtra("productObj", product); // passing the product object to the ProductView activity
            context.startActivity(intent); // starting the ProductView activity
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    // Constructor of ProductAdapter class having two arguments of datalist and context
    public ProductAdapter(ArrayList<ProductModel> dataSet,Context context) {
        this.localDataSet = dataSet; // initialing dataset
        this.context = context; // initialing context
    }

    // setting the localDataSet
    public void setLocalDataSet(ArrayList<ProductModel> localDataSet){
        this.localDataSet = localDataSet; // setting the localDataSet
        this.notifyDataSetChanged(); // notifying the adapter that the data has been changed
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        // setting product name
        viewHolder.getTxtProductName().setText(localDataSet.get(position).getName());

        // setting current stock
        String currentStock = "Current Stock : " + localDataSet.get(position).getCurrent_stock(); // getting current stock of the product
        viewHolder.getTxtCurrentStock().setText(currentStock); // setting current stock of the product

        // setting product price
        String price = "Price : " + localDataSet.get(position).getSale_price(); // getting price of the product
        viewHolder.getTxtPrice().setText(price); // setting price of the product
        Glide.with(context).load(localDataSet.get(position).getPicture()).into(viewHolder.getImgProduct()); // setting image of the product

        // setting barcode SKU Num
        String sku_num = "SKU : " + localDataSet.get(position).getId(); // getting barcode SKU Num of the product
        viewHolder.getTxtSKU_num().setText(sku_num); // setting barcode SKU Num of the product
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}