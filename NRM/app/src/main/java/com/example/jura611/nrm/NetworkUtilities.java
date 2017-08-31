package com.example.jura611.nrm;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Jura611 on 30.8.2017..
 */

public class NetworkUtilities {
    public int getWifiSignalStregth(WifiManager _wifimanager) {
        WifiInfo wifiInfo = _wifimanager.getConnectionInfo();

        return wifiInfo.getRssi();
    }

    public String getWifiSsid(WifiManager _wifiManager) {
        WifiInfo wifiInfo = _wifiManager.getConnectionInfo();

        return wifiInfo.getSSID();
    }

    public String getWifiLinkSpeed(WifiManager _wifiManager) {
        WifiInfo wifiInfo = _wifiManager.getConnectionInfo();

        int linkSpeed = wifiInfo.getLinkSpeed();

        return (linkSpeed + wifiInfo.LINK_SPEED_UNITS);
    }
}
