package com.azul.fuego.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azul.fuego.R;
import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.RestaurantAdapter;
import com.azul.fuego.core.objects.Restaurant;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment implements RestaurantAdapter.OnRestaurantSelectedListener {
    private HomeViewModel homeViewModel;
    RecyclerView mainRecyclerView;
    RestaurantAdapter restaurantAdapter;
    EditText etSearch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.home_et_search);
        mainRecyclerView = view.findViewById(R.id.home_rv_restaurant);

        initRecyclerView();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = etSearch.getText().toString().trim();

                if (!TextUtils.isEmpty(query)) {
                    restaurantAdapter.setQuery(Fuego.mStore.collection("restaurants").whereGreaterThanOrEqualTo("name", query));
                } else {
                    restaurantAdapter.setQuery(Fuego.mStore.collection("restaurants"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initRecyclerView() {
        Query mQuery = FirebaseFirestore.getInstance().collection("restaurants");
        restaurantAdapter = new RestaurantAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0)
                    mainRecyclerView.setVisibility(View.GONE);
                else
                    mainRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
            }
        };

        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainRecyclerView.setAdapter(restaurantAdapter);
    }

    @Override
    public void onRestaurantSelected(Restaurant restaurant) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        NavHostFragment.findNavController(FragmentManager.findFragment(getView())).navigate(R.id.nav_restaurant_details, bundle);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (restaurantAdapter != null)
            restaurantAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (restaurantAdapter != null)
            restaurantAdapter.stopListening();
    }
}