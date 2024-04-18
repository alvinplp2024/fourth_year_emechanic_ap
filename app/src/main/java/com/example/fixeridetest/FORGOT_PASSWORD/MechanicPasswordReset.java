package com.example.fixeridetest.FORGOT_PASSWORD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.fixeridetest.MechanicLoginRegisterActivity;
import com.example.fixeridetest.R;
import com.example.fixeridetest.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MechanicPasswordReset extends AppCompatActivity {

    // Declaration
    Button btnReset, btnBack;
    EditText edtEmail; // Use email field for password reset
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_password_reset);

        // Initialization
        btnBack = findViewById(R.id.btnForgotPasswordBack);
        btnReset = findViewById(R.id.btnReset);
        edtEmail = findViewById(R.id.edtForgotPassmechanic);
        progressBar = findViewById(R.id.forgetPasswordProgressbar);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

        // Reset Button Listener
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    resetPassword(email);
                } else {
                    edtEmail.setError("Email field can't be empty");
                }
            }
        });

        // Back Button Code
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MechanicPasswordReset.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MechanicPasswordReset.this, MechanicLoginRegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorAndReset("Error resetting password: " + e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reset link sent successfully
                            Toast.makeText(MechanicPasswordReset.this, "Please check your email to complete the password reset process", Toast.LENGTH_LONG).show();

                            // After the user has changed the password and signs in again, you can handle the success in the DriverLogin activity
                            // Example: In DriverLogin activity, listen for sign-in success and update the database if needed
                        }
                    }
                });
    }

    private void showErrorAndReset(String errorMessage) {
        Toast.makeText(MechanicPasswordReset.this, errorMessage, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.VISIBLE);
    }
}
