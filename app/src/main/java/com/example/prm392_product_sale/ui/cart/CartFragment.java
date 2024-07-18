package com.example.prm392_product_sale.ui.cart;

import static android.app.Activity.RESULT_OK;
import static com.example.prm392_product_sale.activity.BillingActivity.PAYPAL_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.activity.BillingActivity;
import com.example.prm392_product_sale.activity.LoginActivity;
import com.example.prm392_product_sale.adapter.CartAdapter;
import com.example.prm392_product_sale.databinding.FragmentCartBinding;
import com.example.prm392_product_sale.model.CartItem;
import com.example.prm392_product_sale.model.CartManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {

    FirebaseAuth mAuth;
    RecyclerView rvCart;
    TextView tvTotalPrice;
    private String TAG = "CartFragment";
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private CartManager cartManager;
    private AppCompatButton btnCheckout;
    private FragmentCartBinding binding;
    private ImageView btnBack;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (getActivity() != null) {
            cartManager = new CartManager(mAuth.getCurrentUser().getUid(), getActivity());
            loadCartItems();
        }

        rvCart = binding.rvCart;
        btnCheckout = binding.btnCheckout;
        tvTotalPrice = binding.tvTotal;
        btnBack = binding.backBtn;

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, getContext(), this);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(cartAdapter);


        btnCheckout.setOnClickListener(view -> {
            if (!cartItemList.isEmpty()) {
            Intent intent = new Intent(getActivity(), BillingActivity.class);
            intent.putExtra("userId", mAuth.getCurrentUser().getUid());
            intent.putExtra("cartItemList", (Serializable) cartItemList);
            intent.putExtra("totalPrice", tvTotalPrice.getText().toString().substring(7, tvTotalPrice.length() - 1));
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            } else {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onCartUpdated() {
        loadCartItems();
    }

    public void loadCartItems() {
        cartManager.getCartItems(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    cartItemList.clear();
                    cartAdapter.notifyDataSetChanged();
                    binding.tvEmptyCart.setText(("The Cart is Empty"));
                    binding.tvEmptyCart.setVisibility(View.VISIBLE);
                    tvTotalPrice.setText(("Total: 0$"));
                } else {
                    cartItemList.clear();
                    float total = 0.0f;
                    for (DocumentSnapshot document : task.getResult()) {
                        CartItem cartItem = document.toObject(CartItem.class);
                        if (cartItem != null) {
                            cartItemList.add(cartItem);
                            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    tvTotalPrice.setText(String.format("Total: %.2f$", total));
                }
                binding.pbCart.setVisibility(View.INVISIBLE);
            } else {
                Log.e(TAG, "loadCartItems:failed", task.getException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Payment was successful, update the database
                if (data != null) {
                    String paymentId = data.getStringExtra("paymentId");
                    String paymentState = data.getStringExtra("paymentState");

                    if ("approved".equals(paymentState)) {
                        // Update the database to mark items as purchased
                        updateDatabaseForSuccessfulPayment();
                        Toast.makeText(getContext(), "Payment Successful! Payment ID: " + paymentId, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Payment failed: " + paymentState, Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "Payment Cancelled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDatabaseForSuccessfulPayment() {
        cartManager.clearCart(this::onCartUpdated);
    }
}