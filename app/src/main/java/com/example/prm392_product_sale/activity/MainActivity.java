package com.example.prm392_product_sale.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.databinding.ActivityMainBinding;
import com.example.prm392_product_sale.model.CartManager;
import com.example.prm392_product_sale.service.NotificationService;
import com.example.prm392_product_sale.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnDataPass {

    private static final String TAG = "MainActivity";
    ImageView imageChat;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUid;
    private String currentUsername;
    private boolean isAdmin;
    private CartManager cartManager;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Use view binding to inflate the layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        imageChat = binding.chatImage;
        currentUid = null;
        currentUsername = null;
        isAdmin = false;

        cartManager = new CartManager(null, this);

        if (mAuth.getCurrentUser() != null) {
            loadCurrentUser();
        } else {
            imageChat.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set up the custom toolbar
        Toolbar toolbar = binding.mainToolbar;
        setSupportActionBar(toolbar);

        // Set up the bottom navigation view
        BottomNavigationView navView = binding.navView;
        navView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.grey_dark));
        navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.grey_dark));

        // Configure the AppBarConfiguration with the top level destinations
        AppBarConfiguration appBarConfiguration
                = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_cart,
                R.id.navigation_profile).build();

        // Find the NavController and set up navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        if (currentUid != null) {
            updateCartNotification(this);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDataPass() {
        // Refresh the activity
        recreate();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
//        navigateToCartFragment();
    }

    private void navigateToCartFragment() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_cart);
    }

    private void loadCurrentUser() {
        if (mAuth.getCurrentUser() == null) return;
        currentUid = mAuth.getCurrentUser().getUid();
        cartManager = new CartManager(currentUid, this);

        db.collection("users")
                .document(currentUid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "loadCurrentUser:success");
                        currentUsername = document.getString("displayName");
                        isAdmin = Boolean.TRUE.equals(document.getBoolean("admin"));
                        if (isAdmin) {
                            binding.chatNotificationImage.setVisibility(View.INVISIBLE);
                            binding.chatImage.setImageResource(R.drawable.ic_person_black_24dp);
                            binding.chatImage.setOnClickListener(view -> {
                                Intent intent = new Intent(this, AdminActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            binding.chatImage.setOnClickListener(view -> {
                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                intent.putExtra("senderId", currentUid);
                                intent.putExtra("senderName", currentUsername);
                                intent.putExtra("isSenderAdmin", isAdmin);
                                startActivity(intent);
                            });
                        }
                        updateCartNotification(this);
                    } else {
                        Log.w(TAG, "loadCurrentUser:failure", task.getException());
                    }
                });
    }

    private void updateCartNotification(Context context) {
        CartManager.FirestoreCallback callback = new CartManager.FirestoreCallback() {
            @Override
            public void onBooleanCallback(boolean exists) {
                // Handle boolean callback
            }

            @Override
            public void onIntCallback(int count) {
                Intent serviceIntent = new Intent(context, NotificationService.class);
                serviceIntent.putExtra("cartItemCount", count);
                context.startService(serviceIntent);
            }

            @Override
            public void onFloatCallback(float totalPrice) {
                // Handle float callback
            }
        };

        cartManager.getCartItemCount(callback);
    }
}
