package com.example.firebasemessaging.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasemessaging.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtEmail, edtPassword;
    MaterialButton btnSignUp, btnSignIn, btnLoginGG;
    LoginButton btnLoginFB;
    TextView btnForgotPassword;
    RelativeLayout loginContainer;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;

    // đăng ký activity for result
    ActivityResultLauncher<Intent> LoginGoogleResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d("EEE", "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        Log.w("EEE", "Google sign in failed ", e);
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        AnhXa();
        ConfigGoogleSignIn();
        ConfigFacebookSignIn();
        EventListener();
    }


    private void AnhXa() {
        edtEmail = findViewById(R.id.edt_email_login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnSignUp = findViewById(R.id.btn_register);
        btnSignIn = findViewById(R.id.btn_login);
        btnLoginFB = findViewById(R.id.btn_login_facebook);
        btnLoginGG = findViewById(R.id.btn_login_google);
        loginContainer = findViewById(R.id.login_container);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
    }

    private void ConfigGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void ConfigFacebookSignIn(){
        callbackManager = CallbackManager.Factory.create();
    }



    private void EventListener() {
        loginContainer.setOnClickListener(v -> {
            hideSoftKeyBoard();
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(edtEmail.getText()).toString();
            String password = Objects.requireNonNull(edtPassword.getText()).toString();
            NormalLogin(email, password);
        });

        btnLoginGG.setOnClickListener(v -> GoogleLogin());

        btnLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("EEE", "facebook:onSuccess:" + loginResult);
                FacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("EEE", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("EEE", "facebook:onError", error);
            }
        });

        btnForgotPassword.setOnClickListener(v -> {

        });
    }

    private void NormalLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                NavigateToMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "Vui lòng xác thực tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("EEE", "Login Failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });

    }

    private void GoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        LoginGoogleResult.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        NavigateToMainActivity();
                    } else {
                        Log.e("EEE", "gg failed: " + task.getException().getMessage());
                    }
                });
    }

    private void FacebookLogin(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        NavigateToMainActivity();
                    } else {
                        Log.e("EEE", "FB SignIn Failed: " + task.getException().getMessage());
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void NavigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

