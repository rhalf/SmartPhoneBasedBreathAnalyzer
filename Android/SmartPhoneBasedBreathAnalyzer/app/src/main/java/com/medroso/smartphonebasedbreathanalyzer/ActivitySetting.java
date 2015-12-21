package com.medroso.smartphonebasedbreathanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rhalfcaacbay.smartphonebasedbreathanalyzer.R;

public class ActivitySetting extends AppCompatActivity {


     Intent intent;

    EditText editTextOffSet, editTextRatio;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_setting);

        intent =  this.getIntent();
        Bundle bundle = intent.getExtras();


        float offset = bundle.getFloat("offset");
        float ratio = bundle.getFloat("ratio");

        editTextOffSet = (EditText) findViewById(R.id.editTextOffSet);
        editTextRatio = (EditText) findViewById(R.id.editTextRatio);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float offset = Float.parseFloat(editTextOffSet.getText().toString());
                float ratio = Float.parseFloat(editTextRatio.getText().toString());

                intent.putExtra("offset", offset);
                intent.putExtra("ratio",ratio);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        editTextOffSet.setText(String.valueOf(offset));
        editTextRatio.setText(String.valueOf(ratio));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {

        } catch (Exception exception){

        }

    }
}
