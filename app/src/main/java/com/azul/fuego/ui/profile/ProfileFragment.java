package com.azul.fuego.ui.profile;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.azul.fuego.MainMenuActivity;
import com.azul.fuego.R;
import com.azul.fuego.core.Fuego;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private TextView nickname, fullName, email, phone;
    private ProfileViewModel mViewModel;
    protected ImageView editBtn;
    protected CircleImageView editPictureBtn;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE_REQUEST = 22;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nickname = getView().findViewById(R.id.profile_tv_short_name);
        fullName = getView().findViewById(R.id.profile_tv_name);
        email = getView().findViewById(R.id.profile_tv_email);
        phone = getView().findViewById(R.id.profile_tv_phone);
        editBtn = getView().findViewById(R.id.profile_iv_edit);
        editPictureBtn = getView().findViewById(R.id.profile_iv_image);

        if (Fuego.UserData.getPhotoURL() != null && !TextUtils.isEmpty(Fuego.UserData.getPhotoURL())) {
            Glide.with(view).load(Fuego.UserData.getPhotoURL()).into(editPictureBtn);
        }

        mViewModel.getUserDataMutableLiveData().observe(getViewLifecycleOwner(), users -> {
            fullName.setText(users.getFullname());
            nickname.setText(users.getFullname().split(" ")[0]);
            email.setText(users.getEmail());
            phone.setText(TextUtils.isEmpty(users.getPhone()) ? "No phone number attached." : users.getPhone());
        });

        editPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPictureBtn.setEnabled(false);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_bottom_sheet, view.findViewById(R.id.bottomSheetContainer));
                // Take Photo Button
                bottomSheetView.findViewById(R.id.bottom_sheet_btn_take_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getContext(), "Camera is not available.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Choose From Photos
                bottomSheetView.findViewById(R.id.bottom_sheet_btn_choose_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
                    }
                });
                // Cancel Button
                bottomSheetView.findViewById(R.id.bottom_sheet_btn_cancel).setOnClickListener(v1 -> bottomSheetDialog.dismiss());
                bottomSheetDialog.setOnDismissListener(dialog -> editPictureBtn.setEnabled(true));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        editBtn.setOnClickListener(v -> NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.nav_profile_edit));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            editPictureBtn.setImageBitmap(imageBitmap);

            uploadImage();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                editPictureBtn.setImageBitmap(bitmap);

                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final StorageReference ref = Fuego.mStorage.child("profile/" + Fuego.User.getUid() + ".jpeg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) editPictureBtn.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Fuego.UserData.setPhotoURL(task.getResult().toString());
                        Fuego.UserData.save();
                        ((MainMenuActivity)getActivity()).RefreshProfilePic();
                    }
                });
                Toast.makeText(getContext(), "Profile picture has been updated.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Uploaded " + (int) progress + "%");
        });
    }
}