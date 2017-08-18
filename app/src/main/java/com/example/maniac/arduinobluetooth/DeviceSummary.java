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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class DeviceSummary extends AppCompatActivity {

    UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address = "00:21:13:01:45:9C"; //address of bluetooth module connected to arduino
    //String address = "75:C1:D0:6E:D4:6C";  //Artis
    //String address = "C0:EE:FB:55:E4:C5"; //ONEPLUS

    //Bluetooth variables:
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter() ;
    BluetoothDevice device = adapter.getRemoteDevice(address);
    BluetoothSocket tempmmSocket;

    int parentWidth=0;
    //UI variables:
    VideoView videoView ;

    Button loadStream;
    //Other variables
    int parentHeight=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_summary);

        loadStream = (Button)findViewById(R.id.button4);
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

        loadStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    tempmmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e){
                    Log.e("Connect Thread-line 34", "Socket's create() method failed", e);
                }
                getValues();
                videoView.getLayoutParams().height = parentHeight;
                videoView.getLayoutParams().width = parentWidth;
                videoView.requestLayout();
                Log.d("ConnectThread","Creating object");
                ConnectThread ct = new ConnectThread(device);
                Log.d("ConnectThread","Object created");
                Log.d("exp",ct.mmSocket.toString());
                ct.run();
                Log.d("ConnectThread","Function run complete");
                Log.d("ConnectedThread","Creating object");
                ConnectedThread data12 = new ConnectedThread(ct.mmSocket);
                data12.run(videoView);
                Log.d("ConnectedThread","Function run complete");
            }
        });


    }

    public void getValues(){
        ConstraintLayout parent = (ConstraintLayout)findViewById(R.id.constrainedLayout);
        parentHeight = parent.getHeight();
        parentWidth = parent.getWidth();
    }

}
