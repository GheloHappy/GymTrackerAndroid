package com.happy.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputEditText tietName, tietEmail, tietPassword;
    TextView tvError;
    ProgressBar progressBar;
    Button btnSubmit;
    String name, email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(R.layout.activity_register);

        tietName = findViewById(R.id.name);
        tietEmail = findViewById(R.id.email);
        tietPassword = findViewById(R.id.password);
        tvError = findViewById(R.id.error);
        btnSubmit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.loading);

        btnSubmit.setOnClickListener(v -> {
            tvError.setVisibility(View.GONE );
            progressBar.setVisibility(View.VISIBLE);
            name = String.valueOf(tietName.getText());
            email = String.valueOf(tietEmail.getText());
            pass = String.valueOf(tietPassword.getText());
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://192.168.1.64:4950/user";

            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("email", email);
                requestBody.put("password", pass);
                requestBody.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, response -> {
                progressBar.setVisibility(View.GONE);
                if(response.optString("message").equals("success")){
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(response.optString("message"));
                }
            }, error -> {
                System.out.println(error.toString());
                progressBar.setVisibility(View.GONE);
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
        });
    }
}