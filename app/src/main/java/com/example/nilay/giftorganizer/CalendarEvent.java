package com.example.nilay.giftorganizer;

public class CalendarEvent {
    private String name;
    private String eventName;
    private String date;
    private boolean reoccurring;

    public CalendarEvent() {
        this.name = name;
        this.eventName = eventName;
        this.date = date;
        this.reoccurring = reoccurring;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isReoccurring() {
        return reoccurring;
    }

    public void setReoccurring(boolean reoccurring) {
        this.reoccurring = reoccurring;
    }
}
