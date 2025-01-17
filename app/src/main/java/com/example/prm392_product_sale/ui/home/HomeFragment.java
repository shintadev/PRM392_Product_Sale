package com.example.prm392_product_sale.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.BannerAdapter;
import com.example.prm392_product_sale.adapter.CategoryAdapter;
import com.example.prm392_product_sale.adapter.ProductListAdapter;
import com.example.prm392_product_sale.databinding.FragmentHomeBinding;
import com.example.prm392_product_sale.model.Category;
import com.example.prm392_product_sale.model.Product;
import com.example.prm392_product_sale.network.ApiService;
import com.example.prm392_product_sale.network.RetrofitClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db;
    private List<Product> productList;
    private FragmentHomeBinding binding;
    private ProductListAdapter productAdapter;
    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        RecyclerView rvProductsList = binding.rvProductList;
        rvProductsList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this::filterProductsByCategory);
        binding.recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerCategories.setAdapter(categoryAdapter);

        // Initialize the product list
        productList = new ArrayList<>();
        loadBanners();
        loadCategories();
        return root;
    }

    private void filterProductsByCategory(Category category) {
        String categoryTitleLowerCase = category.getTitle().toLowerCase();
        db.collection("products")
                .whereEqualTo("category", categoryTitleLowerCase)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> filteredList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Product product = document.toObject(Product.class);
                            filteredList.add(product);
                        }
                        productAdapter.updateList(filteredList);
                    } else {
                        Log.w(TAG, "filterProductsByCategory:failed", task.getException());
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            SearchView searchView = getActivity().findViewById(R.id.sv_main_search);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<Product> filteredList = productList.stream()
                            .filter(product -> product.getTitle().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    if (!filteredList.isEmpty()) {
                        productAdapter.updateList(filteredList);
                    }
                    return true;
                }
            });
        }

        Spinner sortSpinner = binding.sortSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Sort by Name
                        Collections.sort(productList, (p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()));
                        break;
                    case 1: // Sort by Price (Low to High)
                        Collections.sort(productList, (p1, p2) -> Float.compare(p1.getPrice(), p2.getPrice()));
                        break;
                    case 2: // Sort by Price (High to Low)
                        Collections.sort(productList, (p1, p2) -> Float.compare(p2.getPrice(), p1.getPrice()));
                        break;
                }
                if (productAdapter != null) {
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        loadProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadProducts();
    }

    private void loadBanners() {
        List<String> bannerUrls = new ArrayList<>(Arrays.asList(
                "https://firebasestorage.googleapis.com/v0/b/project175-f1c13.appspot.com/o/banner1.png?alt=media&token=6cb7ba57-4cd5-434a-b668-b1c1c0237fb3",
                "https://firebasestorage.googleapis.com/v0/b/project175-f1c13.appspot.com/o/banner2.png?alt=media&token=2c2a98de-2240-4130-8a89-7fbe7e49fa21"
        ));
        bannerAdapter = new BannerAdapter(bannerUrls);
        ViewPager2 viewPager = binding.viewPager2;
        viewPager.setAdapter(bannerAdapter);
    }

    private void loadCategories() {
        ApiService categoryService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Category>> call = categoryService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    List<Category> categories = response.body();
                    if (categories != null && binding != null) {
                        binding.progressBarOfficial.setVisibility(View.INVISIBLE);
                        categoryAdapter.setCategories(categories);
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Categories are null");
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage());
            }
        });
    }

    public void loadProducts() {
        productList.clear();
        binding.pbProductList.setVisibility(View.VISIBLE);
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
                                            document.getDouble("oldPrice").floatValue(),
                                            document.getString("title")
                                    );
                                    product.setPrice(document.getDouble("price").floatValue());
                                    productList.add(product);
                                } catch (Exception e) {
                                    Log.e(TAG, "loadProducts: " + e.getMessage());
                                }
                            }

                            if (binding != null) {// After loading data, set up the adapters

                                productAdapter = new ProductListAdapter(getContext(), productList);
                                binding.rvProductList.setAdapter(productAdapter);
                                binding.pbProductList.setVisibility(View.INVISIBLE);
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