package com.example.firebasemessaging.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.firebasemessaging.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    MaterialButton btnSignOut;
    CircleImageView imgUser;
    FirebaseUser user;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
        GetUser();
        EventListener();
    }

    private void EventListener() {
        btnSignOut.setOnClickListener( v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }


    private void AnhXa() {
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.txt_user_info);
        imgUser = findViewById(R.id.img_user_info);
        btnSignOut = findViewById(R.id.btn_sign_out);
    }

    private void GetUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Tiến Dũng đẹp trai")
                .setPhotoUri(Uri.parse("https://toplist.vn/images/800px/dan-ong-thuong-cam-thay-ho-co-the-la-chinh-minh-khi-hen-ho-voi-mot-co-gai-binh-thuong-444281.jpg"))
                .build();
        user.updateProfile(profileUpdates);

        RenderUserInfo();
    }

    @SuppressLint("SetTextI18n")
    private void RenderUserInfo() {
        textView.setText("Email: " + user.getEmail());
        textView.append("\n DisplayName: " + user.getDisplayName());
        textView.append("\n PhoneNumber: " + user.getPhoneNumber());
        Glide.with(this).load(user.getPhotoUrl())
                .error(R.drawable.ic_launcher_background)
                .into(imgUser);
    }

    private void LoginWithNumberPhone() {
        Intent intent = getIntent();
        if (intent != null) {
            String phoneUser = intent.getStringExtra("phone");
            Toast.makeText(this, phoneUser, Toast.LENGTH_SHORT).show();

        }
    }
}