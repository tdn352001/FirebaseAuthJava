package com.example.firebasemessaging.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasemessaging.Activity.MainActivity;
import com.example.firebasemessaging.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ConfirmPhoneActivity extends AppCompatActivity {

    TextInputEditText edtPhoneNumber;
    MaterialButton btnConfirm;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone);
        mAuth= FirebaseAuth.getInstance();
        AnhXa();
        HandleEvent();
    }

    private void AnhXa() {
        btnConfirm = findViewById(R.id.btnConfirm);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
    }

    private void HandleEvent() {
        btnConfirm.setOnClickListener(v -> {
            String phoneNumber = Objects.requireNonNull(edtPhoneNumber.getText()).toString().trim();
            Log.e("BBB", phoneNumber);
            HandleRegisterByPhoneNumber(phoneNumber);
        });
    }

    private void HandleRegisterByPhoneNumber(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                                Log.e("BBBB", "Thành công");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e("BBBB", e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Log.e("BBBB", "Gửi Code");
                                Intent intent = new Intent(ConfirmPhoneActivity.this, ConfirmOTP.class);
                                intent.putExtra("phone", phoneNumber);
                                intent.putExtra("code", s);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("BBB", "signInWithCredential:success");
                            FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();

                            if (user != null) {
                                Intent intent = new Intent(ConfirmPhoneActivity.this, MainActivity.class);
                                intent.putExtra("phone", user.getPhoneNumber());
                                startActivity(intent);
                            }
                        } else {
                            Log.w("BBB", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

}