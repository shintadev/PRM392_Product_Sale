package com.example.prm392_product_sale.model;

import androidx.annotation.NonNull;

public class OrderItem extends CartItem{

    public OrderItem(Product product, int quantity) {
        super(product, quantity);
    }

    public double getTotal() {
        return getProduct().getPrice() * getQuantity();
    }

    @NonNull
    public String toString() {
        return getProduct().getTitle() + " x" + getQuantity() + " = $" + getTotal();
    }
}
