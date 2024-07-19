package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.CartManager;
import com.example.prm392_product_sale.model.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderItem> items;
    private String userId;
    private Context context;

    public OrderDetailAdapter(List<OrderItem> orderDetailList, String userId, Context context) {
        this.items = orderDetailList;
        this.userId = userId;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem orderItem = items.get(position);

        Picasso.get().load(orderItem.getProduct().getUrl()).into(holder.ivOrderDetailImage);
        holder.tvTitle.setText(orderItem.getProduct().getTitle());
        holder.tvPrice.setText(String.format("$%.2f", orderItem.getProduct().getPrice()));
        holder.tvTotal.setText(String.format("$%.2f", orderItem.getTotalPrice()));
        holder.tvQuantity.setText(String.format("x%d", orderItem.getQuantity()));

        CartManager cartManager = new CartManager(userId, context);
        holder.btnBuyAgain.setOnClickListener(v -> {
            cartManager.addToCart(orderItem.getProduct(), orderItem.getQuantity(), () -> {
                Log.d("TAG", "onBindViewHolder: ");
            });
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivOrderDetailImage;
        public TextView tvTitle, tvPrice, tvTotal, tvQuantity;
        public Button btnBuyAgain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrderDetailImage = itemView.findViewById(R.id.iv_product_order_detail);
            tvTitle = itemView.findViewById(R.id.tv_product_title_order_detail);
            tvPrice = itemView.findViewById(R.id.tv_price_order_detail);
            tvTotal = itemView.findViewById(R.id.tv_total_price_order_detail);
            tvQuantity = itemView.findViewById(R.id.tv_product_quantity_order_detail);
            btnBuyAgain = itemView.findViewById(R.id.btn_buy_again);
        }
    }
}
