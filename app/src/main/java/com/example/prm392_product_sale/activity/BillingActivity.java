package com.example.prm392_product_sale.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.BillingAdapter;
import com.example.prm392_product_sale.databinding.ActivityBillingBinding;
import com.example.prm392_product_sale.model.CartItem;

import java.util.List;

public class BillingActivity extends AppCompatActivity {

    private static final String TAG = "BillingActivity";
    RecyclerView rvBilling;
    TextView tvSubtotal, tvShipping, tvTax, tvTotal;
    ActivityBillingBinding binding;
    private List<CartItem> cartItemList;
    private BillingAdapter billingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBillingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("cartItemList")) {
            cartItemList = (List<CartItem>) getIntent().getSerializableExtra("cartItemList");
        }

        rvBilling = binding.rvBilling;
        tvSubtotal = binding.tvSubtotalBilling;
        tvShipping = binding.tvFeeDeliveryBilling;
        tvTax = binding.tvTaxBilling;
        tvTotal = binding.tvTotalBilling;

        billingAdapter = new BillingAdapter(cartItemList, this);
        rvBilling.setLayoutManager(new LinearLayoutManager(this));
        rvBilling.setAdapter(billingAdapter);

        Float subtotal = Float.parseFloat(getIntent().getStringExtra("totalPrice"));
        String shipping = "Free";
        Float tax = Float.parseFloat(getIntent().getStringExtra("totalPrice")) * 0.1f;
        Float total = subtotal + tax;

        tvSubtotal.setText(String.format("%.2f $", Float.parseFloat(getIntent().getStringExtra("totalPrice"))));
        tvShipping.setText(shipping);
        tvTax.setText(String.format("%.2f $", Float.parseFloat(getIntent().getStringExtra("totalPrice")) * 0.1f));
        tvTotal.setText(String.format("%.2f $", total));

        Toolbar toolbar = findViewById(R.id.tb_billing);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Check out");
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