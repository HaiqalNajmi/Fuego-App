package com.azul.fuego.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.azul.fuego.R;
import com.azul.fuego.core.Fuego;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class ProfileEditFragment extends Fragment {
    private EditText etName, etEmail, etPass, etPhone;
    private Button updateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.profile_edit_et_name);
        etEmail = view.findViewById(R.id.profile_edit_et_email);
        etPass = view.findViewById(R.id.profile_edit_et_password);
        etPhone = view.findViewById(R.id.profile_edit_et_phone);
        updateBtn = view.findViewById(R.id.profile_edit_btn_update);

        etName.setText(Fuego.UserData.getFullname());
        etEmail.setText(Fuego.UserData.getEmail());
        etPhone.setText(Fuego.UserData.getPhone());

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBtn.setEnabled(false);

                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (!(TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) && Fuego.isValidEmail(email) && (TextUtils.isEmpty(pass) | pass.length() > 5)) {
                    Fuego.UserData.UpdateProfile(name, email, phone);

                    if (!TextUtils.isEmpty(pass)) {
                        Fuego.User.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful())
                                    Toast.makeText(view.getContext(), "Failed to update password. [MSG: " + task.getException().getMessage() + "]", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    Snackbar.make(view, "Your profile information has been successfully updated!", Snackbar.LENGTH_LONG).show();
                    NavHostFragment.findNavController(ProfileEditFragment.this).popBackStack();
                    /*
                        if (!email.equals(Fuego.User.getEmail())) {
                            Fuego.User.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful())
                                        Toast.makeText(view.getContext(), "Failed to update email. [MSG: " + task.getException().getMessage() + "]", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        if (!TextUtils.isEmpty(pass)) {
                            Fuego.User.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful())
                                        Toast.makeText(view.getContext(), "Failed to update password. [MSG: " + task.getException().getMessage() + "]", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();
                        Fuego.User.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Snackbar.make(view, "Your profile information has been successfully updated!", Snackbar.LENGTH_LONG).show();
                                    NavHostFragment.findNavController(ProfileEditFragment.this).popBackStack();
                                }
                            }
                        });
                     */
                } else {
                    if (TextUtils.isEmpty(name))
                        etName.setError("");
                    else if (TextUtils.isEmpty(email))
                        etEmail.setError("");
                    else if (!TextUtils.isEmpty(pass) && pass.length() < 6)
                        etPass.setError("");
                    else if (TextUtils.isEmpty(phone))
                        etPhone.setError("");
                }
            }
        });
    }
}