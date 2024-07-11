package com.example.prm392_product_sale.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_product_sale.databinding.ActivityAddProductBinding;
import com.example.prm392_product_sale.model.Product;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import javax.annotation.Nullable;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    ActivityAddProductBinding binding;
    private EditText etProductTitle, etProductPrice, etProductDescription;
    private ImageView ivProductImage;
    private Button btnPickImage, btnAddProduct;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etProductTitle = binding.etProductTitle;
        etProductPrice = binding.etProductPrice;
        etProductDescription = binding.etProductDescription;
        ivProductImage = binding.ivProductImage;
        btnPickImage = binding.btnPickImage;
        btnAddProduct = binding.btnAddProduct;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        btnPickImage.setOnClickListener(v -> ImagePicker.with(this).start());

        btnAddProduct.setOnClickListener(v -> uploadImageAndSaveProduct());

        Toolbar toolbar = binding.addProductToolbar;
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Product");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            ivProductImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Image pick failed", Toast.LENGTH_SHORT).show();
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

    private void uploadImageAndSaveProduct() {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveProductToFirestore(imageUrl);
                        finish();
                    }))
                    .addOnFailureListener(e -> Log.e(TAG, "Image upload failed", e));
        } else {
            saveProductToFirestore(null);
        }
    }

    private void saveProductToFirestore(String imageUrl) {
        String title = etProductTitle.getText().toString();
        String description = etProductDescription.getText().toString();
        float price = Float.parseFloat(etProductPrice.getText().toString());

        Product product = new Product(UUID.randomUUID().toString(), title, description, imageUrl, price);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "saveProductToFirestore:failed", e));
    }
}