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

import com.example.fixeridetest.FORGOT_PASSWORD.DriverPasswordReset;
import com.example.fixeridetest.PHONELOGIN.LoginPhoneNumberActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverLoginRegisterActivity extends AppCompatActivity {

    private Button CustomerLoginButton;
    private Button CustomerRegisterButton;
    private TextView CustomerRegisterLink;
    private TextView CustomerStatus;

    private EditText EmailCustomer;
    private EditText PasswordCustomer;
    private EditText NameCustomer;
    private EditText PhoneCustomer;
    private EditText CarCustomer;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference CustomerDb;
    private DatabaseReference customersRef;
    private DatabaseReference CustomerDatabaseRef;
    private DatabaseReference CustomerDatabaseNameRef;
    private DatabaseReference CustomerDatabasePhoneRef;

    private String onlineCustomerID;
    private TextView customerpasswordreset;
    private Button phone_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth = FirebaseAuth.getInstance();
        CustomerDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        customersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");

        CustomerLoginButton = findViewById(R.id.customer_login_btn);
        CustomerRegisterButton = findViewById(R.id.customer_register_btn);
        CustomerRegisterLink = findViewById(R.id.customer_register_link);
        CustomerStatus = findViewById(R.id.customer_status);
        phone_login = findViewById(R.id.phone_login);
        EmailCustomer = findViewById(R.id.email_customer);
        PasswordCustomer = findViewById(R.id.password_customer);
        NameCustomer = findViewById(R.id.name_customer);
        PhoneCustomer = findViewById(R.id.phone_customer);
        CarCustomer = findViewById(R.id.car_customer);
        customerpasswordreset = findViewById(R.id.customer_password_reset1);

        loadingBar = new ProgressDialog(this);

        NameCustomer.setVisibility(View.INVISIBLE);
        PhoneCustomer.setVisibility(View.INVISIBLE);
        CarCustomer.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setEnabled(false);

        CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerLoginButton.setVisibility(View.INVISIBLE);
                CustomerRegisterLink.setVisibility(View.INVISIBLE);
                customerpasswordreset.setVisibility(View.INVISIBLE);
                phone_login.setVisibility(View.INVISIBLE);
                NameCustomer.setVisibility(View.VISIBLE);
                PhoneCustomer.setVisibility(View.VISIBLE);
                CarCustomer.setVisibility(View.VISIBLE);
                CustomerStatus.setText("REGISTER DRIVER");

                CustomerRegisterButton.setVisibility(View.VISIBLE);
                CustomerRegisterButton.setEnabled(true);
            }
        });


        phone_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    Intent phone = new Intent(DriverLoginRegisterActivity.this, LoginPhoneNumberActivity.class);
                    startActivity(phone);
                } else {
                    Toast.makeText(DriverLoginRegisterActivity.this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });



        customerpasswordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passreset = new Intent(DriverLoginRegisterActivity.this, DriverPasswordReset.class);
                startActivity(passreset);
            }
        });

        CustomerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    // Proceed with registration
                    String email = EmailCustomer.getText().toString();
                    String password = PasswordCustomer.getText().toString();
                    String name = NameCustomer.getText().toString();
                    String phone = PhoneCustomer.getText().toString();
                    String car = CarCustomer.getText().toString();

                    RegisterCustomer(email, password, name, phone, car);
                } else {
                    Toast.makeText(DriverLoginRegisterActivity.this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });


        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    // Proceed with login
                    String email = EmailCustomer.getText().toString();
                    String password = PasswordCustomer.getText().toString();

                    checkCustomerLogin(email, password);
                } else {
                    Toast.makeText(DriverLoginRegisterActivity.this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkCustomerLogin(final String email, final String password) {
        customersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SignInCustomer(email, password);
                } else {
                    Toast.makeText(DriverLoginRegisterActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    private void SignInCustomer(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(DriverLoginRegisterActivity.this, "Please provide both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Driver Login");
        loadingBar.setMessage("Please wait, while we are checking your credentials...");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent customerIntent = new Intent(DriverLoginRegisterActivity.this, MainActivity.class);
                            startActivity(customerIntent);
                            Toast.makeText(DriverLoginRegisterActivity.this, "Driver Logged in Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            finish();
                        } else {
                            Toast.makeText(DriverLoginRegisterActivity.this, "Login Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void RegisterCustomer(final String email, String password, final String name, final String phone, final String car) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(car)) {
            Toast.makeText(DriverLoginRegisterActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(DriverLoginRegisterActivity.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Driver Registration");
        loadingBar.setMessage("Please wait, while we are registering your data...");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            onlineCustomerID = mAuth.getCurrentUser().getUid();
                            CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID);
                            CustomerDatabaseRef.setValue(true);
                            CustomerDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("email");
                            CustomerDatabaseNameRef.setValue(email);
                            CustomerDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("name");
                            CustomerDatabaseNameRef.setValue(name);
                            CustomerDatabasePhoneRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("phone");
                            CustomerDatabasePhoneRef.setValue(phone);
                            CustomerDatabasePhoneRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID).child("vehicle");
                            CustomerDatabasePhoneRef.setValue(car);

                            Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriverLoginRegisterActivity.class);
                            startActivity(driverIntent);

                            Toast.makeText(DriverLoginRegisterActivity.this, "Driver Registered Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        } else {
                            Toast.makeText(DriverLoginRegisterActivity.this, "Registration Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }
    private boolean isValidPhoneNumber(String phone)
    {
        return phone.matches("^(07|01|\\+2547|\\+2541)\\s?\\d{8}$");
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
