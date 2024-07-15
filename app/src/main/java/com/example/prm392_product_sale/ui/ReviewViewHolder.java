package com.example.prm392_product_sale.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_product_sale.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    public TextView reviewContentTxt;
    public TextView reviewRatingTxt;
    public TextView userNameTxt;
    public ImageView reviewAvatar;

    public   ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        reviewContentTxt = itemView.findViewById(R.id.reviewContentTxt);
        reviewRatingTxt = itemView.findViewById(R.id.reviewRatingTxt);
        reviewAvatar = itemView.findViewById(R.id.imageReview);
        userNameTxt = itemView.findViewById(R.id.userNameTxt);
    }
}
