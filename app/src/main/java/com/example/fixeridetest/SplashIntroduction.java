package com.example.fixeridetest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashIntroduction extends AppCompatActivity {

    private TextView typingTextView;
    private CharSequence textToType = "Seamless Vehicle Solutions";
    private int index = 0;
    private static final int TYPE_DELAY = 200; // Adjust the delay as needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_introduction);

        typingTextView = findViewById(R.id.textviewsplashint1);
        startTypingAnimation();

        Button startButton = findViewById(R.id.startbtn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add a delay before starting the WelcomeActivity (e.g., 1000 milliseconds = 1 second)
                int delayMillis = 1000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start WelcomeActivity
                        Intent intent = new Intent(SplashIntroduction.this, WelcomeActivity.class);
                        startActivity(intent);
                        // Finish the current activity
                        finish();
                    }
                }, delayMillis);
            }
        });
    }

    private void startTypingAnimation() {
        typingHandler.sendEmptyMessageDelayed(0, TYPE_DELAY);
    }

    private Handler typingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (index <= textToType.length()) {
                typingTextView.setText(textToType.subSequence(0, index++));
                typingHandler.sendEmptyMessageDelayed(0, TYPE_DELAY);
            }
            return true;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typingHandler.removeCallbacksAndMessages(null);
    }
}
