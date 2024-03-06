package com.example.stockwise.fragments.person;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockwise.R;
import com.example.stockwise.model.PersonModel;

import java.util.ArrayList;
import java.util.Random;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private ArrayList<PersonModel> localDataSet;
    private final Context context;
    private Random random = new Random();
    private int[] arrMaleImg = {R.drawable.male1, R.drawable.male2, R.drawable.male3, R.drawable.male4, R.drawable.male5, R.drawable.male6};
    private int[] arrFemaleImg = {R.drawable.female1, R.drawable.female2, R.drawable.female3, R.drawable.female4, R.drawable.female5, R.drawable.female6};

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtPersonName;
        private final TextView txtPersonContact;
        private final ImageView imgPerson;
        public ViewHolder(View view) {
            super(view);
            txtPersonName = (TextView) view.findViewById(R.id.txtContactName);
            txtPersonContact = (TextView) view.findViewById(R.id.txtContactDetails);
            imgPerson = (ImageView) view.findViewById(R.id.ContactImage);
        }

        public ImageView getImgPerson() {
            return imgPerson;
        }

        public TextView getTxtPersonName() {
            return txtPersonName;
        }

        public TextView getTxtPersonContact() {
            return txtPersonContact;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    // Constructor of ProductAdapter class having two arguments of datalist and context
    public PersonAdapter(ArrayList<PersonModel> dataSet, Context context) {
        this.localDataSet = dataSet; // initialing dataset
        this.context = context; // initialing context
    }

    public void setLocalDataset(ArrayList<PersonModel> dataSet) {
        this.localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PersonAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contacts_layout, viewGroup, false);

        return new PersonAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        // setting person name
        viewHolder.getTxtPersonName().setText(localDataSet.get(position).getName());
        // setting Contact Number
        viewHolder.getTxtPersonContact().setText(localDataSet.get(position).getContact_num());
        // setting person image
        if(localDataSet.get(position).getGender().equals("Male")){
            viewHolder.getImgPerson().setImageResource(arrMaleImg[random.nextInt(arrMaleImg.length)]);
        } else if (localDataSet.get(position).getGender().equals("Female")) {
            viewHolder.getImgPerson().setImageResource(arrFemaleImg[random.nextInt(arrFemaleImg.length)]);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
