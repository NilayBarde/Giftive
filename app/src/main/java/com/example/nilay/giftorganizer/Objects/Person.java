package com.example.nilay.giftorganizer.Objects;

public class Person implements Comparable<Person> {
    private String name;
    private double budget;
    private String date;
    private double bought;
    private String occasion;
    private boolean allGiftsBought;
    private Long timestamp;

    public Person() {
        this.bought = 0;
        this.date = "0";
        this.allGiftsBought = false;
        Long tsLong = System.currentTimeMillis()/1000;
        this.timestamp = tsLong;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBought() {
        return bought;
    }

    public void setBought(double bought) {
        this.bought = bought;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public boolean isAllGiftsBought() {
        return allGiftsBought;
    }

    public void setAllGiftsBought(boolean allGiftsBought) {
        this.allGiftsBought = allGiftsBought;
    }

    public int compareTo(Person person) {
        if(this.getTimestamp() < person.getTimestamp()) {
            return -1;
        }
        else if(this.getTimestamp() == person.getTimestamp()) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
