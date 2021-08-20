package com.azul.fuego.ui.restaurant.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azul.fuego.R;
import com.azul.fuego.core.BookingHistoryRecyclerViewAdapter;

import java.util.ArrayList;

public class BookingHistoryFragment extends Fragment {
    private BookingHistoryViewModel mViewModel;
    private RecyclerView bookingRV;
    private BookingHistoryRecyclerViewAdapter bookingAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(BookingHistoryViewModel.class);
        return inflater.inflate(R.layout.booking_history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingRV = getView().findViewById(R.id.booking_history_rv_past);

        mViewModel.getBookingLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookingHistoryViewModel.History>>() {
            @Override
            public void onChanged(ArrayList<BookingHistoryViewModel.History> histories) {
                if (histories != null && histories.size() > 0) {
                    bookingAdapter = new BookingHistoryRecyclerViewAdapter(getContext(), histories);
                    bookingRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    bookingRV.setAdapter(bookingAdapter);
                }
            }
        });
    }
}