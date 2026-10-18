package com.campus.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.campus.lostandfound.database.DatabaseHelper;

/**
 * Splash screen displayed at application launch.
 * Seeds initial demo campus data if the local SQLite database is empty.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Pre-populate realistic sample data if DB is empty
        new Thread(() -> {
            DatabaseHelper dbHelper = new DatabaseHelper(SplashActivity.this);
            dbHelper.seedInitialDataIfEmpty();
        }).start();

        // Navigate to MainActivity after brief delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
