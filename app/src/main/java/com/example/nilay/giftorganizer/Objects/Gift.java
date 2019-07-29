package com.example.nilay.giftorganizer.Objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Gift implements Comparable<Gift> {
    String name;
    Double price;
    private boolean bought;
    private HashMap<String, Object> timestampCreated;

    public Gift() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;

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

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public int compareTo(Gift gift) {
        if(this.getTimestampCreatedLong() < gift.getTimestampCreatedLong()) {
            return -1;
        }
        else if(this.getTimestampCreatedLong() == gift.getTimestampCreatedLong()) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
