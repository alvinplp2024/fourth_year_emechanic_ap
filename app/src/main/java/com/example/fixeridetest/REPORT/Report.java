package com.example.fixeridetest.REPORT;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.example.fixeridetest.ADMIN.AdminDashboard;
import com.example.fixeridetest.ADMIN.AdminLogin;
import com.example.fixeridetest.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class Report extends AppCompatActivity {

    private final String reportFileName = "report.csv";
    private List<ReportItem> reportData;  // Declare reportData at class level
    private DatabaseReference historyRef;

    private List<ReportItem> report = new ArrayList<>();
    private static final String CHANNEL_IDS = "ReportDownloadNotification";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Button download = findViewById(R.id.downloadButton);

        historyRef = FirebaseDatabase.getInstance().getReference().child("history");

        // Initialize reportData
        reportData = new ArrayList<>();

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull @NonNull DataSnapshot snapshot) {
                for (DataSnapshot historySnapshot : snapshot.getChildren()) {
                    String id = historySnapshot.getKey();
                    ReportItem item = historySnapshot.getValue(ReportItem.class);

                    assert item != null;
                    item.setId(id);
                    reportData.add(item);
                }

                // Call a method to generate the report
                generateReport(reportData);

                // Enable the download button only if there is data available
                if (reportData.size() > 0) {
                    download.setEnabled(true);
                } else {
                    Toast.makeText(Report.this, "No data available for download", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(Report.this, "Failed to retrieve data", Toast.LENGTH_LONG).show();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there is data available for download
                if (reportData.size() > 0) {
                    // Generate and save the report file before downloading
                    File reportFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), reportFileName);
                    writeReportToFile(reportFileName);



                    // Initiate the download
                    downloadFile(reportFileName);

                } else {
                    Toast.makeText(Report.this, "No data available for download", Toast.LENGTH_LONG).show();
                }
            }
        });

        getReport();


    }


    private void generateReport(List<ReportItem> reportData) {

        TableLayout tableLayout = findViewById(R.id.reportTableLayout);
        tableLayout.removeAllViews(); // Clear existing views if any

        // Create the header row with field names
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.LTGRAY); // Header row color


        addHeaderCell(headerRow, "Service ID");
        addHeaderCell(headerRow, "Driver ID");
        addHeaderCell(headerRow, "Mechanic ID");
        addHeaderCell(headerRow, "Service");
        addHeaderCell(headerRow, "Amount");
        addHeaderCell(headerRow, "Rating");
        addHeaderCell(headerRow, "Distance");
        addHeaderCell(headerRow, "Location Latitude");
        addHeaderCell(headerRow, "Location Longitude");
        addHeaderCell(headerRow, "Timestamp");

        tableLayout.addView(headerRow); // Add the header row to the table

        // Create rows with data
        for (ReportItem item : reportData) {
            TableRow dataRow = new TableRow(this);

            addDataCell(dataRow, item.getId());
            addDataCell(dataRow, item.getCustomer());
            addDataCell(dataRow, item.getDriver());
            addDataCell(dataRow, item.getService());
            addDataCell(dataRow, String.valueOf(item.getAmount()));
            addDataCell(dataRow, String.valueOf(item.getRating()));
            addDataCell(dataRow, String.valueOf(item.getJarak()));

            Location location = item.getLocation();
            if (location != null) {
                addDataCell(dataRow, String.valueOf(location.getFrom().getLat()));
                addDataCell(dataRow, String.valueOf(location.getFrom().getLng()));
            } else {
                addDataCell(dataRow, "");
                addDataCell(dataRow, "");
            }

            addDataCell(dataRow, String.valueOf(item.getTimestamp()));

            tableLayout.addView(dataRow); // Adding the data row to the table
        }

    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackgroundResource(R.drawable.cell_header_background); // Header cell background color
        textView.setPadding(10, 5, 10, 5); // Cell padding
        textView.setTypeface(null, Typeface.BOLD); // Making text bold
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 2, 0, 0); // Set margin top
        textView.setLayoutParams(params); // Apply layout parameters
        row.addView(textView); // Adding the cell to the row
    }

    private void addDataCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setBackgroundResource(R.drawable.cell_background); // Data cell background color
        textView.setPadding(10, 5, 10, 5); // Cell padding
        row.addView(textView); // Adding the cell to the row
    }




    private void writeReportToFile(String fileName) {
        ContentResolver contentResolver = getContentResolver();

        // Define the content values for the new file
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv");

        // Insert the new file into MediaStore
        Uri uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues);

        try {
            if (uri != null) {
                // Open an OutputStream to the newly created file
                OutputStream outputStream = contentResolver.openOutputStream(uri);

                if (outputStream != null) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                    // Write header
                    writer.write("Service ID,Driver ID,Mechanic ID,Service,Amount,Rating,Distance,Location Latitude,Location Longitude,Timestamp");
                    writer.newLine();

                    // Write data
                    for (ReportItem item : reportData) {
                        writer.write(item.getId() + ",");
                        writer.write(item.getCustomer() + ",");
                        writer.write(item.getDriver() + ",");
                        writer.write(item.getService() + ",");
                        writer.write(item.getAmount() + ",");
                        writer.write(item.getRating() + ",");
                        writer.write(item.getJarak() + ",");
                        Location location = item.getLocation();
                        if (location != null) {
                            writer.write(location.getFrom().getLat() + ",");
                            writer.write(location.getFrom().getLng() + ",");
                        } else {
                            writer.write(",");
                            writer.write(",");
                        }
                        writer.write(String.valueOf(item.getTimestamp()));
                        writer.newLine();
                    }

                    // Close the writer
                    writer.close();


                    // Inform the user that the report has been saved
                    Toast.makeText(this, "Report saved successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Inform the user that there was an error creating the file
                Toast.makeText(this, "Failed to create report file", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Inform the user that there was an error writing to the file
            Toast.makeText(this, "Error writing report file", Toast.LENGTH_SHORT).show();
        }
    }



//NOTIFICATIONS
    void getReport() {
        ChildEventListener bookChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<ReportItem> t = new GenericTypeIndicator<ReportItem>() {
                };
                ReportItem reportnotify = snapshot.getValue(t);
                if (reportnotify != null) {
                    report.add(reportnotify);
                    showNotifications(reportnotify.getContent());
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
        historyRef.addChildEventListener(bookChildEventListener);

    }
//END OF NOTIFICATIONS





    private void downloadFile(String fileName) {
        File reportFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        // Check if the file exists before proceeding with the download
        if (reportFile.exists()) {
            // Get the content URI for the file using FileProvider
            Uri fileUri = FileProvider.getUriForFile(this, "com.example.fixeridetest.fileprovider", reportFile);

            try {
                // Insert the file into the MediaStore Downloads directory
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                contentValues.put(MediaStore.Downloads.IS_PENDING, 1); // Mark the file as pending

                ContentResolver contentResolver = getContentResolver();
                Uri contentUri = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                }

                if (contentUri != null) {
                    // Open an OutputStream to the file content URI
                    OutputStream outputStream = contentResolver.openOutputStream(contentUri);

                    if (outputStream != null) {
                        // Copy the file to the OutputStream
                        FileInputStream inputStream = new FileInputStream(reportFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        // Close streams
                        inputStream.close();
                        outputStream.close();

                        // Update the file status to complete
                        contentValues.clear();
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
                        contentResolver.update(contentUri, contentValues, null, null);

                        Toast.makeText(this, "File saved to Downloads", Toast.LENGTH_SHORT).show();



                        showNotifications(fileName);



                        Intent exit = new Intent(Report.this, AdminDashboard.class);
                        startActivity(exit);
                    }
                    else {
                        Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Failed to insert file into MediaStore", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving file to Downloads", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Report file not found", Toast.LENGTH_SHORT).show();
        }
    }




    //NOTIFICATIONS
    private void showNotifications(
            String content
    ) {
        // Create an intent to open the downloads directory
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        intent.setDataAndType(uri, "*/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_IDS)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("REPORT DOWNLOADED")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if Android version is Oreo or higher, as notification channels are required from Oreo onward
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Report Notifications";
            String description = "Notifications for report alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_IDS, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(0, builder.build());
    }








}
