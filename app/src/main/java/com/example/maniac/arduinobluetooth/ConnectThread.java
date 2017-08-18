package com.example.maniac.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by maniac on 8/12/2017.
 */

class ConnectThread extends Thread {

    //Bluetooth variables:
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket mmSocket;
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
        // Always cancel discovery because it will slow down a connection
        mBluetoothAdapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a successful connection or an exception
            Log.i("Debug","Connecting to socket...");
            mmSocket.connect();
            //startActivity(dataActivity);

        } catch (IOException e) {
            Log.e("Debug", e.toString());
            //  e.printStackTrace();

            try {
                Log.i("Debug","Trying fallback...");
                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                mmSocket.connect();
                Log.i("Debug","Connected");
                //startActivity(dataActivity);

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
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.

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