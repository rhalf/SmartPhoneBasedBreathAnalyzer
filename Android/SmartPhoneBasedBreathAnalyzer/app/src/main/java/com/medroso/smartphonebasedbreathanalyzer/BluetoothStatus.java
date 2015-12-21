package com.medroso.smartphonebasedbreathanalyzer;

public class BluetoothStatus extends Enum {

    public static final int DISCONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;
    public static final int DATA_RECEIVED = 3;
    public static final int CONNECTING_FAILED = 4;


    public BluetoothStatus(int enumValue) {
        super(enumValue);

    }

}
