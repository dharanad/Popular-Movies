package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dharan1011.popular_movie_app.Utils.InternetConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_email)
    EditText userEmailEditText;

    @BindView(R.id.password_edit_text)
    EditText accountPasswordEditText;

    @BindView(R.id.button_sign_in)
    Button signInButton;

    @BindView(R.id.button_sign_up)
    Button logInButton;

    @BindView(R.id.button_forgot_password)
    Button forgotPassword;

    private FirebaseAuth mAuth;

    private String email;
    private String password;
    private String name;
    private String city;
    private String gender;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }

    @OnClick(R.id.button_sign_in)
    public void signInButtonClicked() {
        email = userEmailEditText.getText().toString().trim();
        password = accountPasswordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            userEmailEditText.setError(getResources().getString(R.string.error_enter_valid_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            accountPasswordEditText.setError(getResources().getString(R.string.error_password_less_than_six_character_long));
            return;
        }
        if (InternetConnectivity.isInternetConnected(LoginActivity.this)) {
            SignInWithEmailPassword(email, password);
        } else {
            Toast.makeText(LoginActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
    }

    private void SignInWithEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            checkIfEmailVerified();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getResources()
                                            .getString(R.string.toast_authentication_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            //restart this activity
        }
    }

    @OnClick(R.id.button_sign_up)
    public void signUpButtonClicked() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        finish();
    }

    @OnClick(R.id.button_forgot_password)
    public void resetPasswordButtonClicked() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        finish();
    }
}