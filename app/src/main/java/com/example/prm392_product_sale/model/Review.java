package com.example.prm392_product_sale.model;

public class Review {
    private int id;
    private String title;
    private String content;
    private int rating;
    private String date;
    private UserInReview userInReview;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Review(int id, String title, String content, int rating, String date, UserInReview userInReview) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.date = date;
        this.userInReview = userInReview;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserInReview getUserInReview() {
        return userInReview;
    }

    public void setUserInReview(UserInReview userInReview) {
        this.userInReview = userInReview;
    }
}
