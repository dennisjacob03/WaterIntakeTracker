
package com.example.waterintaketracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WaterTracker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "water_intake";
    private static final String COL_ID = "id";
    private static final String COL_AMOUNT = "amount_ml";
    private static final String COL_DATE = "date_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AMOUNT + " INTEGER NOT NULL, " +
                COL_DATE + " TEXT NOT NULL)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Database upgrade logic can be added here later.
    }

    // Add a water intake record
    public boolean addIntake(int amount, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_DATE, dateTime);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1;
    }

    // Get all water intake records
    public Cursor getAllIntakes() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " ORDER BY " + COL_ID + " DESC",
                null
        );
    }

    // Update an existing record
    public boolean updateIntake(int id, int amount,
                                String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_DATE, dateTime);

        int result = db.update(
                TABLE_NAME, values, COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }

    // Delete a record
    public boolean deleteIntake(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME, COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return result > 0;
    }
}
