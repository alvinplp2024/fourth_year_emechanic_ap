package com.example.fixeridetest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoutingListener,
        com.google.android.gms.location.LocationListener//,
        //PopupMenu.OnMenuItemClickListener

{
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    //SearchView searchView;

    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private FusedLocationProviderClient mFusedLocationClient;
    private Button CustomerLogoutButton;
    private Button CallCabCarButton, ServiceDescription, messageButton;
    private Button back, btnOK;
    private Button NearbyPlace;
    private Button CustomerSettingButton, CustomerHistoryButton, CancelRequest, EndRequest;
    private String customerID, mechanicID;
    private LatLng CustomerPickUpLocation;
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID, userId, registrationNumber, phone, mechanicWorking;

    private Marker pickupMarker;
    private Boolean requestBol = false;

    private float jarak;
    private String driverDistance = Float.toString(jarak);

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Marker DriverMarker;
    private String requestService, serviceDesrc, mechanicName;
    private LatLng destinationLatLng;

    private DatabaseReference CustomerDatabaseRef;
    private DatabaseReference DriverAvailableRef, AssignMechanicRef;
    private DatabaseReference DriversRef;

    private LinearLayout DriverInfo;
    private ImageView DriverProfileImage;
    private TextView DriverName, DriverPhone, DriverCar, DriverLocation, serviceCharge;

    private RadioGroup DriverRadioGroup;
    private String destination, estimatedCost;
    private RatingBar mRatingBar;

    private EditText serviceDescription;
    private RadioGroup serviceRadioGroup;
    private RadioButton batteryRadioButton, tyreRadioButton, towingRadioButton;


    //private Button btnService;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //request location services
        if (checkLocationPermission()) {
            // Permissions are already granted, proceed with your location-related functionality
            //startLocationUpdates();
        } else {
            // Request location permissions
            requestLocationPermission();
        }

      //  searchView = findViewById(R.id.sv_location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);



    /*

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(DriverMapActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
*/

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(0.0, 0.0);

        back = (Button) findViewById(R.id.back);
        NearbyPlace = (Button) findViewById(R.id.btnWorkshop);
        CallCabCarButton = (Button) findViewById(R.id.customer_call_cab_btn);
        DriverRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        DriverRadioGroup.check(R.id.battery);

        DriverInfo = (LinearLayout) findViewById(R.id.driver_info);
        DriverProfileImage = (ImageView) findViewById(R.id.driver_profileImage);
        DriverName = (TextView) findViewById(R.id.driver_name);
        DriverPhone = (TextView) findViewById(R.id.driver_phone);
        DriverCar = (TextView) findViewById(R.id.driver_car);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        EndRequest = (Button) findViewById(R.id.customer_end_btn);
        messageButton = (Button) findViewById(R.id.customer_message_btn);

        CancelRequest = (Button) findViewById(R.id.customer_call_btn);
        CancelRequest.setVisibility(View.GONE);


        CancelRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (requestBol) {
                    cancel();
                    Intent intent = new Intent(DriverMapActivity.this, Pop.class);
                    startActivity(intent);
                    CancelRequest.setVisibility(View.GONE);
                    DriverRadioGroup.setVisibility(View.VISIBLE);

                    back.setVisibility(View.VISIBLE);
                    NearbyPlace.setVisibility(View.VISIBLE);
                    CallCabCarButton.setText("Request service");
                }

            }
        });


        CallCabCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestBol) {
                    // Cancel logic
                } else {
                    requestBol = true;

                    int selectId = DriverRadioGroup.getCheckedRadioButtonId();
                    final RadioButton radioButton = (RadioButton) findViewById(selectId);

                    if (radioButton.getText() == null) {
                        return;
                    }

                    requestService = radioButton.getText().toString();

                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");

                    showDialog();

                    if (lastLocation != null) {
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(userId, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));

                        CustomerPickUpLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        pickupMarker = mMap.addMarker(new MarkerOptions().position(CustomerPickUpLocation).title("Customer Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    } else {
                        // Handle the case where lastLocation is null
                        Log.e("DriverMapActivity", "lastLocation is null");
                        Toast.makeText(DriverMapActivity.this, "Location is not available", Toast.LENGTH_SHORT).show();
                    }

                    CallCabCarButton.setText("Waiting for mechanic acceptance...");
                    CancelRequest.setVisibility(View.VISIBLE);
                    back.setVisibility(View.GONE);
                    NearbyPlace.setVisibility(View.GONE);
                    DriverRadioGroup.setVisibility(View.GONE);
                    ref.child(userId).child("service").setValue(requestService);
                    ref.child(userId).child("id").setValue(userId);

                    CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);
                    getUserInfo();

                    getMechanicWorking();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                //return;


            }
        });

        NearbyPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverMapActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                destination = place.getName();
                destinationLatLng = place.getLatLng();

            }

            @Override
            public void onError(Status status) {
            }
        });


    }


    private boolean checkLocationPermission() {
        boolean fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarseLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return fineLocationGranted && coarseLocationGranted;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                LOCATION_PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the request is for location permissions
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Location permissions granted, proceed with your location-related functionality
                startLocationUpdates();
            } else {
                // Location permissions denied, handle it (e.g., show a message or disable location features)
                Toast.makeText(DriverMapActivity.this, "Permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setFastestInterval(5000); // Fastest update interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Handle location updates
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    // Do something with the location (e.g., update UI, send to server)
                    // location.getLatitude(), location.getLongitude(), ...
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getUserInfo()
    {
        CustomerDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("registrationNumber") !=null)
                    {
                        registrationNumber = map.get("registrationNumber").toString();
                        ref.child(userId).child("user").setValue(registrationNumber);
                    }
                    if(map.get("phone") !=null)
                    {
                        phone = map.get("phone").toString();
                        ref.child(userId).child("phone").setValue(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /* addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("registrationNumber") !=null)
                    {
                        registrationNumber = map.get("registrationNumber").toString();
                        ref.child(userId).child("user").setValue(registrationNumber);
                    }
                    if(map.get("phone") !=null)
                    {
                        phone = map.get("phone").toString();
                        ref.child(userId).child("phone").setValue(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         */
    }

    private void getMechanicWorking() {
       //DriverInfo.setVisibility(View.VISIBLE);

       /*
            AssignMechanicRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child("Customers").child(userId).child("MechanicWorkID");
            AssignMechanicRef.addValueEventListener(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        mechanicID = dataSnapshot.getValue().toString();
                        //GetAssignedCustomerPickUpLocation();
                        //GetAssignedCustomerDestination();
                        //GettingDriverLocation();
                        //GetDriverInfo();
                        //GetAssignedCustomerInfo();
                    }
               else{
                   getMechanicWorking();

               }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

 */

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers")
                .child(userId);
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


/*

       rootRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) {
                   mechanicID = dataSnapshot.getValue().toString();
                   polylines = new ArrayList<>();
                   CancelRequest.setVisibility(View.GONE);
                   GettingDriverLocation();
                   GetDriverInfo();
                   getHasRideEnded();
               }
               else
               {
                   getMechanicWorking();
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

*/

    }

    private void showDialog() {



/*

        serviceRadioGroup= (RadioGroup) findViewById(R.id.serviceRadioGroup);
        batteryRadioButton = (RadioButton)findViewById(R.id.batteryRadioButton);
        tyreRadioButton = (RadioButton)findViewById(R.id.tyreRadioButton);
        towingRadioButton = (RadioButton)findViewById(R.id.towingRadioButton);


*/


        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DriverMapActivity.this,R.style.dialogTheme);


        //Dialog customDialog = new Dialog(DriverMapActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View ser_layout = inflater.inflate(R.layout.service_description,null);
        final TextView serviceCharge2 = (TextView) ser_layout.findViewById(R.id.service_charge);
        final TextView serviceCharge3 = (TextView) ser_layout.findViewById(R.id.title_charge);
        final EditText serviceDesc=ser_layout.findViewById(R.id.service_description);
        serviceCharge3.setText("Service Charge:");
        if(requestService .equals("Battery ,Breaks or Engine Heat"))
        {
            serviceCharge2.setText("Kshs 500");
            estimatedCost = "500";

        }
        if(requestService.equals("Tyre Burst"))
        {
            serviceCharge2.setText("Kshs 700");
            estimatedCost = "700";
        }
        if(requestService.equals("Others"))
        {
            serviceCharge2.setText("Kshs 2500");
            estimatedCost = "2500";
        }

        dialog.setTitle("Service Description");
        dialog.setMessage("Provide the service description");
        dialog.setView(ser_layout);


/*
        int selectId = serviceRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }

        */

        dialog.setPositiveButton("OK", null);  // Set to null for custom handling

        final AlertDialog alertDialog = dialog.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String description = serviceDesc.getText().toString().trim();
                        if (!TextUtils.isEmpty(description)) {
                            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


/*
                if(requestService == "Battery ,Breaks or Engine Heat")
                {
                    serviceCharge.setText("Kshs 500");
                }
                if(requestService == "Tyre Burst)
                {
                    serviceCharge.setText("RM 20");
                }
                if(requestService == "Towing")
                {
                    serviceCharge.setText("RM 100");
                }
*/
                            /*
                int selectId = serviceRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }

*/
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference serviceDescRef = database.getReference();

                            serviceDescRef.child("Customers Requests").child(currentUserID).child("serviceDescription").setValue(description);
                            serviceDescRef.child("Customers Requests").child(currentUserID).child("Service").setValue(requestService);

                            alertDialog.dismiss();  // Dismiss the dialog only when the description is valid
                        }
                        else
                        {
                            Toast.makeText(DriverMapActivity.this, "Describe your problem", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    GeoQuery geoQuery;
    private void GetClosestDriverCab()
    {
        DriverAvailableRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(DriverAvailableRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerPickUpLocation.latitude,CustomerPickUpLocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound && requestBol)
                {
                    DatabaseReference CustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    CustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();

                                if(driverFound){
                                    return;
                                }

                                if(driverMap.get("service").equals(requestService)){
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();

                                    DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("Customers Requests");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("CustomerRideID",customerId);
                                    map.put("destination",destination);
                                    map.put("service",requestService);
                                    map.put("destinationLat",destinationLatLng.latitude);
                                    map.put("destinationLng",destinationLatLng.longitude);
                                    DriversRef.updateChildren(map);

                                    GettingDriverLocation();
                                    GetDriverInfo();
                                    //getHasRideEnded();
                                    CallCabCarButton.setText("Looking for Driver Location");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(!driverFound)
                {
                    radius = radius + 1;
                    GetClosestDriverCab();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void GetDriverInfo()
    {
        DriverRadioGroup.setVisibility(View.GONE);
        DriverInfo.setVisibility(View.VISIBLE);
           /*
        DatabaseReference CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Service").child(userId);
        CustomerDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("mechanic name") != null) {
                        DriverName.setText(map.get("mechanic name").toString());
                    }
                    if(map.get("mechanic phone") !=null)
                    {

                        DriverName.setText(map.get("mechanic phone").toString());

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
      */
        DatabaseReference CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mechanicID);
        CustomerDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {

                        DriverName.setText(map.get("name").toString());
                        mechanicName=map.get("name").toString();
                    }
                    if (map.get("phone") != null) {

                        DriverPhone.setText(map.get("phone").toString());
                    }
                    if (map.get("car") != null) {

                        DriverCar.setText(map.get("car").toString());
                    }
                    if (map.get("profileimagesUrl") != null) {
                        Glide.with(getApplication()).load(map.get("profileimagesUrl").toString()).into(DriverProfileImage);
                    }



                  /*  int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingAvg=0;
                    for(DataSnapshot child: dataSnapshot.child("rating") .getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!=0){
                        ratingAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingAvg);
*/

                    // }


                    //  }
                    // }
                }
            }
                 @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
                });

    }

    private DatabaseReference DriverLocationRef;
    private ValueEventListener DriverLocationRefListener;
    private void GettingDriverLocation()
    {
        DriverLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working").child(mechanicID).child("l");
        DriverLocationRefListener = DriverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol)
                {
                    List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                    double LocationLat = 0;
                    double LocationLng = 0;


                    if(driverLocationMap.get(0) != null)
                    {
                        LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                    }
                    if(driverLocationMap.get(1) != null)
                    {
                        LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                    }

                    LatLng DriverLatLng = new LatLng(LocationLat,LocationLng);
                    if(DriverMarker != null)
                    {
                        DriverMarker.remove();

                    }

                    Location location1 = new Location("");
                    location1.setLatitude(CustomerPickUpLocation.latitude);
                    location1.setLongitude(CustomerPickUpLocation.longitude);

                    Location location2 = new Location("");
                    location2.setLatitude(DriverLatLng.latitude);
                    location2.setLongitude(DriverLatLng.longitude);

                    float Distance = location1.distanceTo(location2);
                    if (Distance<100){
                        CallCabCarButton.setText("Mechanic's Here");
                    }else{
                        CallCabCarButton.setText("Mechanic Found ");
                    }
                   // CallCabCarButton.setText("Driver Found" + String.valueOf(Distance));
                    CallCabCarButton.setText("Mechanic Found");
                    messageButton.setVisibility(View.VISIBLE);
                    messageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(DriverMapActivity.this,ChatActivity.class);
                            intent.putExtra("customerOrDriver" ,"User");
                           // intent.putExtra("mechanic",mechanicID);
                            intent.putExtra("customer",userId);
                            //intent.putExtra("mechanic",mechanicID);
                           // intent.putExtra("customer",userId);
                            startActivity(intent);
                        }
                    });


//                    DriverLocation.setText(driverDistance);

                    DriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Your Mechanic is HERE!!!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                    getRouteToMarker (DriverLatLng);

                    /*
                    EndRequest.setVisibility(View.VISIBLE);
                    EndRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            endService();
                        }
                    });*/

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded()
    {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("MechanicWorkID");

        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                }
                else{
                    endService();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void endService() {
        requestBol = false;
        //geoQuery.removeAllListeners();
        DriverLocationRef.removeEventListener(DriverLocationRefListener);
        //driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        DatabaseReference CustomersRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("MechanicWorkID");
        CustomersRef.removeValue();
        erasePolylines();
        DatabaseReference AfterserviceRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child(userId);
        AfterserviceRef.removeValue();
        openActivity2();
        /*
        CallCabCarButton.setText("Call Mechanic");

        DriverInfo.setVisibility(View.GONE);
        DriverName.setText("");
        DriverPhone.setText("");
        DriverCar.setText("");

        DriverProfileImage.setImageResource(R.mipmap.ic_default_user);

        DriverRadioGroup.setVisibility(View.VISIBLE);

        back.setVisibility(View.VISIBLE);
        NearbyPlace.setVisibility(View.VISIBLE);
        EndRequest.setVisibility(View.GONE);

         */
    }

    public void openActivity2() {

        Intent intent = new Intent(this, InvoiceDriver.class);
        intent.putExtra("mechanic",mechanicName);
        intent.putExtra("service", requestService);
        intent.putExtra("charge",estimatedCost);

        startActivity(intent);
    }

    private void cancel(){
        requestBol = false;
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerID);
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("Service");
        serviceRef.removeValue();

        DatabaseReference serviceDescRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("serviceDescription");
        serviceDescRef.removeValue();
    }

    private void endRide(){
        /*
        requestBol = false;
        erasePolylines();
        geoQuery.removeAllListeners();
        DriverLocationRef.removeEventListener(DriverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);


        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("Service");
        serviceRef.removeValue();

        DatabaseReference serviceDescRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("service description");
        serviceDescRef.removeValue();

        if(mechanicID != null)
        {
            DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mechanicID).child("Customers Requests");
            DriversRef.removeValue();
            mechanicID = null;
        }
       // driverFound = false;
        //radius=1;
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerID);
        if(pickupMarker !=null && DriverMarker !=null)
        {
            pickupMarker.remove();
            DriverMarker.remove();
        }
        CallCabCarButton.setText("Call Mechanic");

       DriverInfo.setVisibility(View.GONE);
        DriverName.setText("");
        DriverPhone.setText("");
        DriverCar.setText("");

        DriverProfileImage.setImageResource(R.mipmap.ic_default_user);

         */
        requestBol = false;
        //geoQuery.removeAllListeners();
        DriverLocationRef.removeEventListener(DriverLocationRefListener);
        //driveHasEndedRef.removeEventListener(driveHasEndedRefListener);
        erasePolylines();

       // String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("Service").child(userId);
        serviceRef.removeValue();

        DatabaseReference serviceDescRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests").child("serviceDescription");
        serviceDescRef.removeValue();
/*
        if(mechanicID != null)
        {
            DriversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mechanicID).child("Customers Requests");
            DriversRef.removeValue();
            mechanicID = null;
        }

 */
        //driverFound = false;
       // radius=1;
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerID);
        if(pickupMarker !=null && DriverMarker !=null)
        {
            pickupMarker.remove();
            DriverMarker.remove();
        }
        CallCabCarButton.setText("Call Mechanic");

        DriverInfo.setVisibility(View.GONE);
        DriverName.setText("");
        DriverPhone.setText("");
        DriverCar.setText("");

        DriverProfileImage.setImageResource(R.mipmap.ic_default_user);

        DriverRadioGroup.setVisibility(View.VISIBLE);

        back.setVisibility(View.VISIBLE);
        NearbyPlace.setVisibility(View.VISIBLE);

    }

    private void getRouteToMarker(LatLng DriverLatLng) {
        if(DriverLatLng !=null && lastLocation !=null) {
            Routing routing = new Routing.Builder()
                    .key("AIzaSyCKJTxAYBG9V7i_AeWVRbvblp9m7JN4UEA")
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), DriverLatLng)
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        buildGoogleApiClient();
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    lastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    if(!getDriversAroundStarted)
                        getDriversAround();

                }
            }
        }

    };



    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        // if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }

    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    protected void onStop()
    {
        super.onStop();
    }

    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getDriversAround(){
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lastLocation.getLongitude(), lastLocation.getLatitude()), 999999999);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker markerIt :markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }
                LatLng driverLocation = new LatLng(location.latitude,location.longitude);
                Marker mMechanicMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                mMechanicMarker.setTag(key);

                markers.add(mMechanicMarker);
            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt :markers){
                    if(markerIt.getTag().equals(key))
                        markerIt.remove();

                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for(Marker markerIt :markers){
                    if(markerIt.getTag().equals(key))
                        markerIt.setPosition(new LatLng(location.latitude,location.longitude));
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


/*

    public void showpopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_move:
                displayMessage("Move to option selected");
                return true;

            case R.id.action_label:
                displayMessage("Change label option selected");
                return true;
                default:
                    return false;
        }

    }

    private void displayMessage(String message){
        Snackbar.make(findViewById(R.id.rootView),message,Snackbar.LENGTH_SHORT).show();
    }

*/


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Mechanic  "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()/1000 + "KM"+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
           jarak = route.get(i).getDistanceValue();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private  void erasePolylines(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}
