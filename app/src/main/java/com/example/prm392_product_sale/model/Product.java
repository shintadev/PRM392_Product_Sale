package com.example.prm392_product_sale.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String title;
    private String description;
    private String url;
    private float oldPrice;
    private float price;
    private String category;

    public Product() {
    }

    public Product(String title, String description, String url, float oldPrice, String category) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.oldPrice = oldPrice;
        this.price = (oldPrice * 0.9f);
        this.category = category;
    }

    public Product(String id, String title, String description, String url, float oldPrice, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.oldPrice = oldPrice;
        this.price = oldPrice * 0.9f;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(float oldPrice) {
        this.oldPrice = oldPrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

