package com.example.nilay.giftorganizer.Objects;

import java.util.Date;

public class Gift implements Comparable<Gift> {
    String name;
    Double price;
    private Date date;
    private boolean bought;

    public Gift() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public int compareTo(Gift gift) {
        if(this.getDate().before(gift.getDate())) {
            return -1;
        }
        else if(this.getDate().equals(gift.getDate())) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
