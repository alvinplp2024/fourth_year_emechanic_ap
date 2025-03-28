package com.example.fixeridetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class InvoiceDriver extends AppCompatActivity {

    private Button confirm;
    private TextView mechanicNameTxt,serviceTxt,chargeAmountTxt,extraChargeAmountTxt;
    private String userId,serviceType,mechanicName,chargeAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_driver);

        confirm = (Button) findViewById(R.id.invoice_pay);
        mechanicNameTxt = (TextView) findViewById(R.id.mechanicName);
        serviceTxt = (TextView)findViewById(R.id.service) ;
        chargeAmountTxt = (TextView) findViewById(R.id.chargeAmount);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();

        serviceType = intent.getStringExtra("service");
        mechanicName = intent.getStringExtra("mechanic");
        chargeAmount = intent.getStringExtra("charge");
        serviceTxt.setText(serviceType);
        mechanicNameTxt.setText(mechanicName);
        chargeAmountTxt.setText(chargeAmount);



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceDriver.this, DriverMapActivity.class);
                startActivity(intent);
            }
        });
        //  textView2.setText("" + number);
/*
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("history").child("Customers")
                .child(userId).child("history");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("MechanicWorkID")) {

                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("MechanicWorkID") !=null)
                    {

                        mechanicID = (map.get("MechanicWorkID").toString());

                    }
                    //mechanicID = snapshot.getValue().toString();
                    //GetAssignedCustomerPickUpLocation();
                    //GetAssignedCustomerDestination();
                    polylines = new ArrayList<>();
                    CancelRequest.setVisibility(View.GONE);
                    GettingDriverLocation();
                    GetDriverInfo();
                    getHasRideEnded();
                    //GetAssignedCustomerInfo();
                    CallCabCarButton.setText("Looking for Mechanic Location");
                }
                else {
                    getMechanicWorking();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



*/

    }
}
