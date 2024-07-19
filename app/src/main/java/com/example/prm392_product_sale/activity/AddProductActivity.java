package com.example.prm392_product_sale.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.databinding.ActivityAddProductBinding;
import com.example.prm392_product_sale.model.Product;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import javax.annotation.Nullable;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    ActivityAddProductBinding binding;
    String title, description, category;
    float price;
    private EditText etProductTitle, etProductPrice, etProductDescription;
    private ImageView ivProductImage;
    private Button btnPickImage, btnAddProduct;
    private Spinner productCategorySpinner;
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
        productCategorySpinner = binding.spinnerProductCategory;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(adapter);

        productCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        category = "";
                        break;
                    case 1:
                        category = "laptop";
                        break;
                    case 2:
                        category = "phone";
                        break;
                    case 3:
                        category = "headp";
                        break;
                    case 4:
                        category = "games";
                        break;
                    case 5:
                        category = "others";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(ivProductImage);
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
        title = etProductTitle.getText().toString();
        description = etProductDescription.getText().toString();
        price = Float.parseFloat((!etProductPrice.getText().toString().isEmpty()) ? etProductPrice.getText().toString() : String.valueOf(0));
        if (title.isEmpty() || description.isEmpty() || price == 0 || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri != null) {
            binding.addProductLoading.setVisibility(View.VISIBLE);
            btnAddProduct.setEnabled(false);
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
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProductToFirestore(String imageUrl) {
        Product product = new Product("", title, description, imageUrl, price, category);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.e(TAG, "saveProductToFirestore:failed", e));
    }
}