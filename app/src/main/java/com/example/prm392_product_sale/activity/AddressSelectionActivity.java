// AddressSelectionActivity.java
package com.example.prm392_product_sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.AddressAdapter;
import com.example.prm392_product_sale.model.ApiResponse;
import com.example.prm392_product_sale.model.Province;
import com.example.prm392_product_sale.network.ApiService;
import com.example.prm392_product_sale.network.RetrofitClientProvince;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressSelectionActivity extends AppCompatActivity {
    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        rvAddresses = findViewById(R.id.rv_addresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));

        String apiType = getIntent().getStringExtra("API_TYPE");
        if (apiType.equals("TINHTHANH")) {
            fetchAddresses();
        } else if (apiType.equals("QUANHUYEN")) {
            fetchQuanHuyen(getIntent().getStringExtra("TINHTHANH_ID"));
        } else if (apiType.equals("PHUONGXA")) {
            fetchPhuongXa(getIntent().getStringExtra("QUANHUYEN_ID"));
        }
    }

    private void fetchAddresses() {
        ApiService apiService = RetrofitClientProvince.getClient().create(ApiService.class);
        apiService.getTinhThanh().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Province> provinces = response.body().getData();
                    addressAdapter = new AddressAdapter(provinces, address -> {
                        onAddressSelected(address);
                    });
                    rvAddresses.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressSelectionActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddressSelectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchQuanHuyen(String tinhThanhId) {
        ApiService apiService = RetrofitClientProvince.getClient().create(ApiService.class);
        apiService.getQuanHuyen(tinhThanhId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Province> provinces = response.body().getData();
                    addressAdapter = new AddressAdapter(provinces, address -> {
                        onAddressSelected(address);
                    });
                    rvAddresses.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressSelectionActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddressSelectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPhuongXa(String quanHuyenId) {
        ApiService apiService = RetrofitClientProvince.getClient().create(ApiService.class);
        apiService.getPhuongXa(quanHuyenId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Province> provinces = response.body().getData();
                    addressAdapter = new AddressAdapter(provinces, address -> {
                        onAddressSelected(address);
                    });
                    rvAddresses.setAdapter(addressAdapter);
                } else {
                    Toast.makeText(AddressSelectionActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddressSelectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAddressSelected(Province address) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_address", address.getFull_name_en());
        resultIntent.putExtra("selected_address_id", address.getId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}