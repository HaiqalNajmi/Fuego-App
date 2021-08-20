package com.azul.fuego.core.objects;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.azul.fuego.core.Fuego;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class Restaurant implements Parcelable {
    private String refID, name, phone, address, about, photo_url, website;
    private Integer seats;
    private Map<String, Map<String, Booking>> bookingList;
    private Map<String, String> operating;
    private GeoPoint location;

    private double rating;

    public Restaurant() {
        this.bookingList = new HashMap<>();
    }

    public Restaurant(String refID, String name, String phone, String address, String about, String imgUri, String website, Integer seats, Map<String, Map<String, Booking>> bookingList, Map<String, String> operating, GeoPoint location, double rating) {
        this.refID = refID;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.about = about;
        this.photo_url = imgUri;
        this.website = website;
        this.seats = seats;
        this.bookingList = bookingList;
        this.operating = operating;
        this.location = location;
        this.rating = rating;
    }

    protected Restaurant(Parcel in) {
        refID = in.readString();
        name = in.readString();
        phone = in.readString();
        address = in.readString();
        about = in.readString();
        photo_url = in.readString();
        website = in.readString();
        if (in.readByte() == 0) {
            seats = null;
        } else {
            seats = in.readInt();
        }
        rating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(refID);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(about);
        dest.writeString(photo_url);
        dest.writeString(website);
        if (seats == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(seats);
        }
        dest.writeDouble(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public Map<String, String> getOperating() {
        return operating;
    }

    public void setOperating(Map<String, String> operating) {
        this.operating = operating;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String addBooking(String uid, String date, String time, Integer seat) {
        String ID = "ODR" + Fuego.GenerateRandomString(4);
        try {
            Map<String, Booking> currentBook = bookingList.getOrDefault(date, new HashMap<>());
            currentBook.put(ID, new Booking(uid, time, seat));
            bookingList.put(date, currentBook);
        } catch (Exception e) {

        }

        return ID;
    }

    public Integer getAvailableSeats(String date, String time) {
        Integer occupied = 0;

        if (bookingList.get(date) != null) {
            for (Booking table : bookingList.get(date).values()) {
                if (table.getBooked_time().equals(time))
                    occupied += table.getBooked();
            }
        }

        return (seats - occupied);
    }

    public Map<String, Map<String, Booking>> getBookingList() {
        return bookingList;
    }

    public void setBookingList(Map<String, Map<String, Booking>> bookingList) {
        this.bookingList = bookingList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getWebsite() {
        return website;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
