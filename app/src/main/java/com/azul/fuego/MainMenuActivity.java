package com.azul.fuego;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.azul.fuego.core.Fuego;
import com.azul.fuego.core.objects.Users;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceIdService;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenuActivity extends AppCompatActivity {
    private TextView nav_tvName, nav_tvEmail;
    public CircleImageView nav_ivPhoto;
    private Toolbar toolbar;
    private Fuego myApp;

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_favourites, R.id.nav_profile, R.id.nav_about, R.id.nav_favourites, R.id.nav_settings, R.id.nav_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

                if (!handled) {
                    switch (item.getItemId()) {
                        case R.id.nav_logout:
                            new AlertDialog.Builder(MainMenuActivity.this)
                                    .setTitle("Logout")
                                    .setMessage("Are you sure want to logout?")
                                    .setPositiveButton("YES", (dialog, which) -> {
                                        Fuego.mAuth.signOut();
                                        startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
                            break;
                    }
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (!getSupportActionBar().isShowing() && destination.getId() != R.id.nav_restaurant_details) {
                    getSupportActionBar().show();
                }
            }
        });

        nav_tvName = headerView.findViewById(R.id.nav_tv_name);
        nav_tvEmail = headerView.findViewById(R.id.nav_tv_email);
        nav_ivPhoto = headerView.findViewById(R.id.nav_iv_profile_photo);
    }

    @Override
    protected void onResume() {
        super.onResume();

        myApp = new Fuego();

        if (Fuego.User != null) {
            Fuego.mStore.collection("users").document(Fuego.User.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists() || task.getResult().get("email") == null) {
                        Toast.makeText(this, "Account problem. Please create a new account. [No Data]", Toast.LENGTH_LONG).show();
                        Fuego.mStore.collection("users").document(Fuego.User.getUid()).delete();
                        Fuego.User.delete();
                        Fuego.SignOut();

                        startActivity(new Intent(MainMenuActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        Fuego.UserData = task.getResult().toObject(Users.class);
                        nav_tvName.setText(Fuego.UserData.getFullname());
                        nav_tvEmail.setText(Fuego.UserData.getPhone());

                        if (Fuego.UserData.getPhotoURL() != null && !TextUtils.isEmpty(Fuego.UserData.getPhotoURL())) {
                            Glide.with(this).load(Fuego.UserData.getPhotoURL()).into(nav_ivPhoto);
                        }
                    }
                }
            });
        } else {
            startActivity(new Intent(MainMenuActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void RefreshProfilePic() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_ivPhoto = findViewById(R.id.nav_iv_profile_photo);
        Glide.with(this).load(Fuego.UserData.getPhotoURL()).into(nav_ivPhoto);
    }
}