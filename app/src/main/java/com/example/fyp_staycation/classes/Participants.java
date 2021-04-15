package com.example.fyp_staycation.classes;

public class Participants {

    private String uid;
    private String userEmail;

    public Participants() {
    }

    public Participants(String uid, String userEmail) {
        this.uid = uid;
        this.userEmail = userEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
