package com.azul.fuego;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {
    private ImageButton btnContactPhone, btnContactEmail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnContactPhone = view.findViewById(R.id.about_btn_phone);
        btnContactEmail = view.findViewById(R.id.about_btn_email);

        btnContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:01133900016"));
                if (intent.resolveActivity(view.getContext().getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        btnContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@fuego.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hello.");

                if (intent.resolveActivity(view.getContext().getPackageManager()) != null)
                    startActivity(intent);
            }
        });
    }
}