package com.example.jura611.nrm;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jura611 on 4.9.2017..
 */

public class ExportData {
    private String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private String fileName = "NRM_analysisData.csv";
    private String filePath = baseDir + File.separator + fileName;

    File f = new File(filePath);
    CSVWriter writer;

    private ExportData() throws IOException {
        if(f.exists() && !f.isDirectory()){
            FileWriter mFileWriter = new FileWriter(filePath, true);
            writer = new CSVWriter(mFileWriter);
        } else {
            writer = new CSVWriter(new FileWriter(filePath));
            setHeaders();
        }
    }


    private void setHeaders() throws IOException {
        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[] {"Time", "System Time", "Latitude", "Longitude", "Signal Strength", "SSID", "BSSID"});
        writer.writeAll(data);
        writer.close();
    }


    public void writeRow(double _latitude, double _longitude, double _signalStrength, String _ssid,
                         String _bssid) throws IOException {
        writer = new CSVWriter(new FileWriter(filePath, true));
        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[] {String.valueOf(Calendar.getInstance().getTime()),
                                String.valueOf(System.currentTimeMillis()), String.valueOf(_latitude),
                                String.valueOf(_longitude), String.valueOf(_signalStrength),
                                _ssid, _bssid});

        writer.writeAll(data);
        writer.close();
    }

    private static ExportData _exportData;
    static {
        try {
            _exportData = new ExportData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExportData get_exportData() { return _exportData; }
}

