package com.example.jura611.nrm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    LocationManager locationManager = null;

    double longitudeGps = 0, latitudeGps = 0;
    boolean started = false;

    WifiScanReceiver scanReceiver;
    ExportData exportData;

    String TAG = "MapActivity1: ";

    boolean hasMoved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        scanReceiver = WifiScanReceiver.get_wifiScanReceiver();
        exportData = ExportData.get_exportData();
        String BSSID = scanReceiver.getWifiSSID();
        Log.d(TAG, BSSID + " MapActivity");

        // Initialize Location manager
        locationManager = (LocationManager) getApplicationContext().
                getSystemService(Context.LOCATION_SERVICE);

        // Check if location is enabled
        if (!isLocationEnabled()) {
            showAlert();
        }

        // Start requesting location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Every 2 second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        started = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        started = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
        started = false;
    }

    private boolean isLocationEnabled() {
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location");
        dialog.setMessage("Turn ON Location to use this application");
        dialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent lIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(lIntent);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private int getSignalColour(int signalStrength) {
        int colour = 0;

        if(signalStrength >= 90) {
            colour = R.drawable.net_100;
        }

        else if(signalStrength < 90 && signalStrength >= 75) {
            colour = R.drawable.net_85;
        }

        else if(signalStrength < 75 && signalStrength >= 60) {
            colour = R.drawable.net_60;
        }

        else if(signalStrength < 60 && signalStrength >= 45) {
            colour = R.drawable.net_50;
        }

        else if(signalStrength < 45 && signalStrength >= 25) {
            colour = R.drawable.net_35;
        }

        else if(signalStrength < 25 && signalStrength >= 10) {
            colour = R.drawable.net_20;
        }

        else colour = R.drawable.net_0;

        return colour;
    }

    private final android.location.LocationListener locationListener
            = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(!started) {
                return;
            }
            latitudeGps = location.getLatitude();
            longitudeGps = location.getLongitude();

            Log.d(TAG,  Double.toString(latitudeGps) + " Latitude");
            Log.d(TAG,  Double.toString(longitudeGps) + " Longitude");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LatLng test = new LatLng(latitudeGps, longitudeGps);

                    // Check if WIFI is OFF
                    if(!scanReceiver.isConnectedWifi(getApplicationContext())) {
                        Toast.makeText(MapActivity.this, "Connect To Wifi", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Connect to Wifi");
                        return;
                    }

                    int signalPercentage = scanReceiver.getWifiSignalStrength();
                    Log.d(TAG,  signalPercentage + " Signal Strength");
                    // Toast.makeText(MapActivity.this, "Signal " + rssid, Toast.LENGTH_SHORT).show();

                    // Mover camera Once - TODO: Fix
                    if(!hasMoved) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 15));
                        hasMoved = true;
                    }

                    // Check Wifi Signal percentage and draw cube of that colour
                    int resourceToDraw = getSignalColour(signalPercentage);

                    GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                            .image(BitmapDescriptorFactory.fromResource(resourceToDraw))
                            .position(test, 5f);

                    // Write data to csv
                    try {
                        exportData.writeRow(latitudeGps, longitudeGps, scanReceiver.getWifiSignalStrength(),
                                scanReceiver.getWifiSSID(), scanReceiver.getWifiBssid());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMap.addGroundOverlay(groundOverlayOptions);
                }
            });
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
