package com.example.prm392_product_sale.model;

public class Conversation {
    private String userId;
    private String userName;
    private String latestMessage;

    public Conversation() {
        // Required for Firestore
    }

    public Conversation(String userId, String userName, String latestMessage) {
        this.userId = userId;
        this.userName = userName;
        this.latestMessage = latestMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }
}
