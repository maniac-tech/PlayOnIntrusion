package com.example.maniac.arduinobluetooth;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Bluetooth variables:
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice device;

    private static final String TAG = "Debug";
    private Handler mHandler; // handler that gets info from Bluetooth service

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        managePermissions();
    }

    //Funtion responsible for checking Permissions on Runtime:
    public void managePermissions(){
        //Managing Bluetooth permissions on RunTime:
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        //Managing External Storage permissions on RunTime:
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //Function responsible for Attempting Discovery of nearby Bluetooth Devices:
    public void discoverDevices(View view){
        //Intent to capture states:
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);

        //Starting to discover:
        adapter.startDiscovery();
    }

    //Function responsible for establishing connection to nearby Bluetooth Devices:
    public void establishClientConnection(View view){

        Log.d("Debug:","Cancelling Discovery before attempt connection");

        adapter.cancelDiscovery();
        Log.d("Debug","establishClientConnection complete");

        Log.d("Debug","ConnectThread object ready");
/*
        ConnectThread client = new ConnectThread(device);
        client.run();
*/
        Intent switchScreen = new Intent(MainActivity.this,DeviceSummary.class);
        startActivity(switchScreen);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Log.d("Discovery:","ON");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Log.d("Discovery:","OFF");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d("Discovery:","Device Found!");
                showToast("Found device " + device.getName());
                adapter.cancelDiscovery();
                Log.d("Discovery has","Forced shut ");
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}