package com.example.prm392_product_sale.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.Conversation;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import javax.annotation.Nonnull;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<Conversation> conversations;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(String userId);
    }

    public ConversationAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @Nonnull
    @Override
    public ConversationViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation, listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();

    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private  TextView latestMessage;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_message_username);
            latestMessage = itemView.findViewById(R.id.tv_latest_message);
        }

        public void bind(Conversation conversation, OnConversationClickListener listener) {
            userName.setText(conversation.getUserName());
            latestMessage.setText(conversation.getLatestMessage());
            itemView.setOnClickListener(v -> listener.onConversationClick(conversation.getUserId()));
        }
    }
}