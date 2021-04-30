package com.example.fyp_staycation.classes;

import com.google.android.gms.maps.model.LatLng;

public class Trip {

    private String tripTitle;
    private String date;
    private String meettime;
    private String tid;
    private String createdBy;
    private String participants;
    private double lat;
    private double lng;
    private String address;

    public Trip() {
    }

    public Trip(String tripTitle,String date, String createdBy,String meettime,String tid){
        this.tripTitle=tripTitle;
        this.date=date;
        this.createdBy=createdBy;
        this.tid=tid;
        this.meettime=meettime;
    }

    public Trip(String tripTitle, String date,String meettime, String tid, String createdBy, String participants, double lat, double lng, String address) {
        this.tripTitle = tripTitle;
        this.date = date;
        this.meettime=meettime;
        this.tid = tid;
        this.createdBy = createdBy;
        this.participants = participants;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public String getTripTitle() {
        return tripTitle;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeettime() {
        return meettime;
    }

    public void setMeettime(String meettime) {
        this.meettime = meettime;
    }
}
