package com.happy.gymtracker.home;

import static com.android.volley.VolleyLog.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.happy.gymtracker.Login;
import com.happy.gymtracker.MainActivity;
import com.happy.gymtracker.R;
import com.happy.gymtracker.database.DbHelper;
import com.happy.gymtracker.tracker.TrackerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    Button btnSave;
    EditText etProgram, etDate, etWeight,etReps ,etSets, etTime;
    Spinner spinWeightType, spinTime;

    String program, date, weightType ,timeType;
    Double weight, reps;
    Integer sets;
    Double time;

    DbHelper dbHelper;
    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new DbHelper(getContext());

        etProgram = view.findViewById(R.id.etProgram);
        etDate = view.findViewById(R.id.etDate);
        etWeight = view.findViewById(R.id.etWeight);
        etReps = view.findViewById(R.id.etReps);
        etSets = view.findViewById(R.id.etSets);
        etTime = view.findViewById(R.id.etTime);
        spinWeightType = view.findViewById(R.id.spinWeightType);
        spinTime = view.findViewById(R.id.spinTime);
        btnSave = view.findViewById(R.id.btnSave);


        etDate.setText(GetCurrentDate());
        SetSpinnerValues(spinWeightType, spinTime);

        etDate.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int inputLength;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting) {
                    return;
                }
                inputLength = s.length();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) {
                    return;
                }
                int newLength = s.length();
                String digits = s.toString().replaceAll("[^\\d]", "");
                if (newLength != inputLength && digits.length() < 8) {
                    isFormatting = true;
                    if (digits.length() >= 4) {
                        if (digits.length() == 4 || digits.charAt(4) != '-') {
                            etDate.setText(String.format("%s-%s", digits.substring(0, 4), digits.substring(4)));
                            etDate.setSelection(etDate.getText().length());
                        }
                    }
                    if (digits.length() >= 7) {
                        if (digits.length() == 7 || digits.charAt(7) != '-') {
                            etDate.setText(String.format("%s-%s-%s", digits.substring(0, 4), digits.substring(4, 6), digits.substring(6)));
                            etDate.setSelection(etDate.getText().length());
                        }
                    }
                    isFormatting = false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }
                if (s.length() > 10) {
                    isFormatting = true;
                    s.delete(10, s.length());
                    isFormatting = false;
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            if (areFieldsEmpty(etProgram, etDate, etWeight, etReps, etSets)) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    program = etProgram.getText().toString();
                    date = etDate.getText().toString();
                    weight = Double.parseDouble(etWeight.getText().toString());
                    weightType = spinWeightType.getSelectedItem().toString();
                    reps = Double.parseDouble(etReps.getText().toString());
                    sets = Integer.parseInt(etSets.getText().toString());
                    String timeChck = etTime.getText().toString();
                    if (!timeChck.isEmpty()) {
                        time = Double.parseDouble(etTime.getText().toString());
                    }
                    timeType = spinTime.getSelectedItem().toString();

                        if(dbHelper.InsertProgress(program, date, weight, weightType, reps, sets, time, timeType)){
                            //Toast.makeText(getActivity(), "Workout progress saved", Toast.LENGTH_SHORT).show();
                            GridLayout layout = view.findViewById(R.id.gridLayoutHome);
                            clearAllEditTextFields(layout);
                            etProgram.requestFocus();

                            dbHelper = new DbHelper(getActivity());
                            cursor = dbHelper.getData();

                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex("program");
                                if (columnIndex != -1) {
                                    String columnValue = cursor.getString(columnIndex);
                                    Toast.makeText(getActivity(), columnValue, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), columnIndex, Toast.LENGTH_SHORT).show();
                                }
                                System.out.println(columnIndex);
                            } else {

                            }

                        } else {
                            Toast.makeText(getActivity(), "Failed to add new workout", Toast.LENGTH_SHORT).show();
                        }
                } catch (Exception e) {
                    System.out.println(e.getMessage() + " : btnSaveOnclik");
                }
            }
        });

        return  view;
    }
    private boolean areFieldsEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    private String GetCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        return currentDate;
    }
    private void SetSpinnerValues(Spinner spinWeight, Spinner spinTime) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"lbs", "kg"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinWeight.setAdapter(adapter);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"sec", "min", "hr"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTime.setAdapter(timeAdapter);
    }

    public void clearAllEditTextFields(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            } else if (view instanceof ViewGroup) {
                clearAllEditTextFields((ViewGroup) view);
            }
            etDate.setText(GetCurrentDate());
        }
    }
}