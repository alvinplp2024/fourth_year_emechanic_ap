package com.example.fixeridetest.ADMIN;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fixeridetest.R;
import com.example.fixeridetest.REPORT.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageNews extends AppCompatActivity implements NewsAdapterManage.OnDeleteClickListener{

    private NewsAdapterManage newsAdapter;
    private List<NewsMech> newsList;
    private final DatabaseReference newsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("News").child("news");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_news);

        RecyclerView newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapterManage(newsList);
        newsAdapter.setOnDeleteClickListener(this); // Set the delete click listener here
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        fetchNewsFromDatabase();
    }
    private void fetchNewsFromDatabase() {
        newsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NewsMech news = snapshot.getValue(NewsMech.class);
                    if (news != null) {
                        // Assuming you have a "date" field in your database
                        // Update this part based on your actual data structure
                        String dateString = snapshot.child("date").getValue(String.class);
                        if (dateString != null && !dateString.isEmpty()) {
                            try {
                                // Parse the date string and set it in the News object
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                                Date date = dateFormat.parse(dateString);
                                news.setDate(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        newsList.add(news);
                    }
                }
                // Check if newsList is empty
                if (newsList.isEmpty()) {
                    // If no news available, add a placeholder news
                    NewsMech noNews = new NewsMech();
                    noNews.setTitle("NO NEWS AVAILABLE");
                    newsList.add(noNews);
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public void onDeleteClick(int position) {
        NewsMech news = newsList.get(position);
        String newsId = news.getId(); // Get the ID of the news item
        deleteNewsFromDatabase(newsId);
    }

    private void deleteNewsFromDatabase(String newsId) {
        DatabaseReference newsRef = newsDatabaseRef.child(newsId);
        newsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ManageNews.this, "News deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageNews.this, "Failed to delete news", Toast.LENGTH_SHORT).show();
                });
    }
}

