package com.rhalfcaacbay.smartphonebasedbreathanalyzer;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Ralph on 8/8/2015.
 */
public class Dialog {
    public static void show(Context context, String message) {
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
    }
}
