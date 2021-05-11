package com.example.batter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ImageView BatteryIndicatorView;
    private TextView BatteryPercentageView;
    private TextView BatteryStatusView;
    private TextView BatteryHealthView;
    private TextView BatteryTemperatureView;
    private TextView BatteryVoltageView;
    private ValueAnimator Animator;

    private BroadcastReceiver BatteryStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int Battery_level = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int currentBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                float Percent = ((float) currentBattery / (float) Battery_level) * 100f;
                int percent = Math.round(Percent);
                int drawableLevel = 100 * percent;
                BatteryPercentageView.setText(getString(R.string.battery_percentage, percent));

                int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN);

                if (batteryStatus == 2) {
                    if (Animator == null) {
                        createAnimator();
                    }
                    Animator.start();
                } else {
                    if (Animator != null) {
                        Animator.cancel();
                        Animator.removeAllUpdateListeners();
                        Animator = null;
                    }
                    BatteryIndicatorView.getDrawable().setLevel(drawableLevel);
                }
                BatteryStatusView.setText(getResources()
                        .getStringArray(R.array.battery_status)[batteryStatus]);

                int health =intent.getIntExtra(BatteryManager.EXTRA_HEALTH,
                        BatteryManager.BATTERY_HEALTH_UNKNOWN);
                BatteryHealthView.setText(getResources()
                        .getStringArray(R.array.battery_health)[health]);

                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                BatteryTemperatureView.setText(getString(R.string.battery_temperature, (float) temperature / 10f));

                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                BatteryVoltageView.setText(getString(R.string.battery_voltage, voltage));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatteryIndicatorView = findViewById(R.id.battery_indicator);
        BatteryPercentageView =  findViewById(R.id.battery_percentage);
        BatteryStatusView = findViewById(R.id.battery_status);
        BatteryHealthView = findViewById(R.id.battery_health);
        BatteryTemperatureView =  findViewById(R.id.battery_temperature);
        BatteryVoltageView = findViewById(R.id.battery_voltage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(BatteryStateReceiver, batteryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(BatteryStateReceiver);
    }

    private void createAnimator() {
        Animator = ValueAnimator.ofInt(0, 10000);
        Animator.setRepeatMode(ValueAnimator.REVERSE);
        Animator.setRepeatCount(ValueAnimator.INFINITE);
        Animator.setDuration(4000);
        Animator.setInterpolator(new DecelerateInterpolator());
        Animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                BatteryIndicatorView.getDrawable().setLevel(value);
            }
        });
    }
}