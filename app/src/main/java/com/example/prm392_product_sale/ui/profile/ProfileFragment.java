package com.example.prm392_product_sale.ui.profile;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.activity.AdminConversationListActivity;
import com.example.prm392_product_sale.activity.LoginActivity;
import com.example.prm392_product_sale.activity.MainActivity;
import com.example.prm392_product_sale.adapter.ProfileOptionAdapter;
import com.example.prm392_product_sale.databinding.FragmentProfileBinding;
import com.example.prm392_product_sale.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore firestore;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this.getContext(), gso);
        firestore = FirebaseFirestore.getInstance();

        binding.btnLogin.setOnClickListener(v -> signIn());
        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            loadProfile(null);
        });

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
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            checkUserRole(currentUser.getUid());
//        }
        loadProfile(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText( requireActivity(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                }});
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void checkUserRole(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "checkUserRole:success");
                        if (document.exists()) {
                            boolean isAdmin = Boolean.TRUE.equals(document.getBoolean("admin"));
                            if (isAdmin) {
                                startActivity(new Intent(this.getContext(), AdminConversationListActivity.class));
                            } else {
                                Intent chatIntent = new Intent(this.getContext(), MainActivity.class);
                                startActivity(chatIntent);
                            }
                            getActivity().finish();
                        } else {
                            createUser(userId);
                        }
                    }
                });
    }

    private void createUser(String userId) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this.getContext());
        if (account != null) {
            String email = account.getEmail();
            String displayName = account.getDisplayName();

            User user = new User(email, displayName, false);
            firestore.collection("users").document(userId).set(user)
                    .addOnSuccessListener(aVoid -> checkUserRole(userId))
                    .addOnFailureListener(e -> Log.w("LoginActivity", "Error adding document", e));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadProfile(FirebaseUser currentUser) {
        if (currentUser == null) {
            binding.llBeforeLogin.setVisibility(View.VISIBLE);
            binding.llAfterLogin.setVisibility(View.GONE);

        } else {
            binding.llBeforeLogin.setVisibility(View.GONE);
            binding.llAfterLogin.setVisibility(View.VISIBLE);

            ImageView profileImage = binding.ivProfileImage;
            TextView name = binding.tvProfileName;

            profileImage.setImageURI(currentUser.getPhotoUrl());
            name.setText(currentUser.getDisplayName());


        }
    }
}
