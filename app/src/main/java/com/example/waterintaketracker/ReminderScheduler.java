
package com.example.waterintaketracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class ReminderScheduler {

    private static final int REQUEST_CODE = 101;

    public static void scheduleReminder(
            Context context, long interval) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        Intent intent = new Intent(
                context, ReminderReceiver.class
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        if (alarmManager != null) {

            alarmManager.cancel(pendingIntent);

            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    interval,
                    pendingIntent
            );
        }
    }

    public static void cancelReminder(Context context) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        Intent intent = new Intent(
                context, ReminderReceiver.class
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        pendingIntent.cancel();
    }
}
