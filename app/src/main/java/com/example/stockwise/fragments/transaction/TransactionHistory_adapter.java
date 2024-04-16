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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class TransactionHistory_adapter extends RecyclerView.Adapter<TransactionHistory_adapter.ViewHolder>{
    private ArrayList<DbTransactionModel> localDataSet;
    private ArrayList<String> lsName = new ArrayList<>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtPersonName;
        private final TextView txtPersonType;
        private final TextView txtQuantity;
        private final TextView txtTotalPrice;
        private final TextView txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtPersonName = itemView.findViewById(R.id.txtPersonName);
            this.txtPersonType = itemView.findViewById(R.id.txtPersonType);
            this.txtQuantity = itemView.findViewById(R.id.txtTotalProduct);
            this.txtTotalPrice = itemView.findViewById(R.id.txtTotalAmount);
            this.txtDate = itemView.findViewById(R.id.txtDate);
            itemView.setOnClickListener(this);
        }

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

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DbTransactionModel dbTransactionModel = localDataSet.get(position);
            Intent intent = new Intent(context, GenerateBill.class);
            intent.putExtra("transactionObj", dbTransactionModel);
            intent.putExtra("CallFrom","History");
            intent.putExtra("Name",lsName.get(position));
            context.startActivity(intent);
        }
    }

    // constructor of adapter class
    public TransactionHistory_adapter(ArrayList<DbTransactionModel> localDataSet,ArrayList<String> lsName, Context context){
        this.localDataSet = localDataSet;
        this.context = context;
        this.lsName = lsName;
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
        if (localDataSet.get(position).getIsPurchase().equals("true")) {
            holder.getTxtPersonType().setText("Vendor : ");
            holder.getTxtTotalPrice().setTextColor(context.getResources().getColor(R.color.red));
            holder.getTxtQuantity().setText(String.valueOf(localDataSet.get(position).getITEM_LIST().size())+" -");
        }else {
            holder.getTxtTotalPrice().setTextColor(context.getResources().getColor(R.color.SuccessGreen));
            holder.getTxtQuantity().setText(String.valueOf(localDataSet.get(position).getITEM_LIST().size())+" +");
        }

        holder.getTxtPersonName().setText(lsName.get(position));
        holder.getTxtDate().setText(localDataSet.get(position).getDate());
        holder.getTxtTotalPrice().setText(localDataSet.get(position).getTotal_price()+" /-");
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
