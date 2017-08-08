package com.example.maniac.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class DeviceSummary extends AppCompatActivity {
    TextView displayDeviceName;
    TextView displayDeviceAdd;

    //defining Bluetooth Adapter:
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    //String address = "00:21:13:01:45:9C";
    String address = "75:C1:D0:6E:D4:6C";
    //defining Bluetooth Device:
    BluetoothDevice device = adapter.getRemoteDevice(address);;

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

        establishClientConnection();
    }

    public void establishClientConnection(){
        Log.d("Debug:","Cancelling Discovery before attempt connection");
        adapter.cancelDiscovery();
        Log.d("Debug","establishClientConnection complete");
        ConnectThread client = new ConnectThread(device);
        Log.d("Debug","ConnectThread object ready");
        client.run();
    }

    private class ConnectThread extends Thread {

        //Creating BluetoothAdapter:
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            //Alternate UUID:7f224360-2994-11e7-9598-0800200c9b66; 8ce255c0-200a-11e0-ac64-0800200c9a66
            UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("Connect Thread-line 34", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            //Log.i("Debug", "BEGIN mConnectThread SocketType:" + mSocketType);
            //setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                Log.i("Debug","Connecting to socket...");
                mmSocket.connect();
            } catch (IOException e) {
                Log.e("Debug", e.toString());
                //  e.printStackTrace();

                try {
                    Log.i("Debug","Trying fallback...");
                    mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                    mmSocket.connect();
                    Log.i("Debug","Connected");
                } catch (Exception e2) {
                    Log.e("Debug", "Couldn't establish Bluetooth connection!");
                    try {
                        mmSocket.close();
                    } catch (IOException e3) {
                        //Log.e("Debug", "unable to close() " + mSocketType + " socket during connection failure", e3);
                    }
                    //connectionFailed();
                    return;
                }
                // return;
            }
        }

        // Closes the client socket and causes the thread to finish.


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Connect Thread - Line68", "Could not close the client socket", e);
            }
        }

//    private void showToast(String message) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//    }
    }
}
