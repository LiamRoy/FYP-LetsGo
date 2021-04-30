package com.example.fyp_staycation.classes;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Connections {

    String groupId;
    String ctitle;
    String messages;
    String time;
    String message;
    String username;
    String profileName;
    String members;
    String image;

    public Connections() {
    }

    public Connections(String groupId, String messages, String members) {
        this.groupId = groupId;
        this.messages = messages;
        this.members = members;
    }

    /*public Connections(String groupId, String ctitle, Messages messages, String time, String message, String username, String profileName) {
        this.groupId = groupId;
        this.ctitle=ctitle;
        this.messages=messages;
        this.message=message;
        this.time=time;
        this.username = username;
        this.profileName = profileName;
    }*/

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getGroupId() {
        return groupId;
    }


    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
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