package com.anishgorakavi.batteralert;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        // Register BatteryReceiver
        registerReceiver(new BatteryReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

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
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}