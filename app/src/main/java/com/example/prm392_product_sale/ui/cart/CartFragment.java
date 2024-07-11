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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.activity.BillingActivity;
import com.example.prm392_product_sale.adapter.CartAdapter;
import com.example.prm392_product_sale.databinding.FragmentCartBinding;
import com.example.prm392_product_sale.model.CartItem;
import com.example.prm392_product_sale.model.CartManager;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    TextView textViewTotalPrice;
    private FragmentCartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadUI();
        loadCartItems();

        return root;
    }

    public void loadUI() {
        CartViewModel cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        final Button button = binding.btnCheckout;
        button.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), BillingActivity.class);
            intent.putExtra("cart", cartViewModel.getText().getValue());
            startActivity(intent);
        });
    }

    public void loadCartItems() {
        RecyclerView recyclerView = binding.rvCart;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<CartItem> options = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(options);
        recyclerView.setAdapter(adapter);

        textViewTotalPrice = binding.tvTotal;
        updateTotalPrice();

//        if( adapter.getItemCount()<1){
//            TextView textView = binding.tvEmptyCart;
//            cartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        }else {
//            TextView textView = binding.tvEmptyCart;
//            cartViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        }



    }

    private void updateTotalPrice() {
        float total = CartManager.getInstance().getTotalPrice();
        textViewTotalPrice.setText("Total: " + String.format("%.2f", total)+"$");
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