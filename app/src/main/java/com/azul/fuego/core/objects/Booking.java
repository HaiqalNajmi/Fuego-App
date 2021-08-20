package com.azul.fuego.core.objects;

import com.google.firebase.Timestamp;

public class Booking {
    private String userID, booked_time;
    private Integer booked;
    private Timestamp bookDate;

    public Booking() { }

    public Booking(String userID, String booked_time, Integer booked) {
        this.userID = userID;
        this.booked_time = booked_time;
        this.booked = booked;
        this.bookDate = Timestamp.now();
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBooked_time() {
        return booked_time;
    }

    public void setBooked_time(String booked_time) {
        this.booked_time = booked_time;
    }

    public Integer getBooked() {
        return booked;
    }

    public void setBooked(Integer booked) {
        this.booked = booked;
    }

    public Timestamp getBookDate() {
        return bookDate;
    }

    public void setBookDate(Timestamp bookDate) {
        this.bookDate = bookDate;
    }
}
