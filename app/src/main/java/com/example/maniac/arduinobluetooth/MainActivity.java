package com.example.maniac.arduinobluetooth;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Adapter;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //defining Bluetooth Adapter:
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    //defining Bluetooth Device:
    BluetoothDevice device;

    String arrayDeviceName [] = new String [10];
    String arrayDeviceAdd [] = new String [10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checking for Permission on RUNTIME:
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);



    }

    public void discoverDevices(View view){
        //Intent to capture states:
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);

        //Starting to discover:
        adapter.startDiscovery();

        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.listview,arrayDeviceName);
        ListView listView = (ListView) findViewById(R.id.listView);

        int i=0;
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                arrayDeviceName [i] = deviceName;
                arrayDeviceAdd [i] = deviceHardwareAddress;
                Log.d(deviceName,deviceHardwareAddress);
                i++;
            }
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Intent toConnect = new Intent(MainActivity.this,)
                establishClientConnection();
            }

        });
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

    public void sendData(View view){
        ConnectThread client = new ConnectThread(device); //creating an object of "ConnectThread" to access Socket detail, required for input in below class:
        ConnectedThread willSendData = new ConnectedThread(client.mmSocket);
        byte send [] = {1,2,3};
        willSendData.run();
//        willSendData.write(send);
    }

//    public void sendData(View view){
//        TextView txt = (TextView)findViewById(R.id.textView);
//        Log.d("Debug text",txt.getText().toString());
//    }
    //Managing Connections:

    private static final String TAG = "Debug";
    private Handler mHandler; // handler that gets info from Bluetooth service

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}