package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.activity.OrderDetailActivity;
import com.example.prm392_product_sale.activity.ProductDetailActivity;
import com.example.prm392_product_sale.model.Order;
import com.example.prm392_product_sale.model.OrderItem;
import com.example.prm392_product_sale.model.OrderManager;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> items;
    private Context context;
    private OrderAdapter.OrderUpdateListener orderUpdateListener;
    private FirebaseUser user;
    private OrderManager orderManager;

    public OrderAdapter(List<Order> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order order = items.get(position);
        OrderItem item = order.getOrderItems().get(0);

        Picasso.get().load(item.getProduct().getUrl()).into(holder.ivOrder);
        holder.tvProductTitleOrder.setText(item.getProduct().getTitle());
        holder.tvProductPriceOrder.setText(String.format("$%.2f", item.getProduct().getPrice()));
        holder.tvProductTotalPriceOrder.setText(String.format("$%.2f", item.getTotalPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("order", (Serializable) order);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OrderUpdateListener {
        void onOrderUpdated();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivOrder;
        public TextView tvProductTitleOrder, tvProductPriceOrder, tvProductTotalPriceOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrder = itemView.findViewById(R.id.iv_product_order);
            tvProductTitleOrder = itemView.findViewById(R.id.tv_product_title_order);
            tvProductPriceOrder = itemView.findViewById(R.id.tv_price_order);
            tvProductTotalPriceOrder = itemView.findViewById(R.id.tv_total_price_order);
        }
    }
}
