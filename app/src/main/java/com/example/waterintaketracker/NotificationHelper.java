
package com.example.waterintaketracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "water_reminder";

    public static void createChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Water Drinking Reminders",
                            NotificationManager.IMPORTANCE_DEFAULT
                    );

            channel.setDescription(
                    "Reminders to drink water regularly"
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            manager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context) {

        createChannel(context);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Time to Drink Water!")
                        .setContentText(
                                "Stay hydrated! Drink some water to reach your daily goal."
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        if (manager != null) {
            try {
                manager.notify(1001, builder.build());
            } catch (SecurityException e) {
                // Notification permission is not available.
            }
        }
    }
}
