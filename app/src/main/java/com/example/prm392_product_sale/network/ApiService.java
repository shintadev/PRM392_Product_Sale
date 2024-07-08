package com.example.prm392_product_sale.network;

import com.example.prm392_product_sale.model.Product;
import com.example.prm392_product_sale.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/users")
    Call<List<User>> getUsers();

    @GET("api/products")
    Call<List<Product>> getProducts();
}
