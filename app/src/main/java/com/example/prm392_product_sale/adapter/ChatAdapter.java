package com.example.prm392_product_sale.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.ChatMessage;

import java.util.List;

import javax.annotation.Nonnull;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Nonnull
    @Override
    public ChatViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView messageUser;
        TextView messageText;
        TextView messageTime;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageUser = itemView.findViewById(R.id.tv_message_user);
            messageText = itemView.findViewById(R.id.tv_message_text);
            messageTime = itemView.findViewById(R.id.tv_message_time);
        }

        void bind(ChatMessage message) {
            messageUser.setText(message.getSenderId());
            messageText.setText(message.getMessage());
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimestamp()));
        }
    }
}
