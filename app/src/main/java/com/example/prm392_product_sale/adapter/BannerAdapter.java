package com.example.prm392_product_sale.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.prm392_product_sale.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder>{

    private List<String> bannerUrls;

    public BannerAdapter(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String url = bannerUrls.get(position);
        Glide.with(holder.itemView)
                .load(url)
                .into(holder.bannerImageView);
    }

    @Override
    public int getItemCount() {
        return bannerUrls.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.imageSlide);
        }
    }
    public void setBannerUrls(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
    }
}
