package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dharan1011.popular_movie_app.Models.UserProfile;
import com.example.dharan1011.popular_movie_app.Utils.InternetConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.user_name_edit_text)
    EditText userNameEditText;

    @BindView(R.id.user_city_edit_text)
    EditText userCityEditText;

    @BindView(R.id.user_email_edit_text)
    EditText userEmailEditText;

    @BindView(R.id.password_edit_text)
    EditText accountPasswordEditText;

    @BindView(R.id.button_sign_up)
    Button signUpButton;

    @BindView(R.id.button_already_registered)
    Button logInButton;

    @BindView(R.id.gender_toggle)
    Spinner genderSpinner;
    ArrayList<String> genderArray;
    String userGender;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String name;
    private String city;
    private String gender;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDrivesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        genderArray = new ArrayList<>();
        genderArray.add("Female");
        genderArray.add("Male");
        genderArray.add("Others");
        setupSpinner();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDrivesDatabaseReference = mFirebaseDatabase.getReference().child("userProfile");

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //if user is signed in, then go to the HiringListActivity
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        }
    }

    @OnClick(R.id.button_sign_up)
    public void signUpButtonClicked() {
        email = userEmailEditText.getText().toString().trim();
        password = accountPasswordEditText.getText().toString().trim();
        name = userNameEditText.getText().toString().trim();
        city = userCityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            userNameEditText.setError(getResources().getString(R.string.error_enter_your_name));
            return;
        }
        if (TextUtils.isEmpty(city)) {
            userCityEditText.setError(getResources().getString(R.string.error_enter_your_city));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            userEmailEditText.setError(getResources().getString(R.string.error_enter_valid_email));
            return;
        }
        if (password.length() < 6) {
            accountPasswordEditText.setError(getResources().getString(R.string
                    .error_password_less_than_six_character_long));
            return;
        }
        if (InternetConnectivity.isInternetConnected(SignUpActivity.this)) {
            SignUpWithEmailPassword(email, password, name, city);
        } else {
            Toast.makeText(SignUpActivity.this, getResources()
                    .getString(R.string.check_internet_connectivity), Toast.LENGTH_SHORT).show();
        }
    }

    public void SignUpWithEmailPassword(final String email, String password, final String name, final String city) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.account_created_now_verify_it), Toast.LENGTH_LONG).show();

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // User is signed in
                            // NOTE: this Activity should get open only when the user is not signed in, otherwise
                            // the user will receive another verification email.

                            UserProfile userProfile = new UserProfile(name, city, gender, email);
                            gender = userGender;
                            userEmailEditText.setText("");
                            accountPasswordEditText.setText("");
                            userNameEditText.setText("");
                            userCityEditText.setText("");

                            mDrivesDatabaseReference.child(user.getUid()).push().setValue(userProfile);

                            sendEmailVerification(user);
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            mAuth.signOut();
                            finish();

                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, getResources()
                                            .getString(R.string.toast_problem_creating_account),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick(R.id.button_already_registered)
    public void alreadyRegisteredTransferToLoginActivity() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }

    /* code below referenced from: https://stackoverflow.com/a/41780828/5770629 */
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user and finish this activity
                            mAuth.signOut();

                        } else {
                        }
                    }
                });
    }

    private void setupSpinner() {
        //codes below referenced from: https://www.mkyong.com/android/android-spinner-drop-down-list-example/

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genderArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                userGender = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userGender = genderArray.get(0);
            }
        });
    }

    @OnClick(R.id.button_already_registered)
    public void alreadyRegisteredSignIn() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}
