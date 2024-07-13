package com.example.prm392_product_sale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.ConversationAdapter;
import com.example.prm392_product_sale.databinding.ActivityAdminConversationListBinding;
import com.example.prm392_product_sale.model.Conversation;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminConversationListActivity extends AppCompatActivity implements ConversationAdapter.OnConversationClickListener {

    ActivityAdminConversationListBinding binding;
    private RecyclerView recyclerViewConversations;
    private ConversationAdapter conversationAdapter;
    private List<Conversation> conversations = new ArrayList<>();
    private FirebaseFirestore firestore;
    private CollectionReference conversationCollection;
    private String senderId;
    private String senderName;
    private boolean isSenderAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminConversationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerViewConversations = binding.rvConversation;
        firestore = FirebaseFirestore.getInstance();
        conversationCollection = firestore.collection("conversations");

        senderId = getIntent().getStringExtra("senderId");
        senderName = getIntent().getStringExtra("senderName");
        isSenderAdmin = getIntent().getBooleanExtra("isSenderAdmin", false);

        setupRecyclerView();

        loadConversations();
    }

    private void setupRecyclerView() {
        conversationAdapter = new ConversationAdapter(conversations, this);
        recyclerViewConversations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewConversations.setAdapter(conversationAdapter);

        Toolbar toolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chats");
        }
    }

    private void loadConversations() {
        conversationCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            if (queryDocumentSnapshots != null) {
                conversations.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Conversation conversation = documentSnapshot.toObject(Conversation.class);
                    conversations.add(conversation);
                }
                conversationAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onConversationClick(String userId, String userName) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("senderId", senderId);
        chatIntent.putExtra("senderName", senderName);
        chatIntent.putExtra("isAdmin", isSenderAdmin);
        chatIntent.putExtra("receiverId", userId);
        chatIntent.putExtra("receiverName", userName);
        startActivity(chatIntent);
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
}
