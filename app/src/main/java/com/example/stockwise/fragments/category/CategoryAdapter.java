package com.example.stockwise.fragments.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockwise.R;
import com.example.stockwise.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final ArrayList<CategoryModel> localDataSet;
    private final Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtCategoryName;
        private final TextView txtAvailableProducts;
        public ViewHolder(View view) {
            super(view);
            txtCategoryName = (TextView) view.findViewById(R.id.txtCategoryName);
            txtAvailableProducts = (TextView) view.findViewById(R.id.txtAvailableProduct);
        }

        public TextView getTxtCategoryName() {
            return txtCategoryName;
        }

        public TextView getTxtAvailableProducts() {
            return txtAvailableProducts;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
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
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        // setting product name
        viewHolder.getTxtCategoryName().setText(localDataSet.get(position).getName());
        // setting available products
        String availableProducts =  "Total Products : " + localDataSet.get(position).getNumOfProducts();
        viewHolder.getTxtAvailableProducts().setText(availableProducts);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
