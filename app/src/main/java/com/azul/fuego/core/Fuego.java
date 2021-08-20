package com.azul.fuego.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.azul.fuego.core.objects.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*
    ########################
    # FUEGO Bootstrap Class
    # @author Arfan
    # @date Jan 2021
    ########################

 */
public class Fuego extends FirebaseMessagingService {
    public static FirebaseAuth mAuth;
    public static FirebaseUser User;
    public static FirebaseFirestore mStore;
    public static StorageReference mStorage;
    public static FirebaseMessaging mMessage;
    public static Users UserData;
    public static String[] availableLanguage = {"English", "Bahasa Melayu", "Chinese"};

    public Fuego() {
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mMessage = FirebaseMessaging.getInstance();

        mMessage.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.v("asd", task.getResult());
            }
        });
    }

    @Override
    public void onNewToken(@NonNull String s) {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sharedPref = getSharedPreferences("app_settings", Context.MODE_PRIVATE);
    }

    public static void SignOut() {
        mAuth.signOut();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String GenerateRandomString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String FormatDate(String date) {
        return String.format("%s-%s-%s", date.substring(0, 2), date.substring(2,4), date.substring(4));
    }
}
