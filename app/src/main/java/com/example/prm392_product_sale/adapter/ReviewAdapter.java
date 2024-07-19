package com.example.prm392_product_sale.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.Review;
import com.example.prm392_product_sale.ui.ReviewViewHolder;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewContentTxt.setText(review.getContent());
        holder.reviewRatingTxt.setText(String.valueOf(review.getRating()));
        Glide.with(holder.itemView.getContext())
                .load(review.getUserInReview().getAvatar())
                .circleCrop()
                .into(holder.reviewAvatar);
        holder.userNameTxt.setText(review.getUserInReview().getName());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

}