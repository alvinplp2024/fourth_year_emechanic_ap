package com.example.fixeridetest.ADMIN;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.fixeridetest.R;
import com.example.fixeridetest.REPORT.Report;
import com.example.fixeridetest.WelcomeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private float startX;
    private Uri resultUri;
    private CircleImageView headerImageView;
    private  DatabaseReference storageRef;
    private String CustomerProfileImageUrl;
    private FirebaseAuth mAuth;







    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private MapView mMapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



















        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins");


        getadminInfo();

        // Use the default arrow icon for the back button in the Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // Get the header view
        headerImageView = headerView.findViewById(R.id.profilePic);



        headerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation item clicks here

                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    saveUserInformation();
                    Toast.makeText(AdminDashboard.this, "Exit Drawer", Toast.LENGTH_LONG).show();
                    //Go to Dashboard
                    Intent exit = new Intent(AdminDashboard.this, AdminDashboard.class);
                    startActivity(exit);
                }



                else if (itemId == R.id.payments) {
                    Toast.makeText(AdminDashboard.this, "Available Reports", Toast.LENGTH_LONG).show();
                    // Start report activity
                    Intent report = new Intent(AdminDashboard.this, Report.class);
                    startActivity(report);
                }


                else if (itemId == R.id.reg_mech) {
                    Toast.makeText(AdminDashboard.this, "Registering Mechanic", Toast.LENGTH_LONG).show();
                    // Start RegisterMechanicActivity
                    Intent registerMechanic = new Intent(AdminDashboard.this, MechanicRegister.class);
                    startActivity(registerMechanic);
                }

                else if (itemId == R.id.logout_admin) {
                    mAuth.signOut();
                    LogOutAdmin();
                }

                else if (itemId == R.id.about) {
                    Toast.makeText(AdminDashboard.this, "Mechanics registered", Toast.LENGTH_LONG).show();
                    // Start MechanicsRegisteredActivity
                    Intent mechanicsRegistered = new Intent(AdminDashboard.this, MechanicsRegistered.class);
                    startActivity(mechanicsRegistered);
                }

                else if (itemId == R.id.support) {
                    Toast.makeText(AdminDashboard.this, "Lets Upload News", Toast.LENGTH_LONG).show();
                    // Start SupportUsActivity
                    Intent news = new Intent(AdminDashboard.this, NewsUpload.class);
                    startActivity(news);
                }

                else if (itemId == R.id.manage) {
                    Toast.makeText(AdminDashboard.this, "Lets Manage News", Toast.LENGTH_LONG).show();
                    // Start SupportUsActivity
                    Intent news_manage= new Intent(AdminDashboard.this, ManageNews.class);
                    startActivity(news_manage);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }























    // Handle navigation drawer open/close events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Override onTouchEvent to disable opening the drawer when swiped from left to right
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float screenWidth = getResources().getDisplayMetrics().widthPixels;
                int swipeThreshold = 40; // Adjust the threshold as needed
                if (startX < endX && startX < swipeThreshold && endX - startX > screenWidth * 0.25) {
                    // Swiped from left to right and started from the left edge
                    return true; // Consume the touch event to prevent opening the drawer
                }
                break;
        }
        return super.onTouchEvent(event);
    }



    //ADMIN IMAGE UPLOADING TO THE DATABASE
    private void getadminInfo()
    {
        storageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map.get("profileimagesUrl") !=null)
                    {
                        CustomerProfileImageUrl = map.get("profileimagesUrl").toString();
                        Glide.with(getApplication())
                                .load(CustomerProfileImageUrl)
                                .into(headerImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {

        //storageRef.updateChildren(userInfo);

        if(resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile images");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri downloadUrl = uri.getResult();

                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Map newImage = new HashMap();
                    newImage.put("profileimagesUrl",downloadUrl.toString());
                    storageRef.updateChildren(newImage);
                    return;
                }
            });
        }
        else{
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            final Uri imageUri = data.getData();
            Uri selectedImageUri = data.getData();

            // Create a unique filename for the image (you may use the user's UID or a timestamp)
            String fileName = "user_image.jpg"; // Change this as needed

            // Get a reference to the location where you want to store the file in Firebase Storage
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile images");

            // Upload the file to Firebase Storage
            UploadTask uploadTask = filePath.putFile(selectedImageUri);

            // Register observers to listen for when the upload is successful or fails
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri downloadUrl = uriTask.getResult();

                    // Update the local variable
                    CustomerProfileImageUrl = downloadUrl.toString();

                    // Update the ImageView using Glide
                    Glide.with(AdminDashboard.this)
                            .load(CustomerProfileImageUrl)
                            .into(headerImageView);
                    // Update the database with the new image URL
                    Map newImage = new HashMap();
                    newImage.put("profileimagesUrl", CustomerProfileImageUrl);
                    storageRef.updateChildren(newImage);

                    // Image uploaded successfully
                    Toast.makeText(AdminDashboard.this, "Image uploaded to Firebase Storage", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle unsuccessful uploads
                    Toast.makeText(AdminDashboard.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                }
            });

            resultUri = imageUri;
            headerImageView.setImageURI(resultUri);
        }
    }
    //END OF ADMIN IMAGE UPLOAD


    private void LogOutAdmin()
    {
        Toast.makeText(AdminDashboard.this, "Logging Out", Toast.LENGTH_LONG).show();

        // Start LoginActivity (or any other activity for logging out)
        Intent welcomeIntent = new Intent(AdminDashboard.this,WelcomeActivity.class);
        welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeIntent);
        finish();
    }
}



