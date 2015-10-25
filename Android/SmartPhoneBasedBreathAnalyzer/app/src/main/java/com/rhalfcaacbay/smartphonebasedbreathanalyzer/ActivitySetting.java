package com.rhalfcaacbay.smartphonebasedbreathanalyzer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;

public class ActivitySetting extends AppCompatActivity  { //implements View.OnClickListener {

    BluetoothAdapter bluetoothAdapter;
    String bluetoothDeviceName = "";
    Spinner spinnerBluetoothPairedDevices;
    TextView textViewAnalogValueBacValue;
    //ListView listViewData;
    //Threads
    BluetoothConnectThread bluetoothConnectThread;
    BluetoothConnectedThread bluetoothConnectedThread;

    ArrayList<Float> readings = new ArrayList<>();


    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothStatus.DISCONNECTED: {
                    Dialog.show(ActivitySetting.this.getApplicationContext(), "Disconnected.");
//                    buttonBluetoothConnect.setText("Connect");
                    break;
                }
                case BluetoothStatus.CONNECTING: {
                    Dialog.show(ActivitySetting.this.getApplicationContext(), "Connecting...");
                    break;
                }
                case BluetoothStatus.CONNECTED: {
                    Dialog.show(ActivitySetting.this.getApplicationContext(), "Connected.");
//                    buttonBluetoothConnect.setText("Disconnect");
                    break;
                }
                case BluetoothStatus.DATA_RECEIVED: {
                    try {
                        final String value = (String) msg.obj;


                        //ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewData.getAdapter();
                        // call ArrayAdapter.add, no need to call notifyDataSetChanged as add does this
                        //if (adapter.getCount() > 20) {
                        //  adapter.remove(adapter.getItem(1));
                        //}

                        JSONObject json = new JSONObject(value);
                        JSONObject device = new JSONObject(json.getString("Device"));
                        String analogValue =  device.getString("analogValue");


                        float bac = Float.parseFloat(analogValue) / 5000;

                        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
                        textViewAnalogValueBacValue.setTypeface(Typeface.MONOSPACE);
                        textViewAnalogValueBacValue.setText(decimalFormat.format(bac).toString() + "%");
                        //adapter.add(analogValue);
                        //adapter.add(value);
                        // clear old title
                        //add_act_title.setText("");
                        //adapter.notifyDataSetChanged();

                        //listViewData.smoothScrollToPosition(adapter.getCount() - 1);

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
        setContentView(R.layout.layout_activity_setting);


        spinnerBluetoothPairedDevices = (Spinner) findViewById(R.id.spinnerBluetoothPairedDevices);
        textViewAnalogValueBacValue = (TextView) findViewById(R.id.textViewAnalogValueBacValue);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if( bundle != null)
        {
            bluetoothDeviceName = (String) bundle.get("bluetoothDeviceName");
        } else {
            onDestroy();
        }

//        listViewData = (ListView) findViewById(R.id.listViewData);
//
//        ArrayList<String> listViewDataItems = new ArrayList<String>();
//        listViewDataItems.add("Start");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, listViewDataItems);
//        listViewData.setAdapter(adapter);


        connect();
    }

    @Override
    protected void onDestroy() {

        if (this.bluetoothConnectedThread != null) {
            this.bluetoothConnectedThread.cancel();
            handler.obtainMessage(BluetoothStatus.DISCONNECTED, null).sendToTarget();
        }
        if (this.bluetoothConnectThread != null) {
            this.bluetoothConnectThread.cancel();
        }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void connect() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            String deviceAddress = bluetoothDevice.getAddress();
            if (deviceAddress.equals(bluetoothDeviceName)) {
                bluetoothConnectThread = new BluetoothConnectThread(bluetoothDevice, bluetoothAdapter, handler);
                bluetoothConnectThread.start();
                this.bluetoothConnectedThread = bluetoothConnectThread.bluetoothConnectedThread;
                handler.obtainMessage(BluetoothStatus.CONNECTING, null).sendToTarget();
                return;
            }
        }

    }

}
