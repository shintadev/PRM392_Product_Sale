package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.ViewHolder> {
    private List<CartItem> items;
    private Context context;
    private FirebaseUser user;

    public BillingAdapter(List<CartItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public BillingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_billing, parent, false);
        return new BillingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingAdapter.ViewHolder holder, int position) {
        CartItem item = items.get(position);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        Picasso.get().load(item.getProduct().getUrl()).resize(90, 90).centerCrop().into(holder.ivProductBilling);
        holder.tvProductTitleBilling.setText(item.getProduct().getTitle());
        holder.tvProductPriceBilling.setText(String.format("$%.2f", item.getProduct().getPrice()));
        holder.tvProductTotalPriceBilling.setText((String.format("$%.2f", item.getProduct().getPrice() * item.getQuantity())));
        holder.etProductQuantityBilling.setText(String.valueOf(item.getQuantity()));
        holder.etProductQuantityBilling.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProductBilling;
        public TextView tvProductTitleBilling, tvProductPriceBilling, tvProductTotalPriceBilling;
        public EditText etProductQuantityBilling;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductBilling = itemView.findViewById(R.id.iv_product_billing);
            tvProductTitleBilling = itemView.findViewById(R.id.tv_product_title_billing);
            tvProductPriceBilling = itemView.findViewById(R.id.tv_price_billing);
            tvProductTotalPriceBilling = itemView.findViewById(R.id.tv_total_price_billing);
            etProductQuantityBilling = itemView.findViewById(R.id.et_product_quantity);
        }
    }
}