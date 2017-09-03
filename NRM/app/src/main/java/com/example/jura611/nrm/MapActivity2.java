package com.example.jura611.nrm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;


public class MapActivity2 extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    LocationManager locationManager = null;

    double longitudeGps = 0, latitudeGps = 0;
    int index = 0;

    WifiScanReceiver scanReceiver;
    String TAG = "MapActivity2: ";

    HeatmapTileProvider mProvider = null;
    TileOverlay mOverlay = null;
    ArrayList<LatLng> heatMapListData = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        // Get object
        scanReceiver = WifiScanReceiver.get_wifiScanReceiver();

        // Initialize Location manager
        locationManager = (LocationManager) getApplicationContext().
                getSystemService(Context.LOCATION_SERVICE);

        // Check if location is enabled
        if (!isLocationEnabled()) {
            showAlert();
        }

        // Every 2 second
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void addHeatmap() {

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

    private final android.location.LocationListener locationListener
            = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            latitudeGps = location.getLatitude();
            longitudeGps = location.getLongitude();

            Log.d(TAG,  Double.toString(latitudeGps) + " Latitude");
            Log.d(TAG,  Double.toString(longitudeGps) + " Longitude");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LatLng test = new LatLng(latitudeGps, longitudeGps);
                    heatMapListData.add(index, test);
                    index ++;

                    // Check if WIFI is OFF
                    if(!scanReceiver.isConnectedWifi(getApplicationContext())) {
                        Toast.makeText(MapActivity2.this, "Connect To Wifi", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Connect to Wifi");
                        return;
                    }

                    int signalPercentage = scanReceiver.getWifiSignalStrength();
                    Log.d(TAG,  signalPercentage + " Signal Strength");

                    // Draw
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(heatMapListData)
                            .build();

                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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
}
