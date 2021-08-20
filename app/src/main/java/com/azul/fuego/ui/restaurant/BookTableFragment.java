package com.azul.fuego.ui.restaurant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.azul.fuego.R;
import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.objects.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookTableFragment extends Fragment {
    // TODO: Rename and change types of parameters
    private Restaurant restaurant;
    private EditText etDate;
    private Spinner spTime, spSeats;
    private Button bookBtn;
    private ImageButton editDateBtn;
    private String date, time;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");

            if (restaurant != null) return;
        }

        getActivity().onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDate = view.findViewById(R.id.booking_et_date);
        spTime = view.findViewById(R.id.booking_sp_time);
        spSeats = view.findViewById(R.id.booking_sp_seats);
        bookBtn = view.findViewById(R.id.book_btn_book);
        editDateBtn = view.findViewById(R.id.book_btn_edit_date);

        if (restaurant.getOperating() != null) {
            Integer open = Integer.parseInt(restaurant.getOperating().get("start"));
            Integer close = Integer.parseInt(restaurant.getOperating().get("end"));
            String[] times = new String[(close - open) / 100];
            for (int i = 0; i < times.length; i++)
                times[i] = String.format("%04d", (open + (i*100)));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, times);
            spTime.setAdapter(adapter);
        } else {
            Toast.makeText(view.getContext(), "This shop does not set operating hours. Please try again or contact administrator.", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(BookTableFragment.this).popBackStack();
        }

        spTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etDate.setOnClickListener(v -> showDateDialog());
        editDateBtn.setOnClickListener(v -> showDateDialog());

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(date)) {
                    Integer seats = Integer.parseInt(spSeats.getSelectedItem().toString());
                    String bookID = restaurant.addBooking(Fuego.User.getUid(), date, time, seats);

                    Fuego.mStore.collection("restaurants").document(restaurant.getRefID()).set(restaurant, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getContext(), BookingConfirmationActivity.class);
                                intent.putExtra("restaurant", (Restaurant) restaurant);
                                intent.putExtra("bookingID", bookID);
                                intent.putExtra("date", date);
                                intent.putExtra("time", time);
                                intent.putExtra("seats", seats);
                                startActivity(intent);
                                Toast.makeText(getContext(), "Thank you for booking! Here is your confirmation details.", Toast.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(view, "An error has been occurred.", Snackbar.LENGTH_SHORT).show();
                            }

                            NavHostFragment.findNavController(BookTableFragment.this).navigate(R.id.nav_home);
                        }
                    });
                } else {
                    etDate.setError("Select a date.");
                }
            }
        });
    }

    private void showDateDialog() {
        DatePickerDialog dateDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String date = new SimpleDateFormat("ddMMyyyy", Locale.US).format(myCalendar.getTime());
                etDate.setText(date);
                Update();
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dateDialog.show();
    }

    private void Update() {
        date = etDate.getText().toString().trim();
        time = spTime.getSelectedItem().toString().trim();
        Integer seatsAvailable = restaurant.getAvailableSeats(date, time);

        if (seatsAvailable == 0) {
            bookBtn.setEnabled(false);
            spSeats.setEnabled(false);
        } else {
            bookBtn.setEnabled(true);
            spSeats.setEnabled(true);
        }

        Integer[] seats = new Integer[seatsAvailable];
        for (int i = 0; i < seats.length; i++)
            seats[i] = (i+1);

        spSeats.setAdapter(new ArrayAdapter<Integer>(this.getContext(), android.R.layout.simple_spinner_item, seats));
    }
}