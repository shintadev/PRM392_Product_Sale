package com.example.prm392_product_sale.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String id;
    private String userId;
    private String address;
    private float totalPrice;
    private int deliveryDate;
    private String status;
    private List<OrderItem> orderItems;


    public Order() {
    }

    public Order(String id, String userId, String address, float totalPrice, String status, List<OrderItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderItems = orderItems;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(int deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (OrderItem item : orderItems) {
            subtotal += item.getTotalPrice() * item.getQuantity();
        }
        return subtotal;
    }

    public double getTax() {
        return getSubtotal() * 0.1;
    }
}
