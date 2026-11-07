
package com.example.waterintaketracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "WaterTrackerPrefs",
                        Context.MODE_PRIVATE
                );

        boolean enabled = preferences.getBoolean(
                "reminderEnabled", false
        );

        if (!enabled) {
            ReminderScheduler.cancelReminder(context);
            return;
        }

        int dailyGoal = preferences.getInt(
                "dailyGoal", 2000
        );

        DatabaseHelper dbHelper =
                new DatabaseHelper(context);

        int total = 0;

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        Cursor cursor = dbHelper.getAllIntakes();

        try {
            int amountIndex =
                    cursor.getColumnIndexOrThrow("amount_ml");

            int dateIndex =
                    cursor.getColumnIndexOrThrow("date_time");

            while (cursor.moveToNext()) {

                int amount = cursor.getInt(amountIndex);

                String dateTime =
                        cursor.getString(dateIndex);

                if (dateTime.startsWith(today)) {
                    total += amount;
                }
            }

        } finally {
            cursor.close();
        }

        if (total >= dailyGoal) {

            ReminderScheduler.cancelReminder(context);

        } else {

            NotificationHelper.showNotification(context);
        }
    }
}
