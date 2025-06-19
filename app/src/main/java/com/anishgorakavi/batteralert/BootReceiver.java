package com.anishgorakavi.batteralert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start BatteryService on boot
            Intent serviceIntent = new Intent(context, BatteryService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
