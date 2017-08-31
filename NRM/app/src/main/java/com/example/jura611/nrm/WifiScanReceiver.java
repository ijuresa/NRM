package com.example.jura611.nrm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;

/**
 * Created by Jura611 on 16.8.2017..
 */

public class WifiScanReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            mScanResults = mWifiManager.getScanResults();

            Log.d(String.valueOf(mScanResults.size()), "Size of and array");

            for(int i = 0; i < mScanResults.size(); i ++) {
                Log.d(mScanResults.get(i).SSID, "id od " + i);
            }
        }
        Toast.makeText(context, "Usao u Broadcast", Toast.LENGTH_LONG).show();

        if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION )) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if((SupplicantState.isValidState(state)) && (state == SupplicantState.COMPLETED)) {
                boolean changed = wifiChanged();
            }
        }
        */
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

        }

        //
        if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {

        }

    }
}
