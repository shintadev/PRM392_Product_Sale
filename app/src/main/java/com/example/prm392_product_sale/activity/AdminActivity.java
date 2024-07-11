package com.example.prm392_product_sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.AdminAdapter;
import com.example.prm392_product_sale.databinding.ActivityAdminBinding;
import com.example.prm392_product_sale.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    TextView tvEmptyProductAdmin, tvTotalProductsAdmin;
    Button btnAddProduct;
    RecyclerView rvProductListAdmin;
    AdminAdapter adapter;
    ActivityAdminBinding binding;
    private List<Product> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvEmptyProductAdmin = binding.tvEmptyProductAdmin;
        tvTotalProductsAdmin = binding.tvTotalProductsAdmin;
        btnAddProduct = binding.btnAddProduct;
        rvProductListAdmin = binding.rvProductListAdmin;
        rvProductListAdmin.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new AdminAdapter(this, productList);
        rvProductListAdmin.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        btnAddProduct.setOnClickListener(v -> addProduct());
        binding.chatImage.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AdminConversationListActivity.class);
            intent.putExtra("senderId", getString(R.string.default_receiverId));
            intent.putExtra("senderName", "ADMIN");
            intent.putExtra("isSenderAdmin", true);
            startActivity(intent);
        });

        loadTotalProducts();

        Toolbar toolbar = binding.adminToolbar;
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Dashboard");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
        adapter.notifyDataSetChanged();
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

    public void loadTotalProducts() {
        TextView tvTotalProductsAdmin = binding.tvTotalProductsAdmin;
        tvTotalProductsAdmin.setText(String.valueOf(productList.size()));
    }

    public void loadProducts() {
        productList.clear();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = new Product();

                                String id = document.getId();
                                String title = document.getString("title");
                                String description = document.getString("description");
                                float price = document.getDouble("price").floatValue();
                                float oldPrice = document.getDouble("oldPrice").floatValue();
                                String url = document.getString("url");

                                product.setId(id);
                                product.setTitle(title);
                                product.setDescription(description);
                                product.setPrice(price);
                                product.setOldPrice(oldPrice);
                                product.setUrl(url);

                                productList.add(product);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void addProduct() {
        Intent intent = new Intent(this, AddProductActivity.class);
        startActivity(intent);
    }
}