package com.example.prm392_product_sale.model;

public class Category {
    private String url;
    private String id;
    private String title;

    public Category(String url, String id, String title) {
        this.url = url;
        this.id = id;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
