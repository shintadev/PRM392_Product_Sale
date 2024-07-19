package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.activity.OrderDetailActivity;
import com.example.prm392_product_sale.model.Order;
import com.example.prm392_product_sale.model.OrderItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> items;
    private Context context;

    public OrderAdapter(List<Order> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order order = items.get(position);

        holder.tvProductTotalPriceOrder.setText(String.format("$%.2f", order.getTotalPrice()));

        StringJoiner productTitles = new StringJoiner(", ");
        for (OrderItem item : order.getOrderItems()) {
            productTitles.add(item.getProduct().getTitle());
        }
        holder.tvProductTitleOrder.setText(productTitles.toString());

        List<String> imageUrls = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            imageUrls.add(item.getProduct().getUrl());
        }

        ImageAdapter imageAdapter = new ImageAdapter(imageUrls, context);
        holder.rvProductImagesOrder.setAdapter(imageAdapter);
        holder.rvProductImagesOrder.setLayoutManager(new GridLayoutManager(context, 2));

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView rvProductImagesOrder;
        public TextView tvProductTitleOrder, tvProductTotalPriceOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProductImagesOrder = itemView.findViewById(R.id.rv_product_images_order);
            tvProductTitleOrder = itemView.findViewById(R.id.tv_product_title_order);
            tvProductTotalPriceOrder = itemView.findViewById(R.id.tv_total_price_order);
        }
    }
}
