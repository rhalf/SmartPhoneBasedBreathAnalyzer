package com.rhalfcaacbay.smartphonebasedbreathanalyzer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class ActivityMain extends AppCompatActivity implements View.OnClickListener {


    BluetoothAdapter bluetoothAdapter;
    Button buttonBluetoothConnect, buttonBluetoothScan;
    Spinner spinnerBluetoothPairedDevices;
    ListView listViewData;
    //Threads
    BluetoothConnectThread bluetoothConnectThread;
    BluetoothConnectedThread bluetoothConnectedThread;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothStatus.DISCONNECTED: {
                    Dialog.show(ActivityMain.this.getApplicationContext(), "Disconnected.");
                    buttonBluetoothConnect.setText("Connect");
                    break;
                }
                case BluetoothStatus.CONNECTING: {
                    Dialog.show(ActivityMain.this.getApplicationContext(), "Connecting...");
                    break;
                }
                case BluetoothStatus.CONNECTED: {
                    Dialog.show(ActivityMain.this.getApplicationContext(), "Connected.");
                    buttonBluetoothConnect.setText("Disconnect");
                    break;
                }
                case BluetoothStatus.DATA_RECEIVED: {
                    //Dialog.show(ActivityMain.this.getApplicationContext(), "Data Received.");

                    //Dialog.show(ActivityMain.this.getApplicationContext(),value);
                    final String value = (String) msg.obj;
                    try {
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewData.getAdapter();
                        // call ArrayAdapter.add, no need to call notifyDataSetChanged as add does this
                        if (adapter.getCount() > 20) {
                            adapter.remove(adapter.getItem(1));
                        }

                        adapter.add(value);
                        // clear old title
                        //add_act_title.setText("");
                        adapter.notifyDataSetChanged();

                        listViewData.smoothScrollToPosition(adapter.getCount() - 1);

                    } catch (Exception exception) {
                        Dialog.show(getApplicationContext(), exception.toString());
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        buttonBluetoothConnect = (Button) findViewById(R.id.buttonBluetoothConnect);
        buttonBluetoothConnect.setOnClickListener(this);

        buttonBluetoothScan = (Button) findViewById(R.id.buttonBluetoothScan);
        buttonBluetoothScan.setOnClickListener(this);

        spinnerBluetoothPairedDevices = (Spinner) findViewById(R.id.spinnerBluetoothPairedDevices);
        listViewData = (ListView) findViewById(R.id.listViewData);

        ArrayList<String> listViewDataItems = new ArrayList<String>();
        listViewDataItems.add("hi");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listViewDataItems);
        listViewData.setAdapter(adapter);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBluetoothConnect: {
                if (buttonBluetoothConnect.getText().equals("Connect")) {
                    String[] device = spinnerBluetoothPairedDevices.getSelectedItem().toString().split(" - ");

                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice bluetoothDevice : pairedDevices) {
                        String deviceAddress = bluetoothDevice.getAddress();
                        if (deviceAddress.equals(device[0])) {
                            bluetoothConnectThread = new BluetoothConnectThread(bluetoothDevice, bluetoothAdapter, handler);
                            bluetoothConnectThread.start();
                            this.bluetoothConnectedThread = bluetoothConnectThread.bluetoothConnectedThread;
                            handler.obtainMessage(BluetoothStatus.CONNECTING, null).sendToTarget();
                            return;
                        }
                    }
                } else {
                    if (this.bluetoothConnectedThread != null) {
                        this.bluetoothConnectedThread.cancel();
                    }
                    if (this.bluetoothConnectThread != null) {
                        this.bluetoothConnectThread.cancel();
                    }
                    handler.obtainMessage(BluetoothStatus.DISCONNECTED, null).sendToTarget();
                }
                break;
            }
            case R.id.buttonBluetoothScan :{

            }
            default: {

            }


        }
    }


}
