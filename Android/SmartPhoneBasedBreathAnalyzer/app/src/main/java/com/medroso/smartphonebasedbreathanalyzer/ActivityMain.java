package com.medroso.smartphonebasedbreathanalyzer;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.rhalfcaacbay.smartphonebasedbreathanalyzer.R;

import java.util.ArrayList;
import java.util.Set;


public class ActivityMain extends AppCompatActivity implements View.OnClickListener {


    Button buttonStart;
    BluetoothAdapter bluetoothAdapter;
    Spinner spinnerBluetoothPairedDevices;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);


        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(this);

        setSpinner();
    }

    @Override
    protected void onDestroy() {
        //bluetoothAdapter.disable();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart: {
                String[] device = spinnerBluetoothPairedDevices.getSelectedItem().toString().split(" - ");
                //device[0].equals("98:D3:31:B3:76:25") &&
                if(device[1].equals("HC-06")) {
                    Intent intentActivitySetting = new Intent(this, ActivityReader.class);
                    intentActivitySetting.putExtra("bluetoothDeviceName",device[0]);
                    startActivity(intentActivitySetting);
                } else {
                    Dialog.show(getApplicationContext(),"Does not support this device... Please try again.");
                }
            }
        }
    }


    public void setSpinner() {
        spinnerBluetoothPairedDevices = (Spinner) findViewById(R.id.spinnerBluetoothPairedDevices);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Does not support this type of bluetooth module.!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            try {
                Thread.sleep(1000);
            } catch (Exception exception) {
                Log.d("Error", exception.getMessage());
            }
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> arrayPairedDevices = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                arrayPairedDevices.add(device.getAddress() + " - " + device.getName());
            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arrayPairedDevices);
        spinnerBluetoothPairedDevices.setAdapter(arrayAdapter);
    }





}
