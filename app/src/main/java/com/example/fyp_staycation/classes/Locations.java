package com.example.fyp_staycation.classes;

import android.location.Location;

import java.util.List;

public class Locations {

    private String image, title, category, city, county, lid, date, time, description;

    private Connections connection;

    public Locations(){

    }

    static class Connections {


        private String user;
        private String title;
        private String city;
        private String county;
        private String date;
        private String reference;

        public Connections(){
        }

        public Connections(String user,String title, String city, String county, String date,String reference) {
            this.user=user;
            this.title = title;
            this.city = city;
            this.county = county;
            this.date = date;
            this.reference=reference;
        }
        public String getReference(){return reference;}

        public void setReference(String reference){this.reference=reference;}

        public String getUser(){
            return user;}
        public void setUser(String user){this.user=user;}

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public Locations(String image, String title, String category, String city, String county, String lid, String date, String time,String description, Connections connection) {

        this.image = image;
        this.title = title;
        this.category = category;
        this.city = city;
        this.county = county;
        this.lid = lid;
        this.date = date;
        this.time = time;
        this.description = description;
        this.connection = connection;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Connections getConnections(){return connection;}

    public void setConnections(Connections connections){this.connection=connection;}
}
