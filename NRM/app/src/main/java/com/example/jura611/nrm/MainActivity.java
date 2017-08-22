package com.example.jura611.nrm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button gButtonStartScan, gButtonShowDetails;
    private WifiManager mWifiManager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private List<ScanResult> mScanResults;
    WifiInfo wifiInfo;
    private int position;

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
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // Start scan
        mWifiManager.startScan();

        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        gButtonStartScan = (Button)findViewById(R.id.btnStart);
        gButtonShowDetails = (Button)findViewById(R.id.btnShowDetails);

        // Set listener
        gButtonStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("gButtonStartScan", "Usao je u onClick");
                // When starting, check if mobile is NOT connected to the WIFI
                if(!isConnectedWifi()) {
                    Log.i("gButtonStartScan", "Usao je u !isConnectedWifi");
                    // Create popup to offer WIFIs
                    final Dialog mDialog = new Dialog(MainActivity.this);
                    mDialog.setContentView(R.layout.custom_dialog);
                    mDialog.setTitle("test");

                    //mScanResults. = (ListView) mDialog.findViewById(R.id.dialogListView);
                }
            }
        });

        gButtonShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedWifi()) {     //!< WIFI is connected
                    Log.i("gButtonShowDetails", "Usao je u !isConnectedWifi");

                    // Create popup to show details of the WIFI network
                    getConnectedWifiInfo();

                    if(mScanResults == null) {

                    }
                    else {
                        for(position = 0; position < mScanResults.size(); position ++) {
                            Log.d(wifiInfo.getSSID() + " = " + mScanResults.get(position).SSID, "ono");
                            if(wifiInfo.getSSID() == mScanResults.get(position).SSID.toString()) {
                                Log.d(wifiInfo.getBSSID() + " = " + mScanResults.get(position).SSID, "ono");
                                break;
                            }
                        }
                        WifiDialog lDialog = new WifiDialog(MainActivity.this, mScanResults.get(position).toString());
                        lDialog.showDialog();
                    }
                }
            }
        });
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Usao je", "da");
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResults = mWifiManager.getScanResults();
                Log.d(String.valueOf(mScanResults.size()), "Size of and array");
                for(int i = 0; i < mScanResults.size(); i ++) {
                    Log.d(mScanResults.get(i).SSID, "id od " + i);
                }
            }
        }
    };

    private boolean isConnectedWifi() {
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
}

