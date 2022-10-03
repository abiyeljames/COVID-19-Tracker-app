package com.example.coronaupdate1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    int _splashTime = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Log.d(TAG, "onCreate: ");

        Thread splashScreenThread = new Thread(){
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(waited < _splashTime) {
                        sleep(100);
                        waited += 100;
                        Log.d(TAG, "run: waiting...");
                    }
                } catch (InterruptedException e){

                } finally {
                    finish();
                    // launching the MainActivity after the splash screen has launched
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };
        splashScreenThread.start();

    }
}