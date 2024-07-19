package com.example.prm392_product_sale.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.adapter.OrderDetailAdapter;
import com.example.prm392_product_sale.databinding.ActivityOrderDetailBinding;
import com.example.prm392_product_sale.model.CartManager;
import com.example.prm392_product_sale.model.Order;
import com.example.prm392_product_sale.model.OrderManager;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailActivity";
    private TextView tvStatus, tvDeliveryAddress, tvSubtotal, tvShipping, tvTax, tvTotal;
    private RecyclerView rvOrderDetail;
    private Button btnBuyAgain, btnCompleteOrder;
    private OrderDetailAdapter orderDetailAdapter;
    private CartManager cartManager;
    private OrderManager orderManager;
    private ActivityOrderDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.tbOrderDetail;
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(null);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tvStatus = binding.tvOrderStatusContent;
        tvDeliveryAddress = binding.tvOrderLocationContent;
        tvSubtotal = binding.tvSubtotalOrder;
        tvShipping = binding.tvFeeDeliveryOrder;
        tvTax = binding.tvTaxOrder;
        tvTotal = binding.tvTotalOrder;
        btnBuyAgain = binding.btnBuyAgainOrder;
        btnCompleteOrder = binding.btnCompleteOrder;
        rvOrderDetail = binding.rvOrderDetail;

        if (getIntent().hasExtra("order")) {
            Order order = (Order) getIntent().getSerializableExtra("order");
            assert order != null;
            tvStatus.setText(order.getStatus());
            tvDeliveryAddress.setText(order.getAddress());
            tvSubtotal.setText(String.format("$%.2f", order.getSubtotal()));
            tvShipping.setText("Free");
            tvTax.setText(String.format("$%.2f", order.getTax()));
            tvTotal.setText(String.format("$%.2f", order.getTotalPrice()));

            orderDetailAdapter = new OrderDetailAdapter(order.getOrderItems(), order.getUserId(), this);
            rvOrderDetail.setLayoutManager(new LinearLayoutManager(this));
            rvOrderDetail.setAdapter(orderDetailAdapter);

            binding.pbOrderDetail.setVisibility(View.GONE);

            if (order.getStatus().equals("Success")) {
                btnBuyAgain.setVisibility(View.VISIBLE);
                btnCompleteOrder.setVisibility(View.GONE);
                btnBuyAgain.setOnClickListener(v -> {
                    cartManager = new CartManager(order.getUserId(), this);
                    order.getOrderItems().forEach(item -> {
                        cartManager.addToCart(item.getProduct(), item.getQuantity(), () -> {
                            Log.d(TAG, "addToCart: " + item.getProduct().getTitle());
                        });
                    });
                });
            } else {
                btnBuyAgain.setVisibility(View.INVISIBLE);
                btnCompleteOrder.setVisibility(View.VISIBLE);
                btnCompleteOrder.setOnClickListener(v -> {
                    order.setStatus("Success");
                    Log.d(TAG, "onCreate: " + order.getStatus());
                    orderManager = new OrderManager(order.getUserId(), this);
                    orderManager.updateOrder(order);
                    Toast.makeText(this, "Order is marked as Success", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}