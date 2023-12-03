package com.example.project.raider;

public class UserDetails {
    private String name;
    private String address;
    private String phone;

    public UserDetails(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    // Create getters for name, address, and phone
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}

