package com.example.project.Adapter;

public class User {
    private String userId;
    private String displayName;
    private String email;

    private String status;



    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId, String displayName, String email, String status) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public User(String displayName, String email, String status) {
        this.displayName = displayName;
        this.email = email;
        this.status = status;
    }

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public User(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
