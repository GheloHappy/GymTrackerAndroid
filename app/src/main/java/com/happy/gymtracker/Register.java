package com.happy.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.happy.gymtracker.api.ApiVars;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputEditText tietName, tietEmail, tietPassword, tietConfirmPass;
    TextView tvError;
    ProgressBar progressBar;
    Button btnSubmit, btnLogin;
    String name, email, pass, passConfirm;
    CheckBox cbShowPass;
    ApiVars apiVars = new ApiVars();

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
        tietConfirmPass = findViewById(R.id.confirmPass);
        tvError = findViewById(R.id.error);
        btnSubmit = findViewById(R.id.submit);
        btnLogin = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);
        cbShowPass = findViewById(R.id.showPassword);

        btnSubmit.setOnClickListener(v -> {
            tvError.setVisibility(View.GONE );
            progressBar.setVisibility(View.VISIBLE);
            name = String.valueOf(tietName.getText()).trim();
            email = String.valueOf(tietEmail.getText()).trim();
            pass = String.valueOf(tietPassword.getText()).trim();
            passConfirm = String.valueOf(tietConfirmPass.getText()).trim();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)  || TextUtils.isEmpty(passConfirm) ) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Please fill all fields");
                progressBar.setVisibility(View.GONE);
            } else {
                if (email.matches(emailPattern) && email != ""){
                    if(pass.equals(passConfirm)) {
                        RequestQueue queue = Volley.newRequestQueue(this);
                        String url = apiVars.apiUrl+"/user";

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
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText("Network error");
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
                    } else {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Password does not match");
                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Invalid email address");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        cbShowPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tietPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tietConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                tietPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tietConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            tietPassword.setSelection(tietPassword.length());
            tietConfirmPass.setSelection(tietConfirmPass.length());
        });
    }
}