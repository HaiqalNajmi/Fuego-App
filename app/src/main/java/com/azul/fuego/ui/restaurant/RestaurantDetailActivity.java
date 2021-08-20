package com.azul.fuego.ui.restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.azul.fuego.R;
import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.objects.Restaurant;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;

public class RestaurantDetailActivity extends Fragment implements OnMapReadyCallback {
    private Restaurant restaurant;
    private TextView name, operating_hours, address, about, seats, phone, website;
    private MapView location;
    private ImageView picture;
    private Button bookBtn;
    private ImageButton backBtn;
    private FloatingActionButton optionsBtn;

    private GoogleMap mMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");
            if (restaurant != null) return;
        }

        getActivity().onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        //return inflater.inflate(R.layout.fragment_restaurant_detail_activity, container, false);
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize
        name = view.findViewById(R.id.restaurant_detail_tv_name);
        operating_hours = view.findViewById(R.id.restaurant_detail_tv_operating);
        address = view.findViewById(R.id.restaurant_detail_tv_address);
        about = view.findViewById(R.id.restaurant_detail_tv_about);
        location = view.findViewById(R.id.restaurant_detail_mv_location);
        picture = view.findViewById(R.id.restaurant_detail_iv_image);
        bookBtn = view.findViewById(R.id.restaurant_detail_btn_book);
        backBtn = view.findViewById(R.id.restaurant_detail_back_btn);
        seats = view.findViewById(R.id.restaurant_detail_tv_seats);
        phone = view.findViewById(R.id.restaurant_detail_tv_phone);
        website = view.findViewById(R.id.restaurant_detail_tv_website);
        optionsBtn = view.findViewById(R.id.restaurant_detail_btn_option_menu);

        name.setText(restaurant.getName());
        address.setText(restaurant.getAddress());
        about.setText(restaurant.getAbout());
        seats.setText(restaurant.getSeats().toString());
        phone.setText(restaurant.getPhone());
        website.setText(restaurant.getWebsite() == "" ? "No website yet." : restaurant.getWebsite());
        Glide.with(picture.getContext()).load(restaurant.getPhoto_url()).fitCenter().into(picture);

        if (restaurant.getOperating() != null) {
            String start = restaurant.getOperating().get("start");
            String end = restaurant.getOperating().get("end");
            Integer diff = (Integer.parseInt(end) - Integer.parseInt(start)) / 100;

            operating_hours.setText(String.format("Open %d hours a day. (%s - %s)", diff, start, end));
        }

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);

        location.onCreate(mapViewBundle);
        location.getMapAsync(this);

        backBtn.setOnClickListener(v -> NavHostFragment.findNavController(RestaurantDetailActivity.this).popBackStack());

        bookBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("restaurant", restaurant);
            NavHostFragment.findNavController(RestaurantDetailActivity.this).navigate(R.id.nav_book_table, bundle);
        });

        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsBtn.setEnabled(false);

                Boolean isFav = Fuego.UserData.getFavourites().contains(restaurant.getRefID());

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_restaurant_bottom_sheet, view.findViewById(R.id.bottom_sheet_restaurant_container));

                if (isFav) {
                    Button btn = bottomSheetView.findViewById(R.id.bottom_restaurant_btn_add_fav);
                    btn.setText("Remove from favourites");
                }
                // Add to fav
                bottomSheetView.findViewById(R.id.bottom_restaurant_btn_add_fav).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();

                        if (isFav) {
                            Fuego.UserData.getFavourites().remove(restaurant.getRefID());
                            Toast.makeText(getContext(), restaurant.getName() + " has been removed from your favourites.", Toast.LENGTH_LONG).show();
                        } else {
                            Fuego.UserData.getFavourites().add(restaurant.getRefID());
                            Toast.makeText(getContext(), restaurant.getName() + " has been added to your favourites.", Toast.LENGTH_LONG).show();
                        }

                        Fuego.UserData.save();
                    }
                });
                bottomSheetView.findViewById(R.id.bottom_restaurant_btn_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Here's " + restaurant.getName() + " on Fuego. Check out what they have! https://fuego.com/" + restaurant.getRefID();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, restaurant.getName());
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });
                // Open in Maps
                bottomSheetView.findViewById(R.id.bottom_restaurant_btn_open_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        GeoPoint loc = restaurant.getLocation();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + loc.getLatitude() + ","+ loc.getLongitude()));
                        startActivity(intent);
                    }
                });
                bottomSheetDialog.setOnDismissListener(dialog -> optionsBtn.setEnabled(true));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        location.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        location.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        location.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        location.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        location.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        location.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        location.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null && restaurant.getLocation() != null) {
            final LatLng latLocation = new LatLng(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLocation).title(restaurant.getName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLocation, 15));
        } else {
            Toast.makeText(getContext(), "Can't get restaurant location. Please try again or contact administrator.", Toast.LENGTH_LONG).show();
        }
    }
}