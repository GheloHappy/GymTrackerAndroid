package com.happy.gymtracker.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.happy.gymtracker.Login;
import com.happy.gymtracker.R;

public class ProfileFragment extends Fragment {
    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);


        btnLogout = view.findViewById(R.id.btnLogout);

       btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginCredentials", this.getActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("Email");
            editor.remove("Password");
            editor.remove("ExpirationTime");
            editor.apply();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        });

        return  view;
    }
}