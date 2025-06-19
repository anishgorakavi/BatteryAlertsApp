package com.anishgorakavi.batteralert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.NotificationCompat;

public class BatteryReceiver extends BroadcastReceiver {
    private static final String PREFS = "battery_alert_prefs";
    private static final String LAST_LEVEL = "last_level";
    private static final String CHANNEL_ID = "battery_alert_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            int percent = (int) ((level / (float) scale) * 100);

            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            int lastLevel = prefs.getInt(LAST_LEVEL, -1);
            System.out.println("Battery Last Level: " + lastLevel);
            System.out.println("Battery level: " + percent + "%");
            int threshold = prefs.getInt("threshold", 20);

            if ( percent <= threshold && !isCharging && (lastLevel == 0 || lastLevel - percent >= 5) ) {
                System.out.println("Battery level dropped to " + percent + "%, sending notification.");
                sendNotification(context, percent);
                prefs.edit().putInt(LAST_LEVEL, percent).apply();
            } else if (lastLevel == 0 || isCharging) {
                prefs.edit().putInt(LAST_LEVEL, percent).apply();
            }
        }
    }

    private void sendNotification(Context context, int percent) {
        // Send ntfy POST request
        new Thread(() -> {
            try {
                String deviceName = Build.MODEL;
                String message = "CRITICAL: Device " + deviceName + "'s Battery at " + percent + "%";
                URL url = new URL("http://192.168.1.67/batteryalerts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(message.getBytes("UTF-8"));
                }
                conn.getResponseCode(); // Trigger the request
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Battery Alerts", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Battery Alert")
                .setContentText("Battery dropped to " + percent + "%!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        manager.notify(1, builder.build());
    }
}
