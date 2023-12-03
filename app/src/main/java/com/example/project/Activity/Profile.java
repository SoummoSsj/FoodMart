package com.example.project.Activity;

public class Profile {
    private String id;
    private String restaurantName;
    private String address;
    private String email;
    private String phone;
    private boolean setCurrentLocation;

    public Profile() {
        // Default constructor required for Firebase
    }

    public Profile(String id, String restaurantName, String address, String email, String phone, boolean setCurrentLocation) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.setCurrentLocation = setCurrentLocation;
    }

    public String getId() {
        return id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isSetCurrentLocation() {
        return setCurrentLocation;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSetCurrentLocation(boolean setCurrentLocation) {
        this.setCurrentLocation = setCurrentLocation;
    }
}

