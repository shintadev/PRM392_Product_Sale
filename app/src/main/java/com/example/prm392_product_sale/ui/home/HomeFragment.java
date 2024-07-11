package com.example.prm392_product_sale.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.adapter.PopularProductAdapter;
import com.example.prm392_product_sale.adapter.ProductListAdapter;
import com.example.prm392_product_sale.databinding.FragmentHomeBinding;
import com.example.prm392_product_sale.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db;
    private List<Product> productList;
    private FragmentHomeBinding binding;
    private PopularProductAdapter popularProductAdapter;
    private ProductListAdapter productAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        RecyclerView rvPopularProducts = binding.rvPopularProductsList;
        rvPopularProducts.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        RecyclerView rvProductsList = binding.rvProductList;
        rvProductsList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize the product list
        productList = new ArrayList<>();

        // Load products from Firestore
        loadProducts();

        return root;
    }

    public void loadProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                try {
                                    Product product = new Product(
                                            document.getId(),
                                            document.getString("title"),
                                            document.getString("description"),
                                            document.getString("url"),
                                            document.getDouble("oldPrice").floatValue()
                                    );
                                    product.setPrice(document.getDouble("price").floatValue());
                                    productList.add(product);
                                } catch (Exception e) {
                                    Log.e(TAG, "loadProducts: " + e.getMessage());
                                }
                            }

                            if(binding != null){// After loading data, set up the adapters
                                popularProductAdapter = new PopularProductAdapter(productList);
                                binding.rvPopularProductsList.setAdapter(popularProductAdapter);

                                productAdapter = new ProductListAdapter(getContext(), productList);
                                binding.rvProductList.setAdapter(productAdapter);
                                binding.pbPopularProducts.setVisibility(View.GONE);
                                binding.pbProductList.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Log.w(TAG, "loadProducts:failed", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}