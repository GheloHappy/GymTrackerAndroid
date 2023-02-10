package com.happy.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    Button btnRegister, btnLogin;
    TextView tvError;
    ProgressBar progressBar;
    TextInputEditText tietEmail, tietPass;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        btnRegister = findViewById(R.id.register);
        btnLogin = findViewById(R.id.submit);
        tietEmail = findViewById(R.id.email);
        tietPass = findViewById(R.id.password);
        tvError = findViewById(R.id.error);
        progressBar = findViewById(R.id.loading);

        btnLogin.setOnClickListener(v -> {
            tvError.setVisibility(View.GONE );
            progressBar.setVisibility(View.VISIBLE);
            email = String.valueOf(tietEmail.getText());
            pass = String.valueOf(tietPass.getText());

            String email = tietEmail.getText().toString().trim();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Please fill all fields");
                progressBar.setVisibility(View.GONE);
            } else {
                if (email.matches(emailPattern) && email != "") {
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = "http://192.168.1.64:4950/login";

                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("email", email);
                        requestBody.put("password", pass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
                        progressBar.setVisibility(View.GONE);
                        if (response.optString("message").equals("success")) {
                            //put user credentials in local storage
                            editor.putString("Email", email);
                            editor.putString("Password", pass);
                            editor.putLong("ExpirationTime", System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 15));
                            editor.apply();

                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else {
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText(response.optString("result"));
                        }
                    }, error -> {
                        System.out.println(error.toString());
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Network error");
                        progressBar.setVisibility(View.GONE);
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };
                    queue.add(jsonObjectRequest);
                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Invalid email address");
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });
    }
}