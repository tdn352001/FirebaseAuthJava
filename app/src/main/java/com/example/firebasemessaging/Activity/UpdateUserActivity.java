package com.example.firebasemessaging.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebasemessaging.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateUserActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 12345;
    TextInputEditText edtUserName;
    MaterialButton btnUpdate;
    CircleImageView imgUser;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Uri photoUri;

    ActivityResultLauncher<Intent> ImageSelectResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
           Intent intent = result.getData();

           if(intent != null && intent.getData() != null){
               photoUri = intent.getData();
               Glide.with(UpdateUserActivity.this).load(photoUri).into(imgUser);
           }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        InitComponents();
        GetUser();
        SetInfoUser();
        HandlerEvent();
    }


    private void GetUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void InitComponents() {
        edtUserName = findViewById(R.id.edt_username_update);
        imgUser = findViewById(R.id.img_user_info_update);
        btnUpdate = findViewById(R.id.btn_confirm_update);
    }

    private void SetInfoUser() {
        if (user == null) {
            return;
        }

        edtUserName.setText(user.getDisplayName());
        photoUri = user.getPhotoUrl();
        Glide.with(this).load(photoUri).error(R.drawable.ic_launcher_background).into(imgUser);
    }

    private void HandlerEvent() {
        imgUser.setOnClickListener(v -> {
            if(CheckPermissions()){
                OpenGallery();
            }else{
                RequestPermissions();
            }
        });

        btnUpdate.setOnClickListener(v -> HandlerUpdateInfo());

    }

    private void HandlerUpdateInfo() {
        String username = Objects.requireNonNull(edtUserName.getText()).toString().trim();

        if(username.isEmpty()){
            Toast.makeText(UpdateUserActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(UpdateUserActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(UpdateUserActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        Log.e("EEE", "Update Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }

                });
    }

    private boolean CheckPermissions(){
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                OpenGallery();
            }
        }
    }

    private void OpenGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ImageSelectResult.launch(Intent.createChooser(intent, "SELECT A PICTURE"));
    }
}
