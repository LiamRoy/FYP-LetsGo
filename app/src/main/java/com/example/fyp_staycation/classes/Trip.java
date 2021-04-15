package com.example.fyp_staycation.classes;

public class Trip {

    private String tripTitle;
    private String date;
    private String tid;
    private String createdBy;
    private String participants;

    public Trip() {
    }

    public Trip(String tripTitle, String date, String tid, String createdBy, String participants) {
        this.tripTitle = tripTitle;
        this.date = date;
        this.tid = tid;
        this.createdBy = createdBy;
        this.participants = participants;
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
}
