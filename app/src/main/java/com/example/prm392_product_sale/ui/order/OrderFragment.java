package com.example.prm392_product_sale.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.activity.LoginActivity;
import com.example.prm392_product_sale.adapter.OrderAdapter;
import com.example.prm392_product_sale.databinding.FragmentOrderBinding;
import com.example.prm392_product_sale.model.Order;
import com.example.prm392_product_sale.model.OrderManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private static final String TAG = "OrderFragment";
    private RecyclerView rvOrder;
    private OrderAdapter orderAdapter;
    private FirebaseUser user;
    private OrderManager orderManager;
    private List<Order> orders;
    private FragmentOrderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (getActivity() != null) {
            orderManager = new OrderManager(user.getUid(), getActivity());
            loadOrders();
        }

        rvOrder = binding.rvOrder;
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(orders,getContext());
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.setAdapter(orderAdapter);

        return root;
    }

    public void loadOrders() {
        orderManager.getOrders(task -> {
            if (task.isSuccessful() && binding != null) {
                orders.clear();
                if (task.getResult().isEmpty()) {
                    orderAdapter.notifyDataSetChanged();
                    binding.tvEmptyOrder.setText("There is no order");
                    binding.tvEmptyOrder.setVisibility(View.VISIBLE);
                } else {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        Order order = snapshot.toObject(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    orderAdapter.notifyDataSetChanged();
                }
                binding.pbOrder.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "loadOrders:failed", task.getException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
