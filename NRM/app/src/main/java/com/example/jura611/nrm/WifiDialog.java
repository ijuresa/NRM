package com.example.jura611.nrm;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Created by Jura611 on 22.8.2017..
 */

public class WifiDialog {
    Activity lActivity;
    AlertDialog lDialog;
    AlertDialog.Builder lDialogBuilder;
    String lWifi;

    public WifiDialog(Activity _lActivity, String _lWifi) {
        this.lActivity = _lActivity;
        this.lWifi = _lWifi;

        lDialogBuilder = new AlertDialog.Builder(_lActivity);
    }

    public void showDialog() {
        lDialogBuilder.setTitle("Details");
        lDialogBuilder.setMessage(lWifi);

        lDialog = lDialogBuilder.create();
        lDialog.show();
    }
}
