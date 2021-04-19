package com.example.fyp_staycation.classes;

import com.google.android.gms.maps.model.LatLng;

public class Trip {

    private String tripTitle;
    private String date;
    private String tid;
    private String createdBy;
    private String participants;
    private LatLng latlng;

    public Trip() {
    }

    public Trip(String tripTitle, String date, String tid, String createdBy, String participants, LatLng latLng) {
        this.tripTitle = tripTitle;
        this.date = date;
        this.tid = tid;
        this.createdBy = createdBy;
        this.participants = participants;
        this.latlng = latLng;
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

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
