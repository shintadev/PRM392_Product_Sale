package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessage> chatMessages;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.item_chat_message, parent, false);
        }

        ChatMessage message = chatMessages.get(position);

        TextView messageUser = itemView.findViewById(R.id.tv_message_user);
        TextView messageText = itemView.findViewById(R.id.tv_message_text);
        TextView messageTime = itemView.findViewById(R.id.tv_message_time);
        ;

        messageUser.setText(message.getSenderName());
        messageText.setText(message.getMessage());
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimestamp()));

        return itemView;
    }
}
