package com.medroso.smartphonebasedbreathanalyzer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rhalfcaacbay.smartphonebasedbreathanalyzer.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;

public class ActivityReader extends AppCompatActivity {


    Boolean isStarted = false;
    Button buttonStartTest;
    ProgressBar progressBar;
    TextView textViewCountValue;

    BluetoothAdapter bluetoothAdapter;
    String bluetoothDeviceName = "";
    Spinner spinnerBluetoothPairedDevices;
    TextView textViewBacValueAverage,textViewBacValue;
    //ListView listViewData;
    //Threads
    BluetoothConnectThread bluetoothConnectThread;
    BluetoothConnectedThread bluetoothConnectedThread;

    /*
        Variables
    */
    float offset = 0.00f;
    //    float ratio = 0.0002f * 2.2f;
    float ratio = 0.0002f;


    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothStatus.DISCONNECTED: {
                    Dialog.show(getApplicationContext(), "Disconnected...");
                    break;
                }
                case BluetoothStatus.CONNECTING: {
                    Dialog.show(getApplicationContext(), "Connecting...");
                    break;
                }
                case BluetoothStatus.CONNECTING_FAILED: {
                    Dialog.show(getApplicationContext(), "Connecting Failed...");
                    finish();
                    break;
                }
                case BluetoothStatus.CONNECTED: {
                    Dialog.show(getApplicationContext(), "Connected...");
                    buttonStartTest.setEnabled(true);
                    break;
                }
                case BluetoothStatus.DATA_RECEIVED: {
                    if(isStarted == false) {
                        return;
                    }
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


                        float bac = offset + (Float.parseFloat(analogValue) * ratio);

                        if (bac < 0.034f){
                            bac = 0.00f;
                        }

                        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
                        textViewBacValue.setTypeface(Typeface.MONOSPACE);
                        textViewBacValue.setText(decimalFormat.format(bac).toString());
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
        setContentView(R.layout.layout_activity_reader);

        buttonStartTest = (Button) findViewById(R.id.buttonStartTest);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewCountValue = (TextView) findViewById(R.id.textViewCounterValue);

        spinnerBluetoothPairedDevices = (Spinner) findViewById(R.id.spinnerBluetoothPairedDevices);
        textViewBacValue = (TextView) findViewById(R.id.textViewBacValue);
        //textViewBacValueAverage = (TextView) findViewById(R.id.textViewBacValueAverage);

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



        buttonStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    textViewBacValue.setBackgroundColor(Color.TRANSPARENT);
                    textViewBacValue.setText("0.0000");
                    isStarted = true;

                    final Thread thread = new Thread() {
                        @Override
                        public void run() {
                            int count = 0;
                            final ArrayList<Double> reading = new ArrayList<>();

                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                            try {
                                this.sleep(1000);
                            } catch (Exception exception) {

                            }
                            while (count < 3000) {
                                count++;
                                final int finalCountProgressBar = count;
                                final double finalCountValue= count / 1000f;

                                String data = textViewBacValue.getText().toString();
                                double bac = Double.parseDouble(data.substring(0,data.length()));
                                reading.add(bac);
                                Log.i("bac=", Double.toString(bac));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
                                        textViewCountValue.setText(decimalFormat.format(finalCountValue));

                                        int progress = (finalCountProgressBar / 3000) * 100;
                                        progressBar.setMax(100);
                                        progressBar.setProgress(progress);
                                    }
                                });

                                try {
                                    this.sleep(1);
                                } catch (Exception exception) {

                                }

                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    double average = 0;
                                    for (int index = 0; index < reading.size(); index++) {
                                        average = average +  reading.get(index);
                                    }
                                    average = average / reading.size();

                                    DecimalFormat decimalFormat = new DecimalFormat("0.0000");
                                    textViewBacValue.setText(decimalFormat.format(average));
                                    textViewBacValue.setBackgroundColor(Color.GREEN);
                                    isStarted = false;
                                }
                            });

                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                        }
                    };
                    thread.start();

                }catch (Exception exception) {

                }
            }
        });

        connect();
    }



    @Override
    protected void onPause() {

        try {
            this.bluetoothConnectedThread.cancel();
        }catch (Exception exception) {

        }

        try {
            this.bluetoothConnectThread.cancel();
        }catch (Exception exception) {

        }


        try {
            for(int count = 0; count < 2000; count++) {
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        super.onPause();
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
            Intent intent = new Intent(this,ActivitySetting.class);
            intent.putExtra("offset",offset);
            intent.putExtra("ratio", ratio);
            int requestCode = RESULT_OK;
            startActivityForResult(intent, requestCode);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //in fragment class callback
        if (requestCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                offset = data.getExtras().getFloat("offset");
                ratio = data.getExtras().getFloat("ratio");
                connect();
            }
        }
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
