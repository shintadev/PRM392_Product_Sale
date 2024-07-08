package com.example.prm392_product_sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
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

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set up the custom toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Set up the bottom navigation view
        BottomNavigationView navView = binding.navView;

        // Configure the AppBarConfiguration with the top level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_notifications, R.id.navigation_profile)
                .build();

        // Find the NavController and set up navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        // Set up click listener for the chat ImageView
        ImageView imageChat = binding.chatImage;
        if (mAuth.getCurrentUser() == null) {
            imageChat.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        } else {
            checkUserRole();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void checkUserRole() {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "checkUserRole:success");
                        boolean isAdmin = Boolean.TRUE.equals(document.getBoolean("admin"));
                        if (isAdmin) {
                            binding.chatImage.setOnClickListener(view -> {
                                Intent intent = new Intent(MainActivity.this, AdminConversationListActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            binding.chatImage.setOnClickListener(view -> {
                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                startActivity(intent);
                            });
                        }
                    } else Log.w(TAG, "checkUserRole:failure", task.getException());
                });
    }

}