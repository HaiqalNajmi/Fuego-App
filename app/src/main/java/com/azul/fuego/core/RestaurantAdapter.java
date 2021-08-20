package com.azul.fuego.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azul.fuego.R;
import com.azul.fuego.core.objects.Restaurant;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class RestaurantAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder> {
    public interface OnRestaurantSelectedListener {
        void onRestaurantSelected(Restaurant restaurant);
    }

    private OnRestaurantSelectedListener mListener;

    public RestaurantAdapter(Query query, OnRestaurantSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.rv_card_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView imgRestaurantImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.card_restaurant_tv_name);
            imgRestaurantImage = itemView.findViewById(R.id.card_restaurant_img_main);
        }

        public void bind(final DocumentSnapshot snapshot, final OnRestaurantSelectedListener listener) {
            Restaurant restaurant = snapshot.toObject(Restaurant.class);
            restaurant.setRefID(snapshot.getId());

            /*
            snapshot.getReference().collection("bookingList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<Booking> bookedList = new ArrayList<>();
                            for (Map.Entry<String, Object> test : document.getData().entrySet())
                                bookedList.add(new Booking((Map<String, Object>) test.getValue()));

                            restaurant.getBookingList().put(document.getId(), bookedList);
                        }
                    }
                }
            });

             */
            tvName.setText(restaurant.getName());
            Glide.with(imgRestaurantImage.getContext()).load(restaurant.getPhoto_url()).fitCenter().into(imgRestaurantImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onRestaurantSelected(restaurant);
                }
            });
        }
    }
}
