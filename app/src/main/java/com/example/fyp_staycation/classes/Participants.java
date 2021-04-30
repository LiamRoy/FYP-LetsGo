package com.example.fyp_staycation.classes;

public class Participants {

    private String uid;
    private String userEmail;
    private String username;
    private String image;

    public Participants() {
    }

    public Participants(String username){
        this.username=username;
    }

    public Participants(String uid, String userEmail, String username,String image) {
        this.uid = uid;
        this.userEmail = userEmail;
        this.username=username;
        this.image=image;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
