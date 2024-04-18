package com.example.fixeridetest.REPORT;

// Location.java
public class Location {
    private LocationData from;

    public Location() {
        // Default constructor required for DataSnapshot.getValue(Location.class)
    }

    public LocationData getFrom() {
        return from;
    }

    public void setFrom(LocationData from) {
        this.from = from;
    }
}
