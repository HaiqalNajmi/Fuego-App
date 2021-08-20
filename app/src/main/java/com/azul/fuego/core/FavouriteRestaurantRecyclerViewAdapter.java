package com.azul.fuego.core;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.azul.fuego.R;
import com.azul.fuego.core.objects.Restaurant;
import com.bumptech.glide.Glide;

import java.util.List;

public class FavouriteRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<FavouriteRestaurantRecyclerViewAdapter.FavouriteViewHolder> {
    public List<Restaurant> restaurantList;
    private Context context;

    public FavouriteRestaurantRecyclerViewAdapter(Context context, List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavouriteRestaurantRecyclerViewAdapter.FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View restaurant_row = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_favourite_card, parent, false);
        FavouriteViewHolder restaurantVH = new FavouriteViewHolder(restaurant_row);

        return restaurantVH;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteRestaurantRecyclerViewAdapter.FavouriteViewHolder holder, int position) {
        holder.tvName.setText(restaurantList.get(position).getName());
        Glide.with(holder.ivMain.getContext()).load(restaurantList.get(position).getPhoto_url()).into(holder.ivMain);

        // Go to restaurant detail
        holder.v.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("restaurant", restaurantList.get(position));
            Navigation.findNavController(v).navigate(R.id.nav_restaurant_details, bundle);
        });
        // Remove from favourite
        holder.v.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Remove favourite")
                    .setMessage("Are you sure want to remove '" + holder.tvName.getText() + "' from your favourites?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        Fuego.UserData.getFavourites().remove(restaurantList.get(position).getRefID());
                        Fuego.UserData.save();
                        restaurantList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivMain;
        public TextView tvName;
        public View v;

        public FavouriteViewHolder(View itemView) {
            super(itemView);

            v = itemView;
            ivMain = itemView.findViewById(R.id.fav_iv_image);
            tvName = itemView.findViewById(R.id.fav_tv_name);
        }

    }
}
