package com.example.fixeridetest.ADMIN;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixeridetest.ADAPTER.MechanicsAdapter;
import com.example.fixeridetest.Model.MechanicsRetrival;
import com.example.fixeridetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MechanicsRegistered extends AppCompatActivity{

    private MechanicsAdapter mechanicsAdapter;
    private List<MechanicsRetrival> mechanicsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanics_registered);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

        RecyclerView recyclerView = findViewById(R.id.mechanicsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mechanicsList = new ArrayList<>();
        mechanicsAdapter = new MechanicsAdapter(this, mechanicsList);
        recyclerView.setAdapter(mechanicsAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mechanicsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MechanicsRetrival mechanic = snapshot.getValue(MechanicsRetrival.class);
                    if (mechanic != null) {
                        mechanicsList.add(mechanic);
                    }
                }
                mechanicsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }


}

