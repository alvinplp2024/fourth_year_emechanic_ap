package com.example.fixeridetest.NEWS;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fixeridetest.ADAPTER.NewsAdapter;
import com.example.fixeridetest.R;
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

public class DriverNews extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private List<News> newsList;

    private DatabaseReference newsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("News").child("news");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_news);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        fetchNewsFromDatabase();
    }

    private void fetchNewsFromDatabase() {
        newsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    News news = snapshot.getValue(News.class);
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
                    News noNews = new News();
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
}

