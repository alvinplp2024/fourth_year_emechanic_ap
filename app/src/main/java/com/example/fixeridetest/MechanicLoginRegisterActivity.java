package com.example.fixeridetest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixeridetest.FORGOT_PASSWORD.MechanicPasswordReset;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MechanicLoginRegisterActivity extends AppCompatActivity {

    private Button DriverLoginButton;
    private EditText EmailDriver;
    private EditText PasswordDriver;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference driversRef;
    private String onlineDriverID;
    private TextView mechanicresetpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_login_register);

        mAuth = FirebaseAuth.getInstance();
        driversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

        DriverLoginButton = findViewById(R.id.driver_login_btn);
        EmailDriver = findViewById(R.id.email_driver);
        PasswordDriver = findViewById(R.id.password_driver);
        mechanicresetpassword = findViewById(R.id.mechanic_password_reset);
        loadingBar = new ProgressDialog(this);

        DriverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    String email = EmailDriver.getText().toString();
                    String password = PasswordDriver.getText().toString();

                    if (TextUtils.isEmpty(email)){
                        Toast.makeText(MechanicLoginRegisterActivity.this, "Please provide email to continue", Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(password)){
                        Toast.makeText(MechanicLoginRegisterActivity.this, "Please provide password to continue", Toast.LENGTH_LONG).show();
                    } else {
                        checkDriverLogin(email, password);
                    }
                } else {
                    Toast.makeText(MechanicLoginRegisterActivity.this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mechanicresetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    Intent mechanicpassreset = new Intent(MechanicLoginRegisterActivity.this, MechanicPasswordReset.class);
                    startActivity(mechanicpassreset);
                } else {
                    Toast.makeText(MechanicLoginRegisterActivity.this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkDriverLogin(final String email, final String password) {
        driversRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    signInDriver(email, password);
                } else {
                    Toast.makeText(MechanicLoginRegisterActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    private void signInDriver(String email, String password) {
        loadingBar.setTitle("Mechanic Login");
        loadingBar.setMessage("Please wait, while we are checking your credentials...");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent driverIntent = new Intent(MechanicLoginRegisterActivity.this, MechanicMapActivity.class);
                            startActivity(driverIntent);
                            Toast.makeText(MechanicLoginRegisterActivity.this, "Mechanic Logged in Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        } else {
                            Toast.makeText(MechanicLoginRegisterActivity.this, "Login Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
