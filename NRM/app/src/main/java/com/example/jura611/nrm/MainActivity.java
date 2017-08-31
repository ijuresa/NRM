package com.example.jura611.nrm;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
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
    private Button gButtonStartScan, gButtonShowDetails, gButtonShowMap;
    private WifiManager mWifiManager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private List<ScanResult> mScanResults;
    WifiInfo wifiInfo;
    private int position;

    Timer timer;
    TimerTask timerTask;

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

        registerReceiver(mWifiScanReceiver, intentFilter);

        // Start initial SCAN
        mWifiManager.startScan();

        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        gButtonStartScan = (Button)findViewById(R.id.btnStart);
        gButtonShowDetails = (Button)findViewById(R.id.btnShowDetails);
        gButtonShowMap = (Button)findViewById(R.id.btnShowMap);

        // Set listener
        gButtonStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When starting, check if mobile is NOT connected to the WIFI
                if(!isConnectedWifi()) {
                    Toast.makeText(MainActivity.this, "Connect to Wifi", Toast.LENGTH_SHORT).show();
                }

                // Start Alarm Manager for scanning
                else {
                    startTimer();
                }
            }
        });

        gButtonShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if results are empty -> Broadcast receiver didin't fire
                if(mScanResults == null) {
                    return; // Don't do anything
                }
                // Start new Activity
                Intent _intent = new Intent(MainActivity.this, MapActivity.class);
                MainActivity.this.startActivity(_intent);
            }
        });

        gButtonShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedWifi()) {     //!< WIFI is connected

                    // Create popup to show details of the WIFI network
                    getConnectedWifiInfo();

                    // When Broadcast receiver didn't fire
                    if(mScanResults == null) {

                    }
                    else {
                        String ssid =  null;
                        for(position = 0; position < mScanResults.size(); position ++) {
                            ssid = getWifiSSID(mWifiManager);

                            if(mScanResults.get(position).SSID.equals(ssid)) {
                                Log.d(wifiInfo.getBSSID() + " = " + mScanResults.get(position).SSID,
                                        "ono");
                                break;
                            }
                        }
                        WifiDialog lDialog = new WifiDialog(MainActivity.this,
                                mScanResults.get(position).toString());
                        lDialog.showDialog();
                    }
                }
            }
        });
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
                        mWifiManager.startScan();
                    }
                });
            }
        };
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override

        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResults = mWifiManager.getScanResults();

                Log.d(String.valueOf(mScanResults.size()), "Size of and array");

                for(int i = 0; i < mScanResults.size(); i ++) {
                    Log.d(mScanResults.get(i).SSID, "id od " + i);
                }
            }
            Toast.makeText(context, "Usao u Broadcast", Toast.LENGTH_SHORT).show();
            Log.i("onReceive", " Usao");

            if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION )) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if((SupplicantState.isValidState(state)) && (state == SupplicantState.COMPLETED)) {
                    boolean changed = wifiChanged();
                }
            }
        }

    };

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

    private void getConnectedWifiInfo() {
        wifiInfo = mWifiManager.getConnectionInfo();
    }

    private String getWifiSSID(WifiManager _wifiManager) {
        WifiInfo _wifiInfo = _wifiManager.getConnectionInfo();

        return _wifiInfo.getSSID().replaceAll("\"","");
    }

    private boolean wifiChanged() {
        boolean _changed = false;

        String previousMacAddress = getMacAddress();


        return _changed;
    }

    private String getMacAddress() {
        String macAddress = null;


        return macAddress;
    }
}

