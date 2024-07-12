package com.example.prm392_product_sale.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {

    private  String TAG = "CartFragment";
    private static final String CHANNEL_ID = "cart_channel";
    private static final int NOTIFICATION_ID = 1;
    FirebaseAuth mAuth;
    RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private CartManager cartManager;
    TextView tvTotalPrice;
    private Button btnCheckout;
    private FragmentCartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CartViewModel cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        if (getActivity() != null) {
            cartManager = new CartManager(mAuth.getCurrentUser().getUid(), getActivity());
        }

        rvCart = binding.rvCart;
        btnCheckout = binding.btnCheckout;
        tvTotalPrice = binding.tvTotal;

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList,getContext(), this);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(cartAdapter);


        btnCheckout.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), BillingActivity.class);
            intent.putExtra("cartItemList", (Serializable) cartItemList);
            intent.putExtra("totalPrice", tvTotalPrice.getText().toString().substring(7, tvTotalPrice.length()-1));
            startActivity(intent);
        });

        loadCartItems();

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
            } else {
                Log.e(TAG, "loadCartItems:failed", task.getException());
            }
        });

//        if( adapter.getItemCount()<1){
//            TextView textView = binding.tvEmptyCart;
//            cartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        }else {
//            TextView textView = binding.tvEmptyCart;
//            cartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    public void updateCartBadge(int cartCount) {
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
//                .setContentTitle("Cart")
//                .setContentText(String.valueOf(cartCount))
//                .setSmallIcon(R.drawable.ic_cart_black_24dp)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
//        if (cartCount > 0) {
//            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
//        } else {
//            notificationManager.cancel(NOTIFICATION_ID);
//        }
//    }
}