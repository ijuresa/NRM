package com.example.jura611.nrm;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Jura611 on 30.8.2017..
 */

public class WifiServiceScanner extends Service {
    private WifiManager wifiManager;
    private WifiInfo wifiinfo;

    public void onCreate() {

    }

    public void onDestroy() {

    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
