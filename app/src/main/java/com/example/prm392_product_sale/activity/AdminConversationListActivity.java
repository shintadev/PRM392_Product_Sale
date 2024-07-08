package com.example.prm392_product_sale.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

    private RecyclerView recyclerViewConversations;
    private ConversationAdapter conversationAdapter;
    private List<Conversation> conversations = new ArrayList<>();

    private FirebaseFirestore firestore;
    private CollectionReference conversationCollection;
    ActivityAdminConversationListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminConversationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerViewConversations = findViewById(R.id.rv_conversation);
        firestore = FirebaseFirestore.getInstance();
        conversationCollection = firestore.collection("conversations");

        setupRecyclerView();

        loadConversations();
    }

    private void setupRecyclerView() {
        conversationAdapter = new ConversationAdapter(conversations, this);
        recyclerViewConversations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewConversations.setAdapter(conversationAdapter);
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
    public void onConversationClick(String userId) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("receiverId", userId);
        startActivity(chatIntent);
    }
}
