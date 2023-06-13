package com.happy.gymtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gym_tracker.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user_progress(id INTEGER PRIMARY KEY, program TEXT, dt TEXT, weight DOUBLE, weightType TEXT," +
                "reps DOUBLE, sets INTEGER, time DOUBLE, timeType TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user_progress");
    }

    public Boolean InsertProgress(String program, String date, Double weight, String weightType,
                                  Double reps, Integer sets, Double time, String timeType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("program", program);
        contentValues.put("dt", date == null ? null : date);
        contentValues.put("weight", weight == null ? null : weight);
        contentValues.put("weightType", weightType);
        contentValues.put("reps", reps == null ? null : reps);
        contentValues.put("sets", sets == null ? null : sets);
        contentValues.put("time", time == null ? null : time);
        contentValues.put("timeType", timeType);
        long result = db.insert("user_progress", null, contentValues);
        if (result == -1) {
            return  false;
        }
        return true;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT program FROM user_progress WHERE id = 1", null);
        return  cursor;
    }
}
