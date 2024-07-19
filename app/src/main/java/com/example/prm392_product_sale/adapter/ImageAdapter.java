package com.example.prm392_product_sale.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private Context context;

    public ImageAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_order, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Picasso.get().load(imageUrls.get(position)).into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProductImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
        }
    }
}
