package com.example.fyp_staycation.classes;

public class Messages {

    String message, sender,timestamp,type,image;

    public Messages() {
    }

    public Messages(String message, String sender, String timestamp, String type, String image) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
