package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import javax.annotation.Nonnull;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Context context;
    private List<Product> productList;

    public ProductListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Nonnull
    @Override
    public ProductListViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_products_list, parent, false);
        return new ProductListAdapter.ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull ProductListAdapter.ProductListViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText("$" + product.getPrice());
        holder.tvOldPrice.setText("$" + product.getOldPrice());
        holder.tvOldPrice.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Glide.with(context)
                .load(product.getUrl())
                .into(holder.ivProduct);
    }

    private void removeProduct(Product product) {
        db.collection("products").document(String.valueOf(product.getId())).delete();
        storage.getReferenceFromUrl(product.getUrl()).delete();

        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvTitle, tvPrice,tvOldPrice;

        public ProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvOldPrice = itemView.findViewById(R.id.tv_old_price);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}