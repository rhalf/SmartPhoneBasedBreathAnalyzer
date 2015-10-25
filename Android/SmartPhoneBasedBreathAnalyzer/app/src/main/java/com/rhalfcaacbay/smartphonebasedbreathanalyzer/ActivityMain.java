package com.rhalfcaacbay.smartphonebasedbreathanalyzer;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
        bluetoothAdapter.disable();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart: {
                Intent intentActivitySetting = new Intent(this, ActivitySetting.class);
                String[] device = spinnerBluetoothPairedDevices.getSelectedItem().toString().split(" - ");
                intentActivitySetting.putExtra("bluetoothDeviceName",device[0]);
                startActivity(intentActivitySetting);
            }
        }
    }


    public void setSpinner() {
        spinnerBluetoothPairedDevices = (Spinner) findViewById(R.id.spinnerBluetoothPairedDevices);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
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
