package com.example.fixeridetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fixeridetest.R;
import com.example.fixeridetest.TipsActivity;

public class TipsAccident extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_accident);

        Button backBtn = (Button) findViewById(R.id.Back_accTipbutton);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TipsAccident.this, TipsActivity.class);
                startActivity(intent);
            }
        });
    }
}
