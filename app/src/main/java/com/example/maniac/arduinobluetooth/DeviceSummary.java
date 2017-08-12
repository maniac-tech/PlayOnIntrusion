package com.example.maniac.arduinobluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class DeviceSummary extends AppCompatActivity {

    //defining Bluetooth Adapter:
    //BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    //BluetoothDevice device = adapter.getRemoteDevice(address);

    BluetoothAdapter adapter ;
    BluetoothDevice device ;
    BluetoothSocket tempmmSocket;


    public DeviceSummary(){
        MainActivity ma = new MainActivity();
        adapter = ma.adapter;
        device = ma.device;
        ConnectThread ct = new ConnectThread(device);
        //tempmmSocket = ct.mmSocket;
    }

    VideoView videoView ;

    String address = "00:21:13:01:45:9C";


    private static final String TAG = "MY_APP_DEBUG_TAG";

    private Handler mHandler; // handler that gets info from Bluetooth service
    //String address = "75:C1:D0:6E:D4:6C";  //Artis
    //String address = "C0:EE:FB:55:E4:C5"; //ONEPLUS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_summary);

        videoView=(VideoView)findViewById(R.id.videoView);

        //specify the location of media file
        Uri uri= Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/WhatsApp/Media/WhatsApp Video/1.mp4");

        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.seekTo(25000);
        videoView.getDuration();

       // ConnectedThread data = new ConnectedThread();
    }

    public void establishClientConnection(View view){
        Log.d("Debug:","Cancelling Discovery before attempt connection");
        adapter.cancelDiscovery();
        Log.d("Debug","establishClientConnection complete");
        ConnectThread client = new ConnectThread(device);
        Log.d("Debug","ConnectThread object ready");
        client.run();
    }

}
