//package com.example.prm392_product_sale.adapter;
//
//import android.content.Intent;
//import android.graphics.Paint;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.prm392_product_sale.R;
//import com.example.prm392_product_sale.activity.ProductDetailActivity;
//import com.example.prm392_product_sale.model.Product;
//
//import java.util.Collections;
//import java.util.List;
//
//public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.ViewHolder> {
//    private List<Product> options;
//
//    public PopularProductAdapter(@NonNull List<Product> options) {
//        Collections.shuffle(options);
//        if (options.size() > 10)
//            this.options = options.subList(0, 10);
//        else {
//            this.options = options;
//        }
//    }
//
//    @NonNull
//    @Override
//    public PopularProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_products, parent, false);
//        return new PopularProductAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PopularProductAdapter.ViewHolder holder, int position) {
//        Product product = options.get(position);
//        Glide.with(holder.itemView)
//                .load(product.getUrl())
//                .into(holder.productImage);
//        holder.title.setText(product.getTitle());
//        holder.priceTxt.setText(String.valueOf(product.getPrice()));
//        holder.oldPriceTxt.setText(String.valueOf(product.getOldPrice()));
//
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
//            intent.putExtra("productId", product.getId());
//            v.getContext().startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return options.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        public ImageView productImage;
//        public TextView title;
//        public TextView priceTxt;
//        public TextView oldPriceTxt;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            productImage = itemView.findViewById(R.id.iv_product);
//            title = itemView.findViewById(R.id.tv_title);
//            priceTxt = itemView.findViewById(R.id.tv_price);
//            oldPriceTxt = itemView.findViewById(R.id.tv_old_price);
//            oldPriceTxt.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        }
//    }
//}
