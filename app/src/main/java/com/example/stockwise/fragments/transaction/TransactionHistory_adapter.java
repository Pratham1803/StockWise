package com.example.stockwise.fragments.transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.model.DbTransactionModel;
import com.example.stockwise.model.SelectItemModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class TransactionHistory_adapter extends RecyclerView.Adapter<TransactionHistory_adapter.ViewHolder>{
    private ArrayList<DbTransactionModel> localDataSet; // list to store data
    private ArrayList<String> lsName = new ArrayList<>(); // list to store name
    private Context context; // context of the activity

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtPersonName; // name of the person
        private final TextView txtPersonType; // type of the person
        private final TextView txtQuantity; // quantity of the product
        private final TextView txtTotalPrice; // total price of the product
        private final TextView txtDate; // date of the transaction

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtPersonName = itemView.findViewById(R.id.txtPersonName); // initialize the name of the person
            this.txtPersonType = itemView.findViewById(R.id.txtPersonType); // initialize the type of the person
            this.txtQuantity = itemView.findViewById(R.id.txtTotalProduct); // initialize the quantity of the product
            this.txtTotalPrice = itemView.findViewById(R.id.txtTotalAmount); // initialize the total price of the product
            this.txtDate = itemView.findViewById(R.id.txtDate); // initialize the date of the transaction
            itemView.setOnClickListener(this); // set the click listener
        }

        // getter methods
        public TextView getTxtDate() {
            return txtDate;
        }

        public TextView getTxtPersonName() {
            return txtPersonName;
        }

        public TextView getTxtPersonType() {
            return txtPersonType;
        }

        public TextView getTxtQuantity() {
            return txtQuantity;
        }

        public TextView getTxtTotalPrice() {
            return txtTotalPrice;
        }

        // on click listener
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // get the position of the item clicked
            DbTransactionModel dbTransactionModel = localDataSet.get(position); // get the transaction model
            Intent intent = new Intent(context, GenerateBill.class); // create an intent to move to the next activity
            intent.putExtra("dbTransactionObj", dbTransactionModel); // put the transaction model in the intent
            intent.putExtra("CallFrom","History"); // put the call from in the intent
            intent.putExtra("Name",lsName.get(position)); // put the name in the intent
            context.startActivity(intent); // start the activity
        }
    }

    // constructor of adapter class
    public TransactionHistory_adapter(ArrayList<DbTransactionModel> localDataSet,ArrayList<String> lsName, Context context){
        this.localDataSet = localDataSet; // set the data
        this.context = context; // set the context
        this.lsName = lsName; // set the name
    }

    @NonNull
    @Override
    public TransactionHistory_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_history_layout, parent, false);

        return new TransactionHistory_adapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionHistory_adapter.ViewHolder holder, int position) {
        // set the data in the view
        if (localDataSet.get(position).getIsPurchase().equals("true")) { // check if the transaction is purchase
            holder.getTxtPersonType().setText("Vendor : "); // set the type of the person
            holder.getTxtTotalPrice().setTextColor(context.getResources().getColor(R.color.black)); // set the color of the total price
        }else { // if the transaction is sale
            holder.getTxtTotalPrice().setTextColor(context.getResources().getColor(R.color.black)); // set the color of the total price
        }

        // calculate the quantity
        int quantity = 0;
        for (SelectItemModel itemModel: localDataSet.get(position).getITEM_LIST()) { // loop through the item list
            quantity += Integer.parseInt(itemModel.getQuantity()); // calculate the quantity
        }

        holder.getTxtQuantity().setText(String.valueOf(quantity)); // set the quantity of the product
        holder.getTxtPersonName().setText(lsName.get(position)); // set the name of the person
        holder.getTxtDate().setText(localDataSet.get(position).getDate()); // set the date of the transaction
        holder.getTxtTotalPrice().setText(localDataSet.get(position).getTotal_price()+" /-"); // set the total price of the product
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}