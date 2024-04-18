package com.example.fixeridetest.ADMIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixeridetest.NEWS.News;
import com.example.fixeridetest.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewsUpload extends AppCompatActivity {

    private EditText titleEditText, contentEditText, startDateEditText, endDateEditText, startTimeEditText, endTimeEditText;

    private DatabaseReference newsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("News").child("news");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_upload);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        Button uploadButton = findViewById(R.id.uploadButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String title = titleEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();
                String startDate = startDateEditText.getText().toString().trim();
                String endDate = endDateEditText.getText().toString().trim();
                String startTime = startTimeEditText.getText().toString().trim();
                String endTime = endTimeEditText.getText().toString().trim();

                // Validate input
                if (isValidInput(title, content, startDate, endDate, startTime, endTime)) {
                    // Save to Firebase Realtime Database
                    saveToDatabase(title, content, startDate, endDate, startTime, endTime);
                    Toast.makeText(NewsUpload.this, "News uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    clearInputFields();
                    Intent intent = new Intent(NewsUpload.this, AdminDashboard.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean isValidInput(String title, String content, String startDate, String endDate, String startTime, String endTime) {
        if (title.isEmpty() || content.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate start date
        if (!isValidDate(startDate)) {
            Toast.makeText(this, "Invalid start date format (MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate end date
        if (!isValidDate(endDate)) {
            Toast.makeText(this, "Invalid end date format (MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate start time
        if (!isValidTime(startTime)) {
            Toast.makeText(this, "Invalid start time format (HH:mm)", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate end time
        if (!isValidTime(endTime)) {
            Toast.makeText(this, "Invalid end time format (HH:mm)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidTime(String time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setLenient(false);

        try {
            Date parsedTime = timeFormat.parse(time);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);

            // Validate month
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
            if (month < 1 || month > 12) {
                return false;
            }

            // Validate day
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 1 || day > 31) {
                return false;
            }

            // Validate year (optional)
            int year = calendar.get(Calendar.YEAR);
            if (year < 2024) {
                return false;
            }

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void saveToDatabase(String title, String content, String startDate, String endDate, String startTime, String endTime) {
        // Generate a unique key for each news item
        String newsId = newsDatabaseRef.push().getKey();

        // Create a News object
        News news = new News(newsId, title, content, startDate, endDate, startTime, endTime);
        // Save the News object to the database
        newsDatabaseRef.child(newsId).setValue(news);
    }

    private void clearInputFields() {
        titleEditText.setText("");
        contentEditText.setText("");
        startDateEditText.setText("");
        endDateEditText.setText("");
        startTimeEditText.setText("");
        endTimeEditText.setText("");
    }
}
