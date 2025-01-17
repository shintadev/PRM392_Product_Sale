package com.example.prm392_product_sale.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.prm392_product_sale.adapter.TabAdapter;
import com.example.prm392_product_sale.databinding.ActivityProductDetailBinding;
import com.example.prm392_product_sale.model.CartManager;
import com.example.prm392_product_sale.model.Product;
import com.example.prm392_product_sale.model.Review;
import com.example.prm392_product_sale.model.UserInReview;
import com.example.prm392_product_sale.service.NotificationService;
import com.example.prm392_product_sale.ui.fragment.DescriptionFragment;
import com.example.prm392_product_sale.ui.fragment.ReviewFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    private DescriptionFragment descriptionFragment;
    private ReviewFragment reviewFragment;

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

        final AppCompatButton buttonLess = binding.btnProductLess;
        buttonLess.setOnClickListener(v -> {
            final int value = getValue();
            if (value > min) {
                setValue(value - 1);
            }
        });

        final AppCompatButton buttonMore = binding.btnProductMore;
        buttonMore.setOnClickListener(v -> {
            final int value = getValue();
            if (value < max) {
                setValue(value + 1);
            }
        });


        loadProduct();
        ViewPager viewPagerLayout = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        TabAdapter tabAdapter = new TabAdapter(this);
        viewPagerLayout.setAdapter(tabAdapter);
        descriptionFragment = tabAdapter.getDescriptionFragment();
        reviewFragment = tabAdapter.getReviewFragment();

        tabLayout.setupWithViewPager(viewPagerLayout);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                switch (i) {
                    case 0:
                        tab.setText("Description");
                        break;
                    case 1:
                        tab.setText("Review");
                        break;
//                    case 2:
//                        tab.setText("Sold");
//                        break;
                }
            }
        }
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
                            Picasso.get().load(document.getString("url")).fit().centerInside().into(binding.ivDetailProduct);

                            binding.tvProductTitle.setText(document.getString("title"));
                            descriptionFragment.setDescription(document.getString("description"));
                            binding.tvProductPrice.setText(String.format("$%.2f", document.getDouble("price").floatValue()));
                            binding.tvTotal.setText(String.format("$%.2f", getValue() * document.getDouble("price").floatValue()));
                            reviewFragment.setReviews(dataExampleForReview());
                            Product product = new Product(
                                    document.getId(),
                                    document.getString("title"),
                                    document.getString("description"),
                                    document.getString("url"),
                                    document.getDouble("oldPrice").floatValue(),
                                    document.getString("title")
                            );
                            quantity.addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {

                                    binding.tvTotal.setText(String.format("$%.2f", getValue() * document.getDouble("price").floatValue()));
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });

                            final AppCompatButton buttonAddToCart = binding.btnAddToCart;
                            buttonAddToCart.setOnClickListener(v -> {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user == null) {
                                    Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    return;
                                }
                                cartManager = new CartManager(user.getUid(), this);
                                cartManager.addToCart(product, getValue(), () -> {
                                    updateCartNotification(this);
                                });
                            });

                            binding.pbProductDetail.setVisibility(View.GONE);
                            binding.svProductDetail.setVisibility(View.VISIBLE);
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

    private List<Review> dataExampleForReview() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(1, "Great Product!", "I have been using this product for a year and it has exceeded my expectations.", 5, "2024-01-05", new UserInReview(1, "JohnDoe", "https://example.com/avatar1.jpg")));
        reviews.add(new Review(2, "Not bad", "The product is decent but could use some improvements.", 3, "2023-02-04", new UserInReview(2, "JaneSmith", "https://example.com/avatar2.jpg")));
        reviews.add(new Review(3, "Terrible experience", "I had a bad experience with this product. It broke down after a month.", 1, "2022-03-03", new UserInReview(3, "MikeJohnson", "https://example.com/avatar3.jpg")));
        reviews.add(new Review(4, "Good value for money", "The product is worth the price. Good value for money.", 4, "2021-04-02", new UserInReview(4, "EmilyClark", "https://example.com/avatar4.jpg")));
        reviews.add(new Review(5, "Excellent customer service", "Customer service was very responsive and helpful.", 5, "2020-05-01", new UserInReview(5, "ChrisBrown", "https://example.com/avatar5.jpg")));
        return reviews;
    }
}