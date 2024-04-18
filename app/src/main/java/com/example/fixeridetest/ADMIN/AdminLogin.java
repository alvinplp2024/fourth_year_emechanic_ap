package com.example.fixeridetest.ADMIN;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixeridetest.R;

import java.util.HashMap;
import java.util.Map;

public class AdminLogin extends AppCompatActivity {

    private static final Map<String, String> adminLogins;

    static {
        adminLogins = new HashMap<>();
        adminLogins.put("alvinondieki5@gmail.com", "Alvin123");
        // Add more admin logins if needed
    }

    private EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        usernameEditText = findViewById(R.id.email_admin);
        passwordEditText = findViewById(R.id.password_admin);
        Button loginButtonAdmin = findViewById(R.id.btn_signin_admin);

        loginButtonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        // Check network connectivity before proceeding with login
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
            return;
        }

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_LONG).show();
            return;
        }

        // Check against static logins
        if (isAdminLogin(username, password)) {
            signInExistingUser(username, password);
        } else {
            Toast.makeText(AdminLogin.this, "Invalid admin credentials", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isAdminLogin(String username, String password) {
        // Check if the entered credentials match the static admin logins
        return adminLogins.containsKey(username) && adminLogins.get(username).equals(password);
    }

    private void signInExistingUser(String username, String password) {
        // Implement Firebase Authentication if needed
        // For the sake of this example, assume authentication is successful
        Toast.makeText(AdminLogin.this, "Authentication successful.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(AdminLogin.this, AdminDashboard.class);
        startActivity(intent);
        finish();
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
