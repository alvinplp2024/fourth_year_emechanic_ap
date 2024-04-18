package com.example.fixeridetest.REPORT;

// LocationData.java
public class LocationData {
    private double lat;
    private double lng;

    public LocationData() {
        // Default constructor required for DataSnapshot.getValue(LocationData.class)
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

