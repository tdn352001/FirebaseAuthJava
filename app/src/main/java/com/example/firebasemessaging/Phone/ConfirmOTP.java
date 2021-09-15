package com.example.firebasemessaging.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasemessaging.Activity.MainActivity;
import com.example.firebasemessaging.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

public class ConfirmOTP extends AppCompatActivity {

    TextInputEditText edtOTPCode;
    MaterialButton btnConfirm, btnSend;
    FirebaseAuth mAuth;
    String Phone, OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        Phone = intent.getStringExtra("phone");
        OTP = intent.getStringExtra("code");
        Log.e("BBB", Phone + "-" + OTP);
        AnhXa();
        HandleEvent();
    }

    private void AnhXa() {
        btnSend = findViewById(R.id.btnSendAgain);
        btnConfirm = findViewById(R.id.btnConfirmCode);
        edtOTPCode = findViewById(R.id.edtOTPCode);
    }

    private void HandleEvent() {
        btnConfirm.setOnClickListener(v -> {
            String code = edtOTPCode.getText().toString().trim();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, code);
            signInWithPhoneAuthCredential(credential);
        });

        btnSend.setOnClickListener(v -> {

        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("BBB", "signInWithCredential:success");
                        FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();

                        if (user != null) {
                            Intent intent = new Intent(ConfirmOTP.this, MainActivity.class);
                            intent.putExtra("phone", user.getPhoneNumber());
                            startActivity(intent);
                        }
                    } else {
                        Log.w("BBB", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(ConfirmOTP.this, "Mã xác nhận sai", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}