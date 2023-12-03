package com.example.project.Admin;

public class Restaurant {
    private String restaurantName;
    private String userId;
    private String imageUrl;
    private double Lat;
    private double Long;
    private String description;

    // Default constructor (required for Firebase)
    public Restaurant() {
    }

    // Constructor with parameters
    public Restaurant(String restaurantName, String userId, String imageUrl, double Lat, double Long, String description) {
        this.restaurantName = restaurantName;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.Lat = Lat;
        this.Long = Long;
        this.description = description;
    }

    // Getters and setters (required for Firebase)

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


