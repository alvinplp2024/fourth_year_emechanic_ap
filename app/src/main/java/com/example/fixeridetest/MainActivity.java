package com.example.fixeridetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixeridetest.ADMIN.AdminDashboard;
import com.example.fixeridetest.NEWS.DriverNews;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    View mHeaderView;


    private FirebaseAuth mAuth;
    private DatabaseReference CustomerDatabaseRef;
    private String userID,name;
    private UserInfo userNameInfo;
    private FirebaseUser currentUser;
    private TextView userName;

    private Button requestService;

    private CircleImageView profilePic;
    private TextView headerName;
    private Handler handler;


    private TextView CustomerNameField,CustomerPhoneField,CustomerEmailField;
    private ImageView CustomerProfileImage;

    private String CustomerName,CustomerEmail;
    private String CustomerPhone;
    private String CustomerProfileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        requestService = (Button) findViewById(R.id.requestService);

        CustomerProfileImage = (ImageView) findViewById(R.id.ProfileImage);
        CustomerEmailField = (TextView) findViewById(R.id.customer_email11);
        CustomerNameField = (TextView) findViewById(R.id.customer_name);
        CustomerPhoneField = (TextView) findViewById(R.id.customer_phone);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mHeaderView = navigationView.getHeaderView(0);
        userName=(TextView) findViewById(R.id.textView);

        profilePic = mHeaderView.findViewById(R.id.profilePic);
        headerName = mHeaderView.findViewById(R.id.header_name);


        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        handler = new Handler();
        getUserInfo();


        requestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverMapActivity.class);
                startActivity(intent);
                return;
            }
        });

    }
    private void getUserInfo() {
        CustomerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isDestroyed()) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        if (map != null) {
                            if (map.get("name") != null) {
                                name = map.get("name").toString();
                                simulateTypingEffect("Hello " + name + ".\tWelcome To E-MECHANIC APP.\n\nPress REQUEST SERVICE To Get Services From Our Esteemed Mechanics", userName);
                                headerName.setText("Hello " + name);
                            }

                            if (map.get("profileimagesUrl") != null) {
                                String imageUrl = map.get("profileimagesUrl").toString();
                                Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.img)
                                        .into(profilePic);
                            }
                        }
                    }
                }
            }
            //typing effect
            private void simulateTypingEffect(final String text, final TextView textView) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    int index = 0;

                    @Override
                    public void run() {
                        textView.setText(text.subSequence(0, index++));

                        if (index <= text.length()) {
                            handler.postDelayed(this, 120); // Adjust the delay as needed
                        }
                    }
                }, 100); // Initial delay
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        CustomerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("email") !=null)
                    {
                        CustomerEmail = map.get("email").toString();
                        CustomerEmailField.setText(CustomerEmail);
                    }

                    if(map.get("name") !=null)
                    {
                        CustomerName = map.get("name").toString();
                        CustomerNameField.setText(CustomerName);
                    }
                    if(map.get("phone") !=null)
                    {
                        CustomerPhone = map.get("phone").toString();
                        CustomerPhoneField.setText(CustomerPhone);
                    }
                    if(map.get("profileimagesUrl") !=null)
                    {
                        CustomerProfileImageUrl = map.get("profileimagesUrl").toString();
                        Glide.with(getApplication())
                                .load(CustomerProfileImageUrl)
                                .into(CustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear Glide requests associated with the activity
        Glide.with(this).clear(profilePic);
        // Remove pending callbacks from the handler
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, DriverSettingActivity.class);
            startActivity(intent);
        }
        else if (itemId == R.id.tips) {
            Intent intenttips = new Intent(MainActivity.this, TipsActivity.class);
            startActivity(intenttips);
        }
        else if (itemId == R.id.history) {
            Intent intenthistory = new Intent(MainActivity.this, HistoryActivity.class);
            intenthistory.putExtra("customerOrDriver", "Customers");
            startActivity(intenthistory);
        }
        else if (itemId == R.id.news) {
        Intent intentnews = new Intent(MainActivity.this, DriverNews.class);
        startActivity(intentnews);
        }

        else if (itemId == R.id.logout) {
            mAuth.signOut();
            LogOutCustomer();
        }

        return false;
    }


    private void LogOutCustomer() {
        Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }

}