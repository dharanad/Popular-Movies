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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends AppCompatActivity {


    @BindView(R.id.user_email_edit_text)
    EditText emailEditText;

    @BindView(R.id.button_sign_up)
    Button signUpButton;

    @BindView(R.id.button_send_password_reset_instructions)
    Button resetPasswordButton;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //if user is signed in, then go to the HiringListActivity
            startActivity(new Intent(ResetPasswordActivity.this, MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.button_send_password_reset_instructions)
    public void resetButtonClicked() {
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getResources().getString(R.string.error_enter_valid_email));
            return;
        }

        if (InternetConnectivity.isInternetConnected(ResetPasswordActivity.this)) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                emailEditText.setText("");
                                Toast.makeText(ResetPasswordActivity.this, getResources()
                                        .getString(R.string.toast_password_reset_instruction_sent), Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                emailEditText.setError(getResources().getString(R.string.error_email_not_registered));
                            }
                        }
                    });
        } else {
            Toast.makeText(ResetPasswordActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_sign_up)
    public void signUpButtonClick() {
        startActivity(new Intent(ResetPasswordActivity.this, SignUpActivity.class));
        finish();
    }
}
