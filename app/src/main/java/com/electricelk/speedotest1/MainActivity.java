package com.electricelk.speedotest1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;

import java.time.Duration;
import java.time.Instant;


public class MainActivity extends AppCompatActivity {

    private SpeedometerGauge speedometer;
    private WebView webView;

    private Instant lastUpdate = Instant.now();
    private double lastSpeed = 0;
    private double thisTrip = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/gauge.html");
//        webView.loadUrl("file:///android_asset/microtronics.html");

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
                    Instant thisUpdate = Instant.now();
                    TextView tripText = findViewById(R.id.tripDistance);

                    double thisSpeed = metersPerSecondToMilesPerHour(location.getSpeed());

                    double distance = Duration.between(lastUpdate, thisUpdate).toMillis()/1000f * lastSpeed * 0.00027777777777778;

//                    Log.d("SpeedoTest1", "Trip: " + thisTrip + " Distance: " + distance + " Speed: " + thisSpeed);

                    thisTrip += distance;
                    tripText.setText("Trip Distance: " + String.format("%.2f", thisTrip));

                    lastSpeed = thisSpeed;
                    lastUpdate = thisUpdate;

                    String speedText = String.format("%.2f", thisSpeed);
                    String jsUpdate = "javascript: drawChart(" + speedText + ");";
//                    String jsUpdate = "javascript: $(\"#GaugeMeter_1\").gaugeMeter({percent:" + speedText + "});";

                    webView.evaluateJavascript(jsUpdate, null);

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.1f, locationListener);


        }
    }

    int metersPerSecondToMilesPerHour(double metersPerSecond) {
        return (int) (metersPerSecond * 2.23694);
    }
}