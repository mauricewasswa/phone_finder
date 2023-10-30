package com.rengcorp.phone_finder;

public class UserLocation {
    private double latitude;
    private double longitude;
    private double timeStamp;
    private String name;

    public UserLocation() {
    }

    public UserLocation(double latitude, double longitude, double timeStamp, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
