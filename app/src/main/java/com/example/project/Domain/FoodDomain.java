package com.example.project.Domain;

import java.io.Serializable;

public class FoodDomain implements Serializable {
    private String type;
    private Double rating;
    private int time;
    private String resID;
    private String title;
    private String pic;
    private String description;
    private Double fee;
    private int numberInCart;


    public FoodDomain() {
    }

    public FoodDomain(String title, String pic, String description, Double fee, int numberInCart, String type, Double rating, int time, String resID) {
        this.title = title;
        this.pic = pic;
        this.description = description;
        this.type=type;
        this.fee = fee;
        this.numberInCart = numberInCart;
        this.rating=rating;
        this.time=time;
        this.resID=resID;
    }

    public FoodDomain(String title, Double fee){
        this.title=title;
        this.fee=fee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getResID() {
        return resID;
    }

    public void setResID(String resID) {
        this.resID = resID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }


}
