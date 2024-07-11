package com.example.prm392_product_sale.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_product_sale.databinding.ActivityProductDetailBinding;

public class ProductDetailActivity extends AppCompatActivity {

    private EditText quantity;
    private final int min = 0;
    private final int max = 10;
    ActivityProductDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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