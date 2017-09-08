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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.IOException;

public class MapActivity3 extends FragmentActivity implements OnMapReadyCallback {
    String TAG = "MapActivity3: ";

    TileOverlay tileOverlay;

    GoogleMap mMap;
    LocationManager locationManager = null;
    boolean hasMoved = false;

    double longitudeGps = 0, latitudeGps = 0;

    WifiScanReceiver scanReceiver;
    ExportData exportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map3);

        // Initialize Location manager
        locationManager = (LocationManager) getApplicationContext().
                getSystemService(Context.LOCATION_SERVICE);

        // Check if location is enabled
        if (!isLocationEnabled()) {
            showAlert();
        }

        scanReceiver = WifiScanReceiver.get_wifiScanReceiver();
        exportData = ExportData.get_exportData();

        // Every 2 second
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
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

                    // Check if WIFI is OFF
                    if(!scanReceiver.isConnectedWifi(getApplicationContext())) {
                        Toast.makeText(MapActivity3.this, "Connect To Wifi", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Connect to Wifi");
                        return;
                    }

                    // Mover camera Once - TODO: Fix
                    if(!hasMoved) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 15));
                        hasMoved = true;
                    }

                    // Get projection
                    Log.d(TAG, "Projection " + mMap.getProjection().toScreenLocation(test));

                    // Get tile
                    Tile tile = tileProvider.getTile(mMap.getProjection().toScreenLocation(test).x,
                            mMap.getProjection().toScreenLocation(test).y, 15);



                    // Get projection
                    Log.d(TAG, "Tile " + tile);

                    // Write to map
                    tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(tileProvider));

                    // Write data to csv
                    try {
                        exportData.writeRow(latitudeGps, longitudeGps, scanReceiver.getWifiSignalStrength(),
                                scanReceiver.getWifiSSID(), scanReceiver.getWifiBssid());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    final TileProvider tileProvider = new TileProvider() {
        @Override
        public Tile getTile(int x, int y, int zoom) {
            return null;
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d(TAG, "Max zoom lvl: " + mMap.getMaxZoomLevel());
        Log.d(TAG, "Min zoom lvl: " + mMap.getMinZoomLevel());
    }
}
