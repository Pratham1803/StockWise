package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stockwise.R;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.ProductModel;
import com.example.stockwise.model.SelectItemModel;
import com.example.stockwise.model.TransactionModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SelectItem_Adapter extends RecyclerView.Adapter<SelectItem_Adapter.ViewHolder> {
    private ArrayList<ProductModel> localDataSet;
    private final Context context;
    private TransactionModel transactionModel;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView imgProductImage;
        private final TextView txtProductName;
        private final TextView txtProductQuantity;
        private final ImageButton btnPlus;
        private final ImageButton btnMinus;
        private final TextView txtQuantityShow;
        private final ImageButton btnAddToCart;
        public ViewHolder(View view) {
            super(view);
            imgProductImage = view.findViewById(R.id.imgProductSell);
            txtProductName = view.findViewById(R.id.txtProductNameSell);
            txtProductQuantity = view.findViewById(R.id.txtQuantityShow);
            btnPlus = view.findViewById(R.id.btnQuantityPlus);
            btnMinus = view.findViewById(R.id.btnQuantityMinus);
            txtQuantityShow = view.findViewById(R.id.txtCurrentQuantity);
            btnAddToCart = view.findViewById(R.id.btnAddToCart);

            btnPlus.setOnClickListener(this);
            btnMinus.setOnClickListener(this);
            btnAddToCart.setOnClickListener(this);
        }

        public ImageButton getBtnPlus() {
            return btnPlus;
        }

        public ImageButton getBtnMinus() {
            return btnMinus;
        }

        public ImageView getImgProductImage() {
            return imgProductImage;
        }

        public TextView getTxtProductName() {
            return txtProductName;
        }

        public TextView getTxtProductQuantity() {
            return txtProductQuantity;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ProductModel productModel = localDataSet.get(position);
            if (v.getId() == R.id.btnQuantityPlus) {
                // Increase the quantity of product
                int quantity = Integer.parseInt(txtQuantityShow.getText().toString());
                if(quantity < Integer.parseInt(txtProductQuantity.getText().toString())) {
                    quantity++;
                    productModel.setCurrent_stock(String.valueOf(Integer.parseInt(productModel.getCurrent_stock())-1));
                    txtQuantityShow.setText(String.valueOf(quantity));
                }else
                    Toast.makeText(v.getContext(), "Quantity is not available", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.btnQuantityMinus) {
                // Decrease the quantity of product
                int quantity = Integer.parseInt(txtQuantityShow.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    productModel.setCurrent_stock(String.valueOf(Integer.parseInt(productModel.getCurrent_stock())+1));
                    txtQuantityShow.setText(String.valueOf(quantity));
                }else
                    Toast.makeText(v.getContext(), "Quantity is not available", Toast.LENGTH_SHORT).show();
            }else if (v.getId() == R.id.btnAddToCart) {
                btnAddToCartClicked(position,v);
            }
        }

        private void btnAddToCartClicked(int position,View v) {
            if(Integer.parseInt(txtQuantityShow.getText().toString()) == 0) {
                Toast.makeText(v.getContext(), "Please select at least one quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int res = Integer.parseInt(btnAddToCart.getTag().toString());

            if(res == R.drawable.close_vector){
                btnAddToCart.setBackground(v.getResources().getDrawable(R.drawable.addtocartvector));
                btnAddToCart.setTag(R.drawable.addtocartvector);

                transactionModel.getITEM_LIST().remove(localDataSet.get(position));
            }else {
                btnAddToCart.setBackground(v.getResources().getDrawable(R.drawable.close_vector));
                btnAddToCart.setTag(R.drawable.close_vector);

                transactionModel.getITEM_LIST().add(localDataSet.get(position));
                transactionModel.getDbTransactionModel().getITEM_LIST().add(new SelectItemModel(localDataSet.get(position).getId(),localDataSet.get(position).getName(),txtQuantityShow.getText().toString(),localDataSet.get(position).getSale_price()));
            }
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    // Constructor of ProductAdapter class having two arguments of datalist and context
    public SelectItem_Adapter(ArrayList<ProductModel> dataSet, Context context, TransactionModel transactionModel) {
        this.localDataSet = dataSet; // initialing dataset
        this.context = context; // initialing context
        this.transactionModel = transactionModel; // initialing transaction model
    }

    public void setLocalDataSet(ArrayList<ProductModel> localDataSet) {
        this.localDataSet = localDataSet;
        notifyDataSetChanged();
    }


    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public SelectItem_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.select_item_layout, viewGroup, false);

        return new SelectItem_Adapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull SelectItem_Adapter.ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // setting product name
        viewHolder.getTxtProductName().setText(localDataSet.get(position).getName());

        // setting product quantity
        viewHolder.getTxtProductQuantity().setText(String.valueOf(localDataSet.get(position).getCurrent_stock()));

        // setting product image
        Glide.with(context).load(localDataSet.get(position).getPicture()).into(viewHolder.getImgProductImage());
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