package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import javax.annotation.Nonnull;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Context context;
    private List<Product> productList;

    public AdminAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Nonnull
    @Override
    public AdminViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_products_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull AdminViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductTitle.setText(product.getTitle());
        holder.tvPrice.setText("$" + String.format("%.2f", product.getPrice()));

        Picasso.get().load(product.getUrl()).into(holder.ivProduct);

        holder.btnProductDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("products").document(String.valueOf(product.getId()))
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        productList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, productList.size());
                        removeProduct(product);
                    });
        });
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

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductTitle;
        TextView tvPrice;
        Button btnProductDelete;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product_admin);
            tvProductTitle = itemView.findViewById(R.id.tv_product_title_admin);
            tvPrice = itemView.findViewById(R.id.tv_price_admin);
            btnProductDelete = itemView.findViewById(R.id.btn_product_delete);
        }
    }
}