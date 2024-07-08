package com.example.prm392_product_sale.model;

public class User {
    private String id;
    private String email;
    private String displayName;
    private boolean isAdmin;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String displayName, boolean isAdmin){
        this.email = email;
        this.displayName = displayName;
        this.isAdmin = isAdmin;
    }

    public User(String id, String email, String displayName, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
