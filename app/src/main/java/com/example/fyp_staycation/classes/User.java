package com.example.fyp_staycation.classes;

public class User {

    private String username;
    private String email;
    private String password;
    private String description;
    private String phoneNum;
    private String uid;
    private String image;

    public User(){
    }

    public User(String username, String email, String password, String description, String phoneNum, String uid, String image) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.description = description;
        this.phoneNum = phoneNum;
        this.uid = uid;
        this.image=image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
