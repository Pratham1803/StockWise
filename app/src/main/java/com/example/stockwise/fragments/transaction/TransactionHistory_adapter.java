package com.example.stockwise.fragments.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockwise.Params;
import com.example.stockwise.R;
import com.example.stockwise.model.DbTransactionModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class TransactionHistory_adapter extends RecyclerView.Adapter<TransactionHistory_adapter.ViewHolder>{
    private ArrayList<DbTransactionModel> localDataSet;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtPersonName;
        private final TextView txtPersonType;
        private final TextView txtQuantity;
        private final TextView txtTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtPersonName = itemView.findViewById(R.id.txtPersonName);
            this.txtPersonType = itemView.findViewById(R.id.txtPersonType);
            this.txtQuantity = itemView.findViewById(R.id.txtTotalProduct);
            this.txtTotalPrice = itemView.findViewById(R.id.txtTotalAmount);
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
    }

    // constructor of adapter class
    public TransactionHistory_adapter(ArrayList<DbTransactionModel> localDataSet, Context context){
        this.localDataSet = localDataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionHistory_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_history_layout, parent, false);

        return new TransactionHistory_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHistory_adapter.ViewHolder holder, int position) {
        if (localDataSet.get(position).getIsPurchase().equals("true")) {
            holder.getTxtPersonType().setText("Vendor : ");
            holder.getTxtTotalPrice().setTextColor(context.getResources().getColor(R.color.red));
        }else
            holder.getTxtTotalPrice().setTextColor(context.getResources().getColor(R.color.SuccessGreen));

        Params.getREFERENCE().child(Params.getPERSON()).child(Params.getCUSTOMER()).child(localDataSet.get(position).getPerson_id()).child(Params.getNAME()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        holder.getTxtPersonName().setText(dataSnapshot.getValue(String.class));
                    }
                });

        if (localDataSet.get(position).getIsPurchase().equals("true"))
            holder.getTxtQuantity().setText(String.valueOf(localDataSet.get(position).getITEM_LIST().size())+" -");
        else
            holder.getTxtQuantity().setText(String.valueOf(localDataSet.get(position).getITEM_LIST().size())+" +");

        holder.getTxtTotalPrice().setText(localDataSet.get(position).getTotal_price()+" /-");
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
