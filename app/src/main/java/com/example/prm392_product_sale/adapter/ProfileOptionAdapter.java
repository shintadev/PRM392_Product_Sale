package com.example.prm392_product_sale.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.activity.MapActivity;

import java.util.List;

public class ProfileOptionAdapter extends RecyclerView.Adapter<ProfileOptionAdapter.ViewHolder> {
    private List<String> options;

    public ProfileOptionAdapter(List<String> options) {
        this.options = options;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = options.get(position);

        switch (option) {
            case "Map":
                holder.optionIcon.setImageResource(R.drawable.maps);
                holder.optionText.setText("Map");
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), MapActivity.class);
                    v.getContext().startActivity(intent);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView optionIcon;
        public TextView optionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            optionIcon = itemView.findViewById(R.id.iv_option_icon);
            optionText = itemView.findViewById(R.id.tv_option_text);
        }
    }
}
