package com.example.fyp_staycation.classes;

import com.google.firebase.auth.FirebaseUser;

public class Connections {

    String groupId;
    String time;
    String message;
    String username;
    String profileName;

    public Connections() {
    }

    public Connections(String groupId, String time,String message, String username,String profileName) {
        this.groupId = groupId;
        this.message=message;
        this.time=time;
        this.username = username;
        this.profileName = profileName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
