package com.example.maniac.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class DeviceSummary extends AppCompatActivity {
    TextView displayDeviceName;
    TextView displayDeviceAdd;

    String address = "00:21:13:01:45:9C";

    //defining Bluetooth Adapter:
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    BluetoothDevice device = adapter.getRemoteDevice(address);
    private static final String TAG = "MY_APP_DEBUG_TAG";

    private Handler mHandler; // handler that gets info from Bluetooth service
    //String address = "75:C1:D0:6E:D4:6C";  //Artis
    //String address = "C0:EE:FB:55:E4:C5"; //ONEPLUS
    //defining Bluetooth Device:


    Intent dataActivity = new Intent(DeviceSummary.this, ReadData.class);

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

    public void establishClientConnection(View view){
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
            Log.d("Data Debug","ConnectedThread object will start");
            ConnectedThread dataLayegaYe = new ConnectedThread(mmSocket);
            Log.d("Data Debug","ConnectedThread object created");
            dataLayegaYe.run();
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
                Log.d("ConnectedThread","InputStrean created");
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
                Log.d("ConnectedThread","OutPut Stream created");
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
                    //Message readMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1,mmBuffer);
                    //readMsg.sendToTarget();

                    String strReceived = new String(mmBuffer, 0, numBytes);
                    final String msgReceived = String.valueOf(numBytes) +
                            " bytes received:\n"
                            + strReceived;
                    Log.d("ANSWER",msgReceived);
                    Log.d("INPUT READ","DATA READ.");
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
