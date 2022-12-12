package com.electricelk.speedotest1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;

public class MainActivity extends AppCompatActivity {

    private SpeedometerGauge speedometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SpeedoTest1", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        speedometer = (SpeedometerGauge) findViewById(R.id.speedometer);
        speedometer.setMaxSpeed(50);
        speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        speedometer.setMaxSpeed(40);
        speedometer.setMajorTickStep(5);
        speedometer.setMinorTicks(4);
        speedometer.addColoredRange(0, 19, Color.GREEN);
        speedometer.addColoredRange(20, 28, Color.YELLOW);
        speedometer.addColoredRange(29, 40, Color.RED);
        speedometer.setSpeed(0, 1000, 300);

        // Check if the ACCESS_FINE_LOCATION permission is granted
        Integer REQUEST_CODE = 1;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the ACCESS_FINE_LOCATION permission if it is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            // If the ACCESS_FINE_LOCATION permission is granted, start the location listener
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    TextView speed = findViewById(R.id.speed);
                    speedometer.setSpeed(
                            metersPerSecondToMilesPerHour(location.getSpeed()),
                            1000, 300);

                    speed.setText(String.valueOf(metersPerSecondToMilesPerHour(location.getSpeed())));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    int metersPerSecondToMilesPerHour(double metersPerSecond) {
        return (int) (metersPerSecond * 2.23694);
    }
}