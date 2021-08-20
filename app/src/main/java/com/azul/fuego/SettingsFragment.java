package com.azul.fuego;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.azul.fuego.core.Fuego;

public class SettingsFragment extends Fragment {
    TextView tvLanguage;
    LinearLayout llLanguage, llNotification, llSendOffer;
    Switch swNotification, swSendOffer;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String SP_LANGUAGE = "language";
    String SP_NOTIFICATION = "notification";
    String SP_SENDOFFERS = "sendoffers";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLanguage = view.findViewById(R.id.settings_tv_language);

        llLanguage = view.findViewById(R.id.setting_ll_language);
        llNotification = view.findViewById(R.id.setting_ll_notification);
        llSendOffer = view.findViewById(R.id.setting_ll_send_offer);

        swNotification = view.findViewById(R.id.setting_switch_notification);
        swSendOffer = view.findViewById(R.id.setting_switch_send_offers);

        sharedPref = this.getActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        tvLanguage.setText(Fuego.availableLanguage[sharedPref.getInt(SP_LANGUAGE, 0)]);
        swNotification.setChecked(sharedPref.getBoolean(SP_NOTIFICATION, false));
        swSendOffer.setChecked(sharedPref.getBoolean(SP_SENDOFFERS, false));

        tvLanguage.setOnClickListener(v -> ShowLanguageOptions());
        swNotification.setOnCheckedChangeListener((buttonView, isChecked) -> editor.putBoolean(SP_NOTIFICATION, isChecked).commit());
        swSendOffer.setOnCheckedChangeListener((buttonView, isChecked) -> editor.putBoolean(SP_SENDOFFERS, isChecked).commit());

        llNotification.setOnClickListener(v -> {
            Boolean flag = swNotification.isChecked();
            swNotification.setChecked(!flag);
            editor.putBoolean(SP_NOTIFICATION, !flag).commit();
        });
        llSendOffer.setOnClickListener(v -> {
            Boolean flag = swSendOffer.isChecked();
            swSendOffer.setChecked(!flag);
            editor.putBoolean(SP_SENDOFFERS, !flag).commit();
        });
    }

    private void ShowLanguageOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Select your language");
        builder.setSingleChoiceItems(Fuego.availableLanguage, sharedPref.getInt(SP_LANGUAGE, 0), (dialog, which) -> {
            editor.putInt(SP_LANGUAGE, which).commit();
            dialog.dismiss();

            tvLanguage.setText(Fuego.availableLanguage[sharedPref.getInt(SP_LANGUAGE, 0)]);
        });
        builder.show();
    }
}