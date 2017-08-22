package com.example.jura611.nrm;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Jura611 on 22.8.2017..
 */

public class AdapterWifiInfo extends ArrayAdapter<String> {
    private Activity lActivity;
    private ArrayList<String> lWifiInfo;
    private static LayoutInflater lInflater = null;


    public AdapterWifiInfo(Activity activity, ArrayList<String> wifiInfo) {
        super(activity, R.layout.custom_dialog, wifiInfo);

        try {
            this.lActivity = activity;
            this.lWifiInfo = wifiInfo;

            lInflater = (LayoutInflater) lActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = lInflater.inflate(R.layout.custom_dialog, parent, false);

        return convertView;
    }

}
