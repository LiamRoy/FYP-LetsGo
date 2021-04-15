package com.example.fyp_staycation.classes;

import com.google.firebase.auth.FirebaseAuth;

public class UserTrip {

    String user1;
    String title;
    String city;
    String county;
    String date;
    String tid;
    String tripUser;
    private String reference;

    public UserTrip(){

    }

    public UserTrip(String user1, String title, String city, String county, String date, String tid, String tripUser,String reference) {

        this.user1=user1;
        this.title=title;
        this.city=city;
        this.county=county;
        this.date=date;
        this.tid =tid;
        this.tripUser=tripUser;
        this.reference=reference;

    }


    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

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

    public String getDate(){return date;}

    public void setDate(String date){this.date=date;}

    public String getTid(){return tid;}

    public void setTid(String tid){this.tid=tid;}

    public String getReference(){return reference;}
    public void setReference(String reference){this.reference=reference;}
}
