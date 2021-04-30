package com.example.fyp_staycation.classes;

import android.location.Location;

import java.util.List;

public class Locations {

    private String image, title, category, city, county, lid, date, time, description,link;
    private double lat;
    private double lng;
    private Connections connection;

    public Locations(){

    }

    public Locations(String image, String title, String category, String city, String county, String lid, String date, String time, String description,String link, double lat, double lng, Connections connection) {
        this.image = image;
        this.title = title;
        this.category = category;
        this.city = city;
        this.county = county;
        this.lid = lid;
        this.date = date;
        this.time = time;
        this.description = description;
        this.link=link;
        this.lat = lat;
        this.lng = lng;
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

    public Connections getConnection() {
        return connection;
    }

    public void setConnection(Connections connection) {
        this.connection = connection;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
