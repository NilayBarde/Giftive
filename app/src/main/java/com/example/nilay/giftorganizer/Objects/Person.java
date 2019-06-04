package com.example.nilay.giftorganizer.Objects;

import java.util.Date;

public class Person implements Comparable<Person> {
    private String name;
    private double budget;
    private String birthday;
    private double bought;
    private Date date;
    private String occasion;
    private boolean reoccurring;

    public Person() {
        this.bought = 0;
        this.birthday = "0";

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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public double getBought() {
        return bought;
    }

    public void setBought(double bought) {
        this.bought = bought;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public boolean isReoccurring() {
        return reoccurring;
    }

    public void setReoccurring(boolean reoccurring) {
        this.reoccurring = reoccurring;
    }

    public int compareTo(Person person) {
       if(this.getDate().before(person.getDate())) {
           return -1;
       }
       else if(this.getDate().equals(person.getDate())) {
           return 0;
       }
       else {
           return 1;
       }
    }
}
