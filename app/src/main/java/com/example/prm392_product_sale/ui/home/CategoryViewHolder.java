package com.example.prm392_product_sale.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_product_sale.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public ImageView pic;
    public TextView title;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        pic = itemView.findViewById(R.id.pic);
        title = itemView.findViewById(R.id.title);
    }
}
