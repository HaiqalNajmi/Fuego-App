package com.azul.fuego.ui.restaurant.history;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.objects.Booking;
import com.azul.fuego.core.objects.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BookingHistoryViewModel extends ViewModel {
    private MutableLiveData<ArrayList<History>> historyLiveData;

    public BookingHistoryViewModel() {
        historyLiveData = new MutableLiveData<>();
        ArrayList<History> bookingDetails = new ArrayList<>();

        Fuego.mStore.collection("restaurants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);

                        if (restaurant.getBookingList() != null) {
                            Iterator it = restaurant.getBookingList().entrySet().iterator();
                            while(it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                Map<String, Booking> pair2 = (Map<String, Booking>) pair.getValue();

                                for (Map.Entry<String, Booking> entry : pair2.entrySet()) {
                                    if (entry.getValue().getUserID().equals(Fuego.UserData.getUID()))
                                        bookingDetails.add(new History(entry.getKey(), restaurant.getRefID(), restaurant.getName(), entry.getValue(), pair.getKey().toString()));
                                }
                            }
                            /*
                            for (Map<String, Booking> dateFolder : restaurant.getBookingList().values()) {
                                /*
                                for (Booking booking : dateFolder.values()) {
                                    if (booking.getUserID().equals(Fuego.User.getUid())) {
                                        bookingDetails.add(new History(restaurant.getRefID(), restaurant.getName(), booking));
                                    }
                                }

                            }
                             */
                        }
                    }
                    Collections.sort(bookingDetails);
                    historyLiveData.setValue(bookingDetails);
                }
            }
        });
    }

    public MutableLiveData<ArrayList<History>> getBookingLiveData() {
        return historyLiveData;
    }

    public class History implements Comparable<History> {
        private String BookingID, RestaurantID, RestaurantName, BookingDate;
        private Booking BookingDetail;

        public History(String id, String restaurantID, String restaurantName, Booking bookingDetail, String date) {
            BookingID = id;
            RestaurantID = restaurantID;
            RestaurantName = restaurantName;
            BookingDetail = bookingDetail;
            BookingDate = date;
        }

        public String getBookingDate() {
            return BookingDate;
        }

        public void setBookingDate(String bookingDate) {
            BookingDate = bookingDate;
        }

        public String getBookingID() {
            return BookingID;
        }

        public void setBookingID(String bookingID) {
            BookingID = bookingID;
        }

        public String getRestaurantID() {
            return RestaurantID;
        }

        public void setRestaurantID(String restaurantID) {
            RestaurantID = restaurantID;
        }

        public String getRestaurantName() {
            return RestaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            RestaurantName = restaurantName;
        }

        public Booking getBookingDetail() {
            return BookingDetail;
        }

        public void setBookingDetail(Booking bookingDetail) {
            BookingDetail = bookingDetail;
        }

        @Override
        public int compareTo(History o) {
            if (BookingDetail.getBookDate() == null || o.getBookingDetail() == null)
                return 0;

            return o.BookingDetail.getBookDate().compareTo(BookingDetail.getBookDate());
        }
    }
}