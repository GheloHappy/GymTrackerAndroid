package com.happy.gymtracker.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    public void SaveUser(Context context, String email, String pass) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginCredentials", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Email", email);
        editor.putString("Password", pass);
        editor.putLong("ExpirationTime", System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 15));
        editor.apply();
    }

    public boolean RetrieveUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginCredentials", context.MODE_PRIVATE);
        String email = sharedPreferences.getString("Email", "");
        long expirationTime = sharedPreferences.getLong("ExpirationTime", 0);

        if(System.currentTimeMillis() > expirationTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("Email");
            editor.remove("Password");
            editor.remove("ExpirationTime");
            editor.apply();
        } else{
            if (email != "") {
                return true;
            }
        }
        return false;
    }
}
