package com.example.prm392_product_sale.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.ChatAdapter;
import com.example.prm392_product_sale.databinding.ActivityChatBinding;
import com.example.prm392_product_sale.model.ChatMessage;
import com.example.prm392_product_sale.model.Conversation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListView listView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private EditText messageInput;
    private FloatingActionButton sendFab;

    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;
    ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listView = binding.lvMessageList;
        messageInput = binding.etMessageInput;
        sendFab = binding.fabSend;

        setSenderInfo();

        receiverId = getString(R.string.default_receiverId);
        receiverName = getString(R.string.default_receiverName);

        if (getIntent().getStringExtra("receiverId") != null) {
            receiverId = getIntent().getStringExtra("receiverId");

            DocumentReference docRef = db.collection("users").document(receiverId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "getUser:success");
                    receiverName = document.getString("displayName");
                } else {
                    Log.w(TAG, "Error getting documents: ", task.getException());
                    finish();
                }
            });
        }

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chats");
        }

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);

        listView.setAdapter(chatAdapter);

        loadMessages();

        sendFab.setOnClickListener(view -> sendChatMessage());
    }

    private void setSenderInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            senderId = currentUser.getUid();
        }

        db.collection("users").document(senderId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            senderName = document.getString("displayName");
                        }
                    }
                });
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
            messagesRef
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            ChatMessage message = dc.getDocument().toObject(ChatMessage.class);
                            if ((message.getSenderId().equals(currentUser.getUid())
                                    && message.getReceiverId().equals(receiverId))
                                    || (message.getReceiverId().equals(currentUser.getUid())
                                    && message.getSenderId().equals(receiverId))) {
                                chatMessages.add(message);
                                chatAdapter.notifyDataSetChanged();
                                listView.smoothScrollToPosition(chatMessages.size() - 1);
                            }
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

    private void sendChatMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                long timestamp = System.currentTimeMillis();
                String conversationId;

                ChatMessage chatMessage = new ChatMessage(senderId, senderName, receiverId, receiverName, messageText, timestamp);

                if (!senderName.equals("ADMIN")) {
                    conversationId = senderId;
                } else {
                    conversationId = receiverId;
                }

                db.collection("conversations").document(conversationId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    db.collection("conversations").document(conversationId)
                                            .update("latestMessage", chatMessage.getMessage());
                                } else {
                                    db.collection("conversations").document(conversationId)
                                            .set(new Conversation(conversationId, senderName, chatMessage.getMessage()));
                                }
                            } else {
                                Log.w(TAG, "Error getting documents: ", task.getException());
                            }
                        });

                db.collection("messages").add(chatMessage)
                        .addOnSuccessListener(documentReference -> {
                            messageInput.setText("");
                            Log.d(TAG, "Message sent successfully");
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error sending message", e));
            }

        }
    }
}