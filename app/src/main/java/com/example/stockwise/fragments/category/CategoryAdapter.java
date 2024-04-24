package com.example.stockwise.fragments.category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockwise.R;
import com.example.stockwise.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final ArrayList<CategoryModel> localDataSet; // creating arraylist of CategoryModel
    private final Context context; // creating context

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtCategoryName; // creating textview for category name
        private final TextView txtAvailableProducts; // creating textview for available products

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this); // setting onclick listener on view
            txtCategoryName = (TextView) view.findViewById(R.id.txtCategoryName); // initialing category name
            txtAvailableProducts = (TextView) view.findViewById(R.id.txtAvailableProduct); // initialing available products
        }

        // getter methods for category name and available products
        public TextView getTxtCategoryName() {
            return txtCategoryName;
        }

        public TextView getTxtAvailableProducts() {
            return txtAvailableProducts;
        }

        // onclick listener for view
        @Override
        public void onClick(View v) {
            int pos = this.getAbsoluteAdapterPosition(); // getting position of clicked item
            String category_id = localDataSet.get(pos).getId(); // getting category id
            String category_name = localDataSet.get(pos).getName(); // getting category name

            Intent intent = new Intent(context, ProductList.class); // creating intent for ProductList class
            intent.putExtra("category_id", category_id); // putting category id in intent
            intent.putExtra("category_name", category_name); // putting category name in intent
            context.startActivity(intent); // starting activity
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    // Constructor of ProductAdapter class having two arguments of datalist and context
    public CategoryAdapter(ArrayList<CategoryModel> dataSet, Context context) {
        this.localDataSet = dataSet; // initialing dataset
        this.context = context; // initialing context
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_layout, viewGroup, false);

        return new CategoryAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.getTxtCategoryName().setText(localDataSet.get(position).getName()); // setting category name

        String availableProducts; // creating string for available products
        if (localDataSet.get(position).getArrProducts() == null) // checking if products are available or not
            availableProducts = "Total Products : 0"; // setting available products as 0
        else
            // setting total products in the category
            availableProducts = "Total Products : " + localDataSet.get(position).getArrProducts().size();
        viewHolder.getTxtAvailableProducts().setText(availableProducts); // setting available products
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}