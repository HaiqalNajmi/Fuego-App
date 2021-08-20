package com.azul.fuego.ui.restaurant.favourites;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.objects.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavouriteRestaurantViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Restaurant>> favouriteListData;
    public static ArrayList<Restaurant> restaurantsList;

    public FavouriteRestaurantViewModel() {
        favouriteListData = new MutableLiveData<>();
        restaurantsList = new ArrayList<>();

        List<String> toFind = Fuego.UserData.getFavourites();

        favouriteListData.setValue(restaurantsList);
        Fuego.mStore.collection("restaurants").whereIn("refID", (toFind.size() > 0 ? toFind : Arrays.asList(""))).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        restaurantsList.add(snapshot.toObject(Restaurant.class));
                        favouriteListData.setValue(restaurantsList);
                    }
                }
            }
        });
    }

    public MutableLiveData<ArrayList<Restaurant>> getFavouriteListData() {
        return favouriteListData;
    }
}