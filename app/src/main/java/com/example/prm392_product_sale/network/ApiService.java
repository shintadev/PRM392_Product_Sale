package com.example.prm392_product_sale.network;

import com.example.prm392_product_sale.model.ApiResponse;
import com.example.prm392_product_sale.model.Category;
import com.example.prm392_product_sale.model.Product;
import com.example.prm392_product_sale.model.Province;
import com.example.prm392_product_sale.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/users")
    Call<List<User>> getUsers();

    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("category")
    Call<List<Category>> getCategories();


    @GET("api-tinhthanh/1/0.htm")
    Call<ApiResponse> getTinhThanh();

    @GET("api-tinhthanh/2/{id}.htm")
    Call<ApiResponse> getQuanHuyen(@Path("id") String tinhThanhId);

    @GET("api-tinhthanh/3/{id}.htm")
    Call<ApiResponse> getPhuongXa(@Path("id") String quanHuyenId);

    @GET("api-tinhthanh/4/0.htm")
    Call<ApiResponse> getDiaChiDayDu();

    @GET("api-tinhthanh/5/{id}.htm")
    Call<ApiResponse> getTenDiaChi(@Path("id") String diaChiId);
}
