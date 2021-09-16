package com.example.firebasemessaging.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebasemessaging.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    MaterialButton btnUpdate, btnSignOut;
    MaterialButton btnLink;
    CircleImageView imgUser;
    FirebaseUser user;
    FirebaseAuth mAuth;


    AuthCredential credential;
    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> LoginGoogleResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                        Log.d("EEE", "firebaseAuthWithGoogle:" + account.getId());
                    } catch (ApiException e) {
                        Log.w("EEE", "Google sign in failed ", e);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
        GetUser();
        RenderUserInfo();
        ConfigGoogleSignIn();
        EventListener();
    }

    private void AnhXa() {
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.txt_user_info);
        imgUser = findViewById(R.id.img_user_info);
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnUpdate = findViewById(R.id.btn_update_info);
        btnLink = findViewById(R.id.btn_link_provider1);
    }

    private void GetUser() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @SuppressLint("SetTextI18n")
    private void RenderUserInfo() {
        if(user == null){
            Toast.makeText(MainActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_LONG).show();
            return;
        }
        textView.setText("Email: " + user.getEmail());
        textView.append("\n DisplayName: " + user.getDisplayName());
        textView.append("\n PhoneNumber: " + user.getPhoneNumber());
        textView.append("\n PhotoUri: " + user.getPhotoUrl());
        Glide.with(this).load(user.getPhotoUrl())
                .error(R.drawable.ic_launcher_background)
                .into(imgUser);
    }

    private void ConfigGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void EventListener() {
        btnLink.setOnClickListener(v -> {
            GoogleLogin();
        });

        btnSignOut.setOnClickListener( v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateUserActivity.class);
            startActivity(intent);
        });
    }

    private void LoginWithNumberPhone() {
        Intent intent = getIntent();
        if (intent != null) {
            String phoneUser = intent.getStringExtra("phone");
            Toast.makeText(this, phoneUser, Toast.LENGTH_SHORT).show();

        }
    }


    private void GoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        LoginGoogleResult.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        if(mAuth == null){
            Log.e("EEE", "mAtuth Null");
            return;
        }
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("EEE", "linkWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                    } else {
                        Log.w("EEE", "linkWithCredential:failure", task.getException());
                    }
                });
    }
}