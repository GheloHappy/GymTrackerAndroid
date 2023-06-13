package com.happy.gymtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.happy.gymtracker.auth.SharedPref;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        SharedPref sharedPref = new SharedPref();

        new Handler().postDelayed((Runnable) () -> {
            if(sharedPref.RetrieveUser(getApplicationContext())) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashScreen.this, Login.class));
                finish();
            }
        }, 1000);
    }
}
