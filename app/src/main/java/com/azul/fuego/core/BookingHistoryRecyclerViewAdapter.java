package com.azul.fuego.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azul.fuego.R;
import com.azul.fuego.core.objects.Restaurant;
import com.azul.fuego.ui.restaurant.BookingConfirmationActivity;
import com.azul.fuego.ui.restaurant.history.BookingHistoryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingHistoryRecyclerViewAdapter extends RecyclerView.Adapter<BookingHistoryRecyclerViewAdapter.BookingViewHolder> {
    public List<BookingHistoryViewModel.History> bookingList;
    private Context context;

    public BookingHistoryRecyclerViewAdapter(Context context, List<BookingHistoryViewModel.History> bookingList) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View booking_row = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_booking_history, parent, false);
        BookingHistoryRecyclerViewAdapter.BookingViewHolder bookingVH = new BookingHistoryRecyclerViewAdapter.BookingViewHolder(booking_row);

        return bookingVH;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd yyyy");
        Date date = bookingList.get(position).getBookingDetail().getBookDate().toDate();

        holder.tvID.setText("#" + bookingList.get(position).getBookingID());
        holder.tvName.setText(bookingList.get(position).getRestaurantName());
        holder.tvDate.setText(sdf.format(date));
        holder.tvTime.setText(bookingList.get(position).getBookingDetail().getBooked_time());
        holder.tvTimeDate.setText(Fuego.FormatDate(bookingList.get(position).getBookingDate()));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }


    public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName, tvDate, tvTime, tvTimeDate, tvID;
        public BookingViewHolder(View itemView) {
            super(itemView);

            tvID = itemView.findViewById(R.id.rv_booking_tv_id);
            tvName = itemView.findViewById(R.id.rv_booking_tv_name);
            tvDate = itemView.findViewById(R.id.rv_booking_tv_date);
            tvTime = itemView.findViewById(R.id.rv_booking_tv_time);
            tvTimeDate = itemView.findViewById(R.id.rv_booking_tv_time_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ProgressDialog progressDialog = new ProgressDialog(v.getContext());
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            Fuego.mStore.collection("restaurants").document(bookingList.get(getAdapterPosition()).getRestaurantID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        Restaurant sRestaurant = task.getResult().toObject(Restaurant.class);
                        BookingHistoryViewModel.History bHistory = bookingList.get(getAdapterPosition());

                        Intent intent = new Intent(v.getContext(), BookingConfirmationActivity.class);
                        intent.putExtra("restaurant", (sRestaurant));
                        intent.putExtra("bookingID", bHistory.getBookingID());
                        intent.putExtra("date", bHistory.getBookingDate());
                        intent.putExtra("time", bHistory.getBookingDetail().getBooked_time());
                        intent.putExtra("seats", bHistory.getBookingDetail().getBooked());
                        v.getContext().startActivity(intent);
                    } else {
                        Snackbar.make(v, "An error has been occurred.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
