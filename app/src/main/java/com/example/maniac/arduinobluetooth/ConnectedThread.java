package com.example.maniac.arduinobluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by maniac on 8/12/2017.
 */

interface MessageConstants {
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;

    // ... (Add other message types here as needed.)
}

class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    private static final String TAG = "MY_APP_DEBUG_TAG";

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
            Log.d("ConnectedThread","InputStream created");
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
                Log.d("STR RECEIVED",strReceived);
                int index = strReceived.indexOf("-");
                //int tmp = Integer.parseInt(msgReceived);
                if (index!=-1){
                    Log.d("ALERT","FREEZE FOR 30SEC");
                    //videoView.start();
                }
                else{
                    Log.d("INPUT READ","DATA READ.");
                    //Log.d("INPUT READ",msgReceived);
                    //videoView.stopPlayback();
                }
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    /*
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);

            // Share the sent message with the UI activity.
            Message writtenMsg = mHandler.obtainMessage(
                    DeviceSummary.MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

            // Send a failure message back to the activity.
            Message writeErrorMsg =
                    mHandler.obtainMessage(DeviceSummary.MessageConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            mHandler.sendMessage(writeErrorMsg);
        }
    }
    */

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}