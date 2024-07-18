package com.example.prm392_product_sale.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.model.Province;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private final List<Province> addressList;
    private final OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onAddressClick(Province address);
    }

    public AddressAdapter(List<Province> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.prm392_product_sale.R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Province address = addressList.get(position);
        holder.bind(address, listener);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }

        public void bind(Province address, OnAddressClickListener listener) {
            tvAddress.setText(address.getFull_name_en());
            itemView.setOnClickListener(v -> listener.onAddressClick(address));
        }
    }
}