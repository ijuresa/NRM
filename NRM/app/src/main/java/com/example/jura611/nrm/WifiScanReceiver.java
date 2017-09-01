package com.example.jura611.nrm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Jura611 on 16.8.2017..
 */

public class WifiScanReceiver extends BroadcastReceiver {
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private NetworkInfo networkInfo;

    private List<ScanResult> scanResults;
    private ConnectivityManager connectivityManager;
    private String TAG = "WifiScanReceiver";


    public WifiScanReceiver(WifiManager _wifiManager) {
        this.wifiManager = _wifiManager;
    }

    public WifiScanReceiver() { }

    private static final WifiScanReceiver _wifiScanReceiver = new WifiScanReceiver();

    public static WifiScanReceiver get_wifiScanReceiver() { return _wifiScanReceiver; }



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, " Broadcast has fired");

        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            scanResults = wifiManager.getScanResults();

            /*
            Log.d(String.valueOf(scanResults.size()), "Size of and array");

            for(int i = 0; i < scanResults.size(); i ++) {
                Log.d(scanResults.get(i).SSID, "id od " + i);
            }
            */
        }

        //
        if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if((SupplicantState.isValidState(state)) && (state == SupplicantState.COMPLETED)) {
                boolean changed = wifiChanged();
            }
        }
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
    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    public void setWifiManager(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    public String getWifiSSID() {
        wifiInfo = wifiManager.getConnectionInfo();

        return wifiInfo.getSSID().replaceAll("\"","");
    }

    public int getWifiSignalStrength() {
        // Get % of Wifi Signal Strength
        return wifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
    }

    public boolean isConnectedWifi(Context context) {
        connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null) { //!< Connected to the internet
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) { //!< Connected to the WIFI
                return true;
            }
        }
        return false;
    }
}



