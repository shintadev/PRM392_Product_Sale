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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    ActivityChatBinding binding;
    private FirebaseFirestore db;
    private ListView listView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText messageInput;
    private FloatingActionButton sendFab;
    private String senderId;
    private String senderName;
    private boolean isSenderAdmin;
    private String receiverId;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        listView = binding.lvMessageList;
        messageInput = binding.etMessageInput;
        sendFab = binding.fabSend;

        setSenderInfo();
        setReceiverInfo();

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (getIntent().getStringExtra("receiverName") != null)
                getSupportActionBar().setTitle(getIntent().getStringExtra("receiverName"));
            else
                getSupportActionBar().setTitle("Chat with Admin");
        }

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        listView.setAdapter(chatAdapter);

        loadMessages();

        sendFab.setOnClickListener(view -> sendChatMessage());
    }

    private void setSenderInfo() {
        senderId = getIntent().getStringExtra("senderId");
        senderName = getIntent().getStringExtra("senderName");
        isSenderAdmin = getIntent().getBooleanExtra("isSenderAdmin", true);
    }

    private void setReceiverInfo() {
        if (isSenderAdmin) {
            if (getIntent().getStringExtra("receiverId") != null) {
                receiverId = getIntent().getStringExtra("receiverId");

                db.collection("users")
                        .document(receiverId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d(TAG, "setReceiverInfo:success");
                                receiverName = document.getString("displayName");
                            } else {
                                Log.w(TAG, "setReceiverInfo:failure", task.getException());
                                finish();
                            }
                        });
            }
        } else {
            receiverId = getString(R.string.default_receiverId);
            receiverName = getString(R.string.default_receiverName);
        }
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
        db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                try {
                                    ChatMessage message = dc.getDocument().toObject(ChatMessage.class);
                                    if ((message.getSenderId().equals(senderId)
                                            && message.getReceiverId().equals(receiverId))
                                            || (message.getReceiverId().equals(senderId)
                                            && message.getSenderId().equals(receiverId))) {
                                        chatMessages.add(message);
                                        chatAdapter.notifyDataSetChanged();
                                        listView.smoothScrollToPosition(chatMessages.size() - 1);
                                    }
                                } catch (Exception es) {
                                    Log.e(TAG, "loadMessages:failure", es);
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

    private void sendChatMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            long timestamp = System.currentTimeMillis();
            String conversationId;

            ChatMessage chatMessage
                    = new ChatMessage(senderId,
                    senderName,
                    receiverId,
                    receiverName,
                    messageText,
                    timestamp);

            if (isSenderAdmin) {
                conversationId = receiverId;
            } else {
                conversationId = senderId;
            }

            db.collection("conversations")
                    .document(conversationId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                db.collection("conversations")
                                        .document(conversationId)
                                        .update("latestMessage", chatMessage.getMessage());
                            } else {
                                db.collection("conversations")
                                        .document(conversationId)
                                        .set(new Conversation(conversationId, senderName, chatMessage.getMessage()));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents: ", task.getException());
                        }
                    });

            db.collection("messages")
                    .add(chatMessage)
                    .addOnSuccessListener(documentReference -> {
                        messageInput.setText("");
                        Log.d(TAG, "sendChatMessage:success");
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "sendChatMessage:failure", e));
        }

    }
}
