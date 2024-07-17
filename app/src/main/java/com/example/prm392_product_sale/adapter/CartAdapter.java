package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.CartItem;
import com.example.prm392_product_sale.model.CartManager;
import com.example.prm392_product_sale.service.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> items;
    private Context context;
    private CartUpdateListener cartUpdateListener;
    private FirebaseUser user;
    private CartManager cartManager;

    public CartAdapter(List<CartItem> items, Context context, CartUpdateListener cartUpdateListener) {
        this.items = items;
        this.context = context;
        this.cartUpdateListener = cartUpdateListener;
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        cartManager = new CartManager(user.getUid(), context);

        Picasso.get().load(item.getProduct().getUrl()).into(holder.ivProductCart);
        holder.tvProductTitleCart.setText(item.getProduct().getTitle());
        holder.tvProductPriceCart.setText(String.format("$%.2f", item.getProduct().getPrice()));
        holder.tvProductTotalPriceCart.setText((String.format("$%.2f", item.getProduct().getPrice() * item.getQuantity())));
        holder.etProductQuantityCart.setText(String.valueOf(item.getQuantity()));
        holder.etProductQuantityCart.setEnabled(false);

        holder.btnProductLessCart.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.etProductQuantityCart.setText(String.valueOf(newQuantity));
                cartManager.updateCartItemQuantity(item.getProduct().getId(), newQuantity);
                holder.tvProductTotalPriceCart.setText(String.format("$%.2f", item.getProduct().getPrice() * newQuantity));
            } else {
                cartManager.removeCartItem(item.getProduct().getId());
                items.remove(item);
                notifyDataSetChanged();
            }
            cartUpdateListener.onCartUpdated();
            updateCartNotification(context);
        });

        holder.btnProductMoreCart.setOnClickListener(v -> {
            if (item.getQuantity() < 10) {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity);
                holder.etProductQuantityCart.setText(String.valueOf(newQuantity));
                cartManager.updateCartItemQuantity(item.getProduct().getId(), newQuantity);
                holder.tvProductTotalPriceCart.setText(String.format("$%.2f", item.getProduct().getPrice() * newQuantity));
                cartUpdateListener.onCartUpdated();
                updateCartNotification(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProductCart;
        public TextView tvProductTitleCart, tvProductPriceCart, tvProductTotalPriceCart, tvTotalPrice;
        public EditText etProductQuantityCart;
        AppCompatButton btnProductLessCart, btnProductMoreCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductCart = itemView.findViewById(R.id.iv_product_cart);
            tvProductTitleCart = itemView.findViewById(R.id.tv_product_title_cart);
            tvProductPriceCart = itemView.findViewById(R.id.tv_price_cart);
            tvProductTotalPriceCart = itemView.findViewById(R.id.tv_total_price_cart);
            etProductQuantityCart = itemView.findViewById(R.id.et_product_quantity_cart);
            btnProductLessCart = itemView.findViewById(R.id.btn_product_less_cart);
            btnProductMoreCart = itemView.findViewById(R.id.btn_product_more_cart);
            if (itemView.findViewById(R.id.tv_total) != null)
                tvTotalPrice = itemView.findViewById(R.id.tv_total);

        }
    }

    private void updateCartNotification(Context context) {
        CartManager.FirestoreCallback callback = new CartManager.FirestoreCallback() {

            @Override
            public void onBooleanCallback(boolean exists) {

            }

            @Override
            public void onIntCallback(int count) {
                Intent serviceIntent = new Intent(context, NotificationService.class);
                serviceIntent.putExtra("cartItemCount", count);
                context.startService(serviceIntent);
            }

            @Override
            public void onFloatCallback(float totalPrice) {

            }
        };

        cartManager.getCartItemCount(callback); // Implement a method to get the cart item count
    }

    public interface CartUpdateListener {
        void onCartUpdated();
    }
}


