package com.example.prm392_product_sale.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm392_product_sale.activity.BillingActivity;
import com.example.prm392_product_sale.databinding.FragmentCartBinding;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CartViewModel cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.tvEmptyCart;
        cartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final Button button = binding.btnCheckout;
        button.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), BillingActivity.class);
            intent.putExtra("cart", cartViewModel.getText().getValue());
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}