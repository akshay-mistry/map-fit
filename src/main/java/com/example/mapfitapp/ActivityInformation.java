package com.example.mapfitapp;

import com.google.android.gms.maps.model.Marker;

public class ActivityInformation {

    String email;
    double latitude;
    double longitude;
    String user;
    Marker marker;
    String workoutType;
    String timestamp;

    public ActivityInformation(String e, double la, double lo, String u, Marker m, String w, String t) {
        email = e;
        latitude = la;
        longitude = lo;
        user = u;
        marker = m;
        workoutType = w;
        timestamp = t;
    }
    public ActivityInformation()
    {

    }

    public String getEmail()
    {
        return email;
    }
    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public String getUser()
    {
        return user;
    }


    public Marker getMarker() {
        return marker;
    }
    public void setMarker(Marker m) {
        marker = m;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public String getWorkoutType(){
        return workoutType;
    }

}