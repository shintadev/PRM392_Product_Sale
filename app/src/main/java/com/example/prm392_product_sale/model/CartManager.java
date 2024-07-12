package com.example.prm392_product_sale.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String TAG = "CartManager";
    private FirebaseFirestore db;
    private String userId;
    private Context context;

    public CartManager(String userId, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.context = context;
    }

    public void addToCart(Product product, int quantity) {
        CartItem cartItem = new CartItem(product, quantity);
        db.collection("users").document(userId)
                .collection("cart").document(product.getId())
                .set(cartItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to add to cart", e);
                });
    }

    public void updateCartItemQuantity(String productId, int quantity) {
        db.collection("users").document(userId)
                .collection("cart").document(productId)
                .update("quantity", quantity)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Updated cart item quantity", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update cart item quantity", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update cart item quantity", e);
                });
    }

    public void removeCartItem(String productId) {
        db.collection("users").document(userId)
                .collection("cart").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to remove from cart", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to remove from cart", e);
                });
    }

    public void getCartItems(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection("users").document(userId)
                .collection("cart").get()
                .addOnCompleteListener(onCompleteListener);
    }

    public float getTotalPrice() {
        final float[] total = {0};
        db.collection("users").document(userId)
                .collection("cart").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CartItem> cartItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartItem cartItem = document.toObject(CartItem.class);
                            cartItems.add(cartItem);
                            total[0] += cartItem.getProduct().getPrice() * cartItem.getQuantity();
                        }
                    }
                });

        return total[0];
    }
}
