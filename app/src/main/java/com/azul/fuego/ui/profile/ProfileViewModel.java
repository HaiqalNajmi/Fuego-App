package com.azul.fuego.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.objects.Users;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Users> userData;

    public ProfileViewModel() {
        userData = new MutableLiveData<>();
        userData.setValue(Fuego.UserData);
    }

    public LiveData<Users> getUserDataMutableLiveData() {
        return userData;
    }
}