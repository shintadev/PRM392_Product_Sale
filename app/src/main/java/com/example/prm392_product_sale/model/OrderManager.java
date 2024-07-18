package com.example.prm392_product_sale.model;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
        db.collection("users").document(userId)
                .collection("orders")
                .add(order)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "addOrder:success");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "addOrder:failure", e);
                });
    }

    public void removeOrder(String id) {
        db.collection("users").document(userId)
                .collection("orders").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "removeOrder:success");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "removeOrder:failure", e);
                });
    }

    public void updateOrder(Order order) {
        db.collection("users").document(userId)
                .collection("orders").document(order.getId())
                .update("status", order.getStatus())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "updateOrder:success");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "updateOrder:failure", e);
                });
    }

    public void getOrders(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection("users").document(userId)
                .collection("orders")
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void isOrderExists(String id, OrderCallback callback) {
        db.collection("users").document(userId)
                .collection("orders").document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onBoolean(task.getResult().exists());
                    } else
                        callback.onBoolean(false);
                });
    }

    public interface OrderCallback {
        void onBoolean(Boolean exists);
    }
}
