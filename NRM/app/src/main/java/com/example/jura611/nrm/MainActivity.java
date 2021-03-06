package com.example.jura611.nrm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.net.wifi.SupplicantState;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    private Button gButtonStartScan, gButtonShowDetails, gButtonShowMap, gButtonShowMap2,
                    gButtonShowMap3;

    private WifiManager mWifiManager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    WifiInfo wifiInfo;
    private int position;

    Timer timer;
    TimerTask timerTask;

    String TAG = "MainActivity";

    WifiScanReceiver scanReceiver = null;

    boolean started = false;

    // Intent for MapActivity
    Intent _intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate WifiManager
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        // Check if WIFI is enabled
        if(!mWifiManager.isWifiEnabled()) {
            Toast.makeText(MainActivity.this, "Enabling Wifi", Toast.LENGTH_SHORT).show();
            // If not, enable
            mWifiManager.setWifiEnabled(true);
        }

        // Register receiver
        IntentFilter intentFilter = new IntentFilter();

        // This will trigger when BSSID will be changed
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

        // This will trigger on first scan so we can find data about networks
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        //registerReceiver(mWifiScanReceiver, intentFilter);
        scanReceiver = WifiScanReceiver.get_wifiScanReceiver();
        scanReceiver.setWifiManager(mWifiManager);

        registerReceiver(scanReceiver, intentFilter);

        // Start initial SCAN
        mWifiManager.startScan();

        addListenerOnButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, " onResume");
        if(started) {
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, " onPause");

        if(started && (_intent == null)) {
            // Broadcast has started BUT Map is not opened
            Log.d(TAG, " onPause without MapActivity");
            stopTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, " onStop");
        if(started && (_intent == null)) {
            Log.d(TAG, " onStop without MapActivity");
            stopTimer();
        }
    }

    private void addListenerOnButtons() {
        gButtonStartScan = (Button)findViewById(R.id.btnStart);
        gButtonShowDetails = (Button)findViewById(R.id.btnShowDetails);
        gButtonShowMap = (Button)findViewById(R.id.btnShowMap);
        gButtonShowMap2 = (Button)findViewById(R.id.btnShowMap2);
        gButtonShowMap3 = (Button)findViewById(R.id.btnshowMap3);

        // Check if not connected turn off ShowDetails button
        if(!scanReceiver.isConnectedWifi(getApplicationContext())) {
            gButtonShowDetails.setEnabled(false);
        }

        // Set listener
        gButtonStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When starting, check if mobile is NOT connected to the WIFI
                if(!scanReceiver.isConnectedWifi(getApplicationContext())) {
                    showAlert();
                }

                // Start Alarm Manager for scanning
                else {
                    if(started == false) {
                        gButtonShowDetails.setEnabled(true);
                        gButtonStartScan.setText("Stop");
                        started = true;
                        startTimer();

                        Toast.makeText(MainActivity.this, "Scanning started", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    gButtonStartScan.setText("Start");
                    started = false;
                    gButtonShowDetails.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Scanning stopped", Toast.LENGTH_SHORT).show();

                    stopTimer();
                }
            }
        });

        gButtonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Don't enter if scanning is not started
                if(started) {
                    // Start new Activity
                    _intent = new Intent(MainActivity.this, MapActivity.class);
                    MainActivity.this.startActivity(_intent);
                }

                else Toast.makeText(MainActivity.this, "Start scanning", Toast.LENGTH_SHORT).show();
            }
        });

        gButtonShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scanReceiver.isConnectedWifi(getApplicationContext())) {  //!< WIFI is connected

                    // Create popup to show details of the WIFI network
                    getConnectedWifiInfo();

                    // When Broadcast receiver didn't fire
                    if(scanReceiver.getScanResults() == null) {

                    }
                    else {
                        String ssid =  null;
                        for(position = 0; position < scanReceiver.getScanResults().size();
                                position ++) {
                            ssid = scanReceiver.getWifiSSID();

                            if(scanReceiver.getScanResults().get(position).SSID.equals(ssid)) {
                                Log.d(wifiInfo.getBSSID() + " = " + scanReceiver.getScanResults()
                                                .get(position).SSID, "ono");
                                break;
                            }
                        }
                        WifiDialog lDialog = new WifiDialog(MainActivity.this,
                                scanReceiver.getScanResults().get(position).toString());
                        lDialog.showDialog();
                    }
                }
            }
        });

        gButtonShowMap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Don't enter if scanning is not started
                if(started) {
                    // Start new Activity
                    _intent = new Intent(MainActivity.this, MapActivity2.class);
                    MainActivity.this.startActivity(_intent);
                }

                else Toast.makeText(MainActivity.this, "Start scanning", Toast.LENGTH_SHORT).show();
            }
        });

        gButtonShowMap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Don't enter if scanning is not started
                if(started) {
                    // Start new Activity
                    _intent = new Intent(MainActivity.this, MapActivity3.class);
                    MainActivity.this.startActivity(_intent);
                }

                else Toast.makeText(MainActivity.this, "Start scanning", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Connect");
        dialog.setMessage("Connect Wifi to start scanning.");
        dialog.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent lIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
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

    // Use handler to run in TimerTask
    final static android.os.Handler handler = new android.os.Handler();

    public void startTimer() {
        timer = new Timer();

        // Initialize TimerTask
        initializeTimerTask();

        // After 2s Timer will run every other 2 sec
        timer.schedule(timerTask, 2000, 2000);
    }

    public void stopTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // We have started scanning and Wifi was ON, but Wifi is lost now and we are
                        // trying to scan - not good
                        if((started == true)
                                && (!scanReceiver.isConnectedWifi(getApplicationContext()))) {
                            // Set start button to stop
                            gButtonStartScan.setText("Stop");
                            // Disable ShowDetails button
                            gButtonShowDetails.setEnabled(false);

                            // Ask user to connect again to Wifi
                            showAlert();
                            return;
                        }

                        // else - just scan network
                        mWifiManager.startScan();
                    }
                });
            }
        };
    }

    /*
    private boolean isConnectedWifi() {
        connectivityManager = (ConnectivityManager) getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null) { //!< Connected to the internet
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) { //!< Connected to the WIFI
                return true;
            }
        }
        return false;
    }
*/
    private void getConnectedWifiInfo() {
        wifiInfo = mWifiManager.getConnectionInfo();
    }
}

