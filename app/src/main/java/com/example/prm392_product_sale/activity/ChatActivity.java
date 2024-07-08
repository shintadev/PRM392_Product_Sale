package com.example.prm392_product_sale.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.ChatAdapter;
import com.example.prm392_product_sale.databinding.ActivityChatBinding;
import com.example.prm392_product_sale.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chats");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = binding.rvChat;

        chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("Tao", "Hello, how are you?", System.currentTimeMillis()));
        chatMessages.add(new ChatMessage("May", "I'm good, thanks for asking!", System.currentTimeMillis()));
        chatMessages.add(new ChatMessage("Tao", "I'm doing well, thank you!", System.currentTimeMillis()));
        chatMessages.add(new ChatMessage("May", "Glad to hear that!", System.currentTimeMillis()));
        chatMessages.add(new ChatMessage("Tao", "Glad to hear that!", System.currentTimeMillis()));
        chatMessages.add(new ChatMessage("May", "Glad to hear that!", System.currentTimeMillis()));
        chatMessages.add(new ChatMessage("Tao", "Glad to hear that!", System.currentTimeMillis()));

        chatAdapter = new ChatAdapter(chatMessages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        loadMessages();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessages() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            CollectionReference messagesRef = db.collection("messages");
            messagesRef.addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            ChatMessage message = dc.getDocument().toObject(ChatMessage.class);
                            chatMessages.add(message);
                            chatAdapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            // Handle modified messages
                            break;
                        case REMOVED:
                            // Handle removed messages
                            break;
                    }
                }
            });
        }
    }
}