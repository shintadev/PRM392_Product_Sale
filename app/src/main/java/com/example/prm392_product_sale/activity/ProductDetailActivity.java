package com.example.prm392_product_sale.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.prm392_product_sale.adapter.CartAdapter;
import com.example.prm392_product_sale.databinding.ActivityProductDetailBinding;
import com.example.prm392_product_sale.model.CartManager;
import com.example.prm392_product_sale.model.Product;
import com.example.prm392_product_sale.service.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private final int min = 1;
    private final int max = 10;
    ActivityProductDetailBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private CartManager cartManager;
    private String productId;
    private EditText quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = binding.tbProductDetail;
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(null);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        quantity = binding.etProductQuantity;

        final Button buttonLess = binding.btnProductLess;
        buttonLess.setOnClickListener(v -> {
            final int value = getValue();
            if (value > min) {
                setValue(value - 1);
            }
        });

        final Button buttonMore = binding.btnProductMore;
        buttonMore.setOnClickListener(v -> {
            final int value = getValue();
            if (value < max) {
                setValue(value + 1);
            }
        });


        loadProduct();
    }

    public void loadProduct() {
        productId = getIntent().getStringExtra("productId");
        db.collection("products")
                .document(productId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "loadProduct:success ");
                        if (document.exists() && binding != null) {
                            Glide.with(this)
                                    .load(document.getString("url"))
                                    .into(binding.ivDetailProduct);

                            binding.tvProductTitle.setText(document.getString("title"));
                            binding.tvProductPrice.setText(String.format("$%.2f", document.getDouble("price").floatValue()));
                            binding.tvProductDescription.setText(document.getString("description"));
                            binding.tvTotal.setText(String.format("$%.2f", getValue() * document.getDouble("price").floatValue()));

                            Product product = document.toObject(Product.class);
                            quantity.addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {

                                    binding.tvTotal.setText(String.format("$%.2f", getValue() * document.getDouble("price").floatValue()));
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });

                            final Button buttonAddToCart = binding.btnAddToCart;
                            buttonAddToCart.setOnClickListener(v -> {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                cartManager = new CartManager(user.getUid(),this);
                                cartManager.addToCart(product, getValue(), () -> {
                                    updateCartNotification(this);
                                });
                            });
                        }
                    } else Log.e(TAG, "loadProduct:failure", task.getException());
                });
    }

    public int getValue() {
        if (quantity != null) {
            try {
                final String value = quantity.getText().toString();
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                Log.e("HorizontalNumberPicker", ex.toString());
            }
        }
        return 0;
    }

    public void setValue(final int value) {
        if (quantity != null) {
            quantity.setText(String.valueOf(value));
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

        cartManager.getCartItemCount(callback);
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