package com.example.prm392_product_sale.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.activity.LoginActivity;
import com.example.prm392_product_sale.adapter.ProfileOptionAdapter;
import com.example.prm392_product_sale.databinding.FragmentProfileBinding;
import com.example.prm392_product_sale.ui.profile.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Button button = binding.btnLogin;
            button.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }else {
            binding.llBeforeLogin.setVisibility(View.GONE);
            binding.llAfterLogin.setVisibility(View.VISIBLE);

            ImageView profileImage = binding.ivProfileImage;
            TextView name = binding.tvProfileName;
            TextView email = binding.tvProfileEmail;

            profileImage.setImageURI(currentUser.getPhotoUrl());
            name.setText(currentUser.getDisplayName());
            email.setText(currentUser.getEmail());
        }



        RecyclerView recyclerView = binding.profileOptionsList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup adapter with dummy data
        List<String> options = new ArrayList<>();
        options.add("Map");

        ProfileOptionAdapter adapter = new ProfileOptionAdapter(options);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
