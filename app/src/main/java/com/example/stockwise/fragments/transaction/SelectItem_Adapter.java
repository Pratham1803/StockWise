package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.graphics.Color;
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
    private ArrayList<ProductModel> localDataSet; // arraylist of product model
    private final Context context; // context
    private TransactionModel transactionModel; // transaction model

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView imgProductImage; // product image
        private final TextView txtProductName; // product name
        private final TextView txtProductQuantity; // product quantity
        private final ImageButton btnPlus; // plus button
        private final ImageButton btnMinus; // minus button
        private final TextView txtCurrentQuantity; // current quantity
        private final ImageButton btnAddToCart; // add to cart button
        public ViewHolder(View view) {
            super(view);
            imgProductImage = view.findViewById(R.id.imgProductSell); // getting product image
            txtProductName = view.findViewById(R.id.txtProductNameSell); // getting product name
            txtProductQuantity = view.findViewById(R.id.txtQuantityShow); // getting product quantity
            btnPlus = view.findViewById(R.id.btnQuantityPlus); // getting plus button
            btnMinus = view.findViewById(R.id.btnQuantityMinus); // getting minus button
            txtCurrentQuantity = view.findViewById(R.id.txtCurrentQuantity); // getting current quantity
            btnAddToCart = view.findViewById(R.id.btnAddToCart); // getting add to cart button

            btnPlus.setOnClickListener(this); // setting click listener on plus button
            btnMinus.setOnClickListener(this); // setting click listener on minus button
            btnAddToCart.setOnClickListener(this); // setting click listener on add to cart button
        }

        // getter methods
        public ImageView getImgProductImage() {
            return imgProductImage;
        }

        public TextView getTxtProductName() {
            return txtProductName;
        }

        public TextView getTxtProductQuantity() {
            return txtProductQuantity;
        }

        // onClick method
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // getting position of product
            ProductModel productModel = localDataSet.get(position); // getting product model
            int currentQuan = Integer.parseInt(txtCurrentQuantity.getText().toString()); // quantity from + or - button
            int productQuan = Integer.parseInt(txtProductQuantity.getText().toString()); // total quantity of Product

            // checking which button is clicked
            if (v.getId() == R.id.btnQuantityPlus) { // if plus button is clicked
                // Increase the quantity of product
                if(currentQuan < productQuan || transactionModel.isPurchase()) { // checking if quantity is available or not
                    currentQuan++; // increasing quantity

                    // updating current stock of product
                    if(transactionModel.isPurchase()) // if purchase transaction
                        // increase the stock
                        productModel.setCurrent_stock(String.valueOf(Integer.parseInt(productModel.getCurrent_stock())+1));
                    else // if sale transaction
                        // decrease the stock
                        productModel.setCurrent_stock(String.valueOf(Integer.parseInt(productModel.getCurrent_stock())-1));

                    txtCurrentQuantity.setText(String.valueOf(currentQuan)); // setting current quantity
                }else // if quantity is not available
                    Toast.makeText(v.getContext(), "Quantity is not available", Toast.LENGTH_SHORT).show(); // showing toast message

            } else if (v.getId() == R.id.btnQuantityMinus) { // if minus button is clicked
                // Decrease the quantity of product
                if (currentQuan > 0) { // checking if quantity is greater than 0
                    currentQuan--; // decreasing quantity

                    if(transactionModel.isPurchase()) // if purchase transaction
                        // decrease the stock
                        productModel.setCurrent_stock(String.valueOf(Integer.parseInt(productModel.getCurrent_stock())-1));
                    else // if sale transaction
                        // increase the stock
                        productModel.setCurrent_stock(String.valueOf(Integer.parseInt(productModel.getCurrent_stock())+1));
                    txtCurrentQuantity.setText(String.valueOf(currentQuan)); // setting current quantity
                }else // if quantity is not available
                    Toast.makeText(v.getContext(), "Quantity is not available", Toast.LENGTH_SHORT).show(); // showing toast message

            }else if (v.getId() == R.id.btnAddToCart) { // if add to cart button is clicked
                btnAddToCartClicked(position,v); // calling add to cart method
            }
        }

        // add to cart method
        private void btnAddToCartClicked(int position,View v) {
            // checking if quantity is 0
            if(Integer.parseInt(txtCurrentQuantity.getText().toString()) == 0) { // if quantity is 0
                // showing toast message
                Toast.makeText(v.getContext(), "Please select at least one quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int res = Integer.parseInt(btnAddToCart.getTag().toString()); // getting tag of add to cart button

            if(res == R.drawable.close_vector){ // if tag is close vector
                btnAddToCart.setBackground(v.getResources().getDrawable(R.drawable.addtocartvector)); // setting background of add to cart button
                btnAddToCart.setTag(R.drawable.addtocartvector); // setting tag of add to cart button

                transactionModel.getITEM_LIST().remove(localDataSet.get(position)); // removing product from item list
            }else { // if tag is not close vector
                btnAddToCart.setBackground(v.getResources().getDrawable(R.drawable.close_vector)); // setting background of add to cart button
                btnAddToCart.setTag(R.drawable.close_vector); // setting tag of add to cart button

                String id = localDataSet.get(position).getId(); // getting product id
                String name = localDataSet.get(position).getName(); // getting product name
                String currentQuan = txtCurrentQuantity.getText().toString(); // quantity selected from + or - button
                String sale_price = localDataSet.get(position).getSale_price(); // getting sale price of product
                String purchase_price = localDataSet.get(position).getPurchase_price(); // getting purchase price of product

                transactionModel.getITEM_LIST().add(localDataSet.get(position)); // adding product to item list
                transactionModel.getDbTransactionModel().getITEM_LIST().add(new SelectItemModel(id,name,currentQuan,sale_price,purchase_price)); // adding product to db item list
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

    // setting local dataset
    public void setLocalDataSet(ArrayList<ProductModel> localDataSet) {
        this.localDataSet = localDataSet; // setting local dataset
        notifyDataSetChanged(); // notifying data set changed
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
}