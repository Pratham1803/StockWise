package com.example.stockwise.fragments.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stockwise.R;
import com.example.stockwise.model.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final ArrayList<ProductModel> localDataSet;
    private final Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtProductName; // to display product name
        private final TextView txtCurrentStock; // to display current stock
        private final TextView txtPrice; // to display price
        private final ImageView imgProduct; // display image

        public ViewHolder(View view) {
            super(view);
            txtProductName = (TextView) view.findViewById(R.id.txtProductName); // getting view of product name textView
            txtCurrentStock = (TextView) view.findViewById(R.id.txtCurrentStock); // getting view of product stock textView
            txtPrice = (TextView) view.findViewById(R.id.txtPrice); // getting view of product price textView
            imgProduct = (ImageView) view.findViewById(R.id.imgProduct); // getting view of product Image ImageView
        }

        // defining getters to return the views of all four views
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
        String currentStock = viewHolder.getTxtCurrentStock().getText() +" " + localDataSet.get(position).getCurrent_stock();
        viewHolder.getTxtCurrentStock().setText(currentStock);

        // setting product price
        String price = viewHolder.getTxtPrice().getText() +" " + localDataSet.get(position).getSale_price();
        viewHolder.getTxtPrice().setText(price);
        Glide.with(context).load(localDataSet.get(position).getPicture()).into(viewHolder.getImgProduct());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}