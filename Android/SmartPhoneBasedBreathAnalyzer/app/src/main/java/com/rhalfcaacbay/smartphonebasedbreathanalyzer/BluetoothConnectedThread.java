package com.rhalfcaacbay.smartphonebasedbreathanalyzer;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Ralph on 8/8/2015.
 */
public class BluetoothConnectedThread extends Thread {

    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Handler handler;

    private final BufferedReader bufferedReader;

    public BluetoothConnectedThread(BluetoothSocket socket, Handler handler) {
        this.handler = handler;
        bluetoothSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;


        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        inputStream = tmpIn;
        outputStream = tmpOut;

        InputStreamReader inputStreamReader = new InputStreamReader(tmpIn);
        bufferedReader = new BufferedReader(inputStreamReader);

    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()


        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                //bytes = inputStream.read(buffer);
                // Send the obtained bytes to the UI activity

//buffer = bufferedReader.readLine();

//String value = new String(buffer, "UTF-8");

                String value = bufferedReader.readLine();

                //String value = new String(buffer, "UTF-8");
                handler.obtainMessage(BluetoothStatus.DATA_RECEIVED, bytes, -1, value)
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {

        try {
            bluetoothSocket.close();
        } catch (IOException e) {
        }
    }


}
