package com.example.fixeridetest.NOTIFICATION;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.fixeridetest.NEWS.DriverNews;
import com.example.fixeridetest.NEWS.News;
import com.example.fixeridetest.R;
import com.example.fixeridetest.REPORT.ReportItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

public class FIXERIDENOTIFICATION extends Application {

    private static final String CHANNEL_ID = "NewsNotifications";

    private List<News> news = new ArrayList<>();
    private List<ReportItem> report = new ArrayList<>();

    private DatabaseReference newsReference;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        newsReference = firebaseDatabase.getReference().child("News").child("news");
        getNews();
    }


    void getNews() {
        ChildEventListener bookChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<News> t = new GenericTypeIndicator<News>() {
                };
                News newsUpload = snapshot.getValue(t);
                if (newsUpload != null) {
                    news.add(newsUpload);
                    showNotification(newsUpload.getContent());
                }
            }

            @Override
            public void onChildChanged(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@androidx.annotation.NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        };
        newsReference.addChildEventListener(bookChildEventListener);

    }


    private void showNotification(
            String content
    ) {
        // Create an explicit intent for an activity in your app
        // Here, YourActivity is the activity that you want to open when the notification is clicked
        Intent intent = new Intent(this, DriverNews.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("NEWS ADDED")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if Android version is Oreo or higher, as notification channels are required from Oreo onward
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "News Notifications";
            String description = "Notifications for news alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(0, builder.build());
    }



}
