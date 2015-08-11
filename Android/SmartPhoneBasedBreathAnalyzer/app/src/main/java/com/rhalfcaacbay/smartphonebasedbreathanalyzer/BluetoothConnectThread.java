package com.rhalfcaacbay.smartphonebasedbreathanalyzer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Ralph on 8/8/2015.
 */
public class BluetoothConnectThread extends Thread {

    public BluetoothConnectedThread bluetoothConnectedThread;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;

    public BluetoothConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.bluetoothAdapter = bluetoothAdapter;
        this.handler = handler;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.d("e", e.toString());

        }
        bluetoothSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        this.bluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            bluetoothSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            Log.d("connectException", connectException.toString());

            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.d("closeException", closeException.toString());
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);

        handler.obtainMessage(BluetoothStatus.CONNECTED, null).sendToTarget();

        bluetoothConnectedThread = new BluetoothConnectedThread(bluetoothSocket, this.handler);
        bluetoothConnectedThread.start();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            //handler.obtainMessage(BluetoothStatus.DISCONNECTED, null).sendToTarget();
            bluetoothSocket.close();
        } catch (IOException e) {
        }
    }
}

