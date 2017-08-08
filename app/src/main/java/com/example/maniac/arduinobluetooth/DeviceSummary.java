package com.example.maniac.arduinobluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DeviceSummary extends AppCompatActivity {
    TextView displayDeviceName;
    TextView displayDeviceAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_summary);

        displayDeviceName = (TextView)findViewById(R.id.textView5);
        displayDeviceAdd = (TextView)findViewById(R.id.textView6);

        Intent receiveingIntent = this.getIntent();
        String fetchDeviceName = receiveingIntent.getExtras().getString("dName");
        String fetchDeviceAdd = receiveingIntent.getExtras().getString("dAdd");

        displayDeviceName.setText(fetchDeviceName);
        displayDeviceAdd.setText(fetchDeviceAdd);
    }
}
