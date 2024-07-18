package com.example.prm392_product_sale.adapter;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import javax.annotation.Nonnull;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private static final String TAG = "AdminAdapter";
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
        // Delete the product document from Firestore
        db.collection("products")
                .document(product.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete the product image from Firebase Storage
                    storage.getReferenceFromUrl(product.getUrl()).delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Product deleted from storage");

                                // Delete the product from all user carts
                                db.collection("users").get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    document.getReference().collection("cart")
                                                            .document(product.getId()).delete();
                                                }

                                                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                                Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error deleting product image from storage", e);
                                Toast.makeText(context, "Error deleting product image from storage", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting product from Firestore", e);
                    Toast.makeText(context, "Error deleting product from Firestore", Toast.LENGTH_SHORT).show();
                });
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