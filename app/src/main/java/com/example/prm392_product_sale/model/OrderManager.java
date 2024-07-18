package com.example.prm392_product_sale.model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class OrderManager {
    private String TAG = "OrderManager";
    private FirebaseFirestore db;
    private String userId;
    private Context context;

    public OrderManager(String userId, Context context) {
        this.userId = userId;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void addOrder(Order order) {
        db.collection("orders")
                .document(userId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "addOrder:success");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "addOrder:failure", e);
                });
    }

    public void removeOrder(String order) {

    }
}
