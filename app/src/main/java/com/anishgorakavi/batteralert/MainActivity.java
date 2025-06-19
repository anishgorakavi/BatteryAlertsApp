package com.anishgorakavi.batteralert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import  android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start BatteryService to ensure monitoring is active
        Intent serviceIntent = new Intent(this, BatteryService.class);
        startForegroundService(serviceIntent);

        // UI for threshold
        SeekBar seekBar = findViewById(R.id.thresholdSeekBar);
        TextView valueText = findViewById(R.id.thresholdValue);
        SharedPreferences prefs = getSharedPreferences("battery_alert_prefs", MODE_PRIVATE);
        int threshold = prefs.getInt("threshold", 20);
        seekBar.setProgress(threshold);
        valueText.setText(threshold + "%");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueText.setText(progress + "%");
                prefs.edit().putInt("threshold", progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Handle user input for device name
        TextView deviceNameText = findViewById(R.id.inputdevice);
        Button saveButton = findViewById(R.id.button);
        TextView verifiedname = findViewById(R.id.deviceverify);
        saveButton.setOnClickListener(v -> {
                    String deviceName = deviceNameText.getText().toString().trim();
                    if (!deviceName.isEmpty()) {
                        prefs.edit().putString("device_name", deviceName).apply();
                        Toast.makeText(this, "Device name saved: " + deviceName, Toast.LENGTH_SHORT).show();
                        verifiedname.setText("Device Name:" + prefs.getString("device_name", "Unknown Device"));
                    } else {
                        Toast.makeText(this, "Please enter a valid device name.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Show devicename

        verifiedname.setText("Device Name:" + prefs.getString("device_name", "Unknown Device"));

        TextView ntfyText = findViewById(R.id.ntfyname);
        Button ntfyButton = findViewById(R.id.ntfysubmit);
        ntfyText.setText(prefs.getString("ntfy_url", ""));
        ntfyButton.setOnClickListener(v -> {
            String ntfyName = ntfyText.getText().toString().trim();
            if (!ntfyName.isEmpty()) {
                prefs.edit().putString("ntfy_url", ntfyName).apply();
                Toast.makeText(this, "ntfy name saved: " + ntfyName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid ntfy name.", Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences.Editor editor = prefs.edit();
        boolean fun = editor.commit();
        Toast.makeText(this,String.valueOf(fun), Toast.LENGTH_SHORT).show();
    }
}