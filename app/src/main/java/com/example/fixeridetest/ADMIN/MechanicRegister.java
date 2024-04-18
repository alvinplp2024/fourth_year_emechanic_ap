package com.example.fixeridetest.ADMIN;

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

import com.example.fixeridetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MechanicRegister extends AppCompatActivity {

    private Button DriverRegisterButton;
    private TextView DriverStatus;

    private EditText EmailDriver;
    private EditText PasswordDriver,DriverName,DriverCar,DriverPhone;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private DatabaseReference DriverDatabaseRef;
    private DatabaseReference DriverDatabaseNameRef;
    private DatabaseReference DriverDatabaseCarRef;
    private DatabaseReference DriverDatabasePhoneRef;
    private String onlineDriverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_register);

        mAuth = FirebaseAuth.getInstance();

        DriverRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        DriverStatus = (TextView) findViewById(R.id.driver_status);
        EmailDriver = (EditText) findViewById(R.id.email_driver);
        PasswordDriver = (EditText) findViewById(R.id.password_driver);
        DriverCar = (EditText) findViewById(R.id.driver_car);
        DriverName = (EditText) findViewById(R.id.driver_name);
        DriverPhone = (EditText) findViewById(R.id.driver_phone);

        loadingBar = new ProgressDialog(this);

        DriverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    String email = EmailDriver.getText().toString();
                    String password = PasswordDriver.getText().toString();
                    String name = DriverName.getText().toString();
                    String car = DriverCar.getText().toString();
                    String phone = DriverPhone.getText().toString();

                    RegisterDriver(email, password, name, car, phone);
                } else {
                    Toast.makeText(MechanicRegister.this, "No network available. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void RegisterDriver(final String email, String password, final String name,final String car,final String phone)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(MechanicRegister.this,"Please Write Email ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(MechanicRegister.this,"Please Write Password at least 6 character", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(MechanicRegister.this,"Please Write Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(car))
        {
            Toast.makeText(MechanicRegister.this,"Please Write Car", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(MechanicRegister.this,"Please Write Phone", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(MechanicRegister.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            loadingBar.setTitle("Mechanic Registration");
            loadingBar.setMessage("Please wait, while we are register your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // String email = EmailDriver.getText().toString();
                            if(task.isSuccessful())
                            {

                                onlineDriverID = mAuth.getCurrentUser().getUid();
                                DriverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID);
                                DriverDatabaseRef.setValue(true);
                                DriverDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("email");
                                DriverDatabaseNameRef.setValue(email);
                                DriverDatabaseNameRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("name");
                                DriverDatabaseNameRef.setValue(name);
                                DriverDatabaseCarRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("car");
                                DriverDatabaseCarRef.setValue(car);
                                DriverDatabasePhoneRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID).child("phone");
                                DriverDatabasePhoneRef.setValue(phone);

                                Intent driverIntent = new Intent(MechanicRegister.this, AdminDashboard.class);
                                startActivity(driverIntent);

                                Toast.makeText(MechanicRegister.this,"Mechanic Register Successfully...", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();


                            }

                            else
                            {
                                Toast.makeText(MechanicRegister.this,"Registration Unsuccessful, Please Try Again...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
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