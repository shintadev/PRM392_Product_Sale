package com.example.prm392_product_sale.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> items;

    public CartAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImageView;
        public TextView productTilleTextView;
        public TextView productPriceTextView;;
        public TextView productTotalPriceTextView;
        public EditText productQuantityEditText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.iv_product_cart);
            productTilleTextView = itemView.findViewById(R.id.tv_product_title_cart);
            productPriceTextView = itemView.findViewById(R.id.tv_price_cart);
            productTotalPriceTextView = itemView.findViewById(R.id.tv_total_price_cart);
            productQuantityEditText = itemView.findViewById(R.id.et_product_quantity);
        }

        public void bind(CartItem item) {
            Picasso.get().load(item.getProduct().getUrl()).resize(100, 100).centerCrop().into(productImageView);
            productTilleTextView.setText(item.getProduct().getTitle());
            productPriceTextView.setText("$" + String.format("%.2f", item.getProduct().getPrice()));
            productTotalPriceTextView.setText((String.valueOf(item.getProduct().getPrice()*item.getQuantity())));
            productQuantityEditText.setText(String.valueOf(item.getQuantity()));
        }
    }
}
