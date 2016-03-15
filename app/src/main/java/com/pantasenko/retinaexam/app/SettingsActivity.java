package com.pantasenko.retinaexam.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Vladimir Pantasenko
 */
public class SettingsActivity extends Activity {
    private EditText editTextPatientName;
    private EditText editTextServerHost;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button buttonQ100;
    private Button buttonQ90;
    private Button buttonQ80;
    private Button buttonQ70;
    private Button buttonQ60;
    static String patientName = null;
    static String serverHost = null;
    static int numberOfPhotos = 1;
    static int jpegQuality = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        editTextPatientName = (EditText) findViewById(R.id.editTextPatientName);
        editTextServerHost = (EditText) findViewById(R.id.editTextServerHost);
        if (patientName != null) {
            editTextPatientName.setText(patientName);
        }
        if (serverHost != null) {
            editTextServerHost.setText(serverHost);
        }
        button1 = (Button) findViewById(R.id.btnFrame1);
        button2 = (Button) findViewById(R.id.btnFrame2);
        button3 = (Button) findViewById(R.id.btnFrame3);
        buttonQ100 = (Button) findViewById(R.id.btnQuality100);
        buttonQ90 = (Button) findViewById(R.id.btnQuality90);
        buttonQ80 = (Button) findViewById(R.id.btnQuality80);
        buttonQ70 = (Button) findViewById(R.id.btnQuality70);
        buttonQ60 = (Button) findViewById(R.id.btnQuality60);
        View.OnClickListener oclBtnFrame = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setEnabled(true);
                button2.setEnabled(true);
                button3.setEnabled(true);
                switch (view.getId()) {
                    case R.id.btnFrame1:
                        numberOfPhotos = 1;
                        button1.setEnabled(false);
                        break;
                    case R.id.btnFrame2:
                        numberOfPhotos = 2;
                        button2.setEnabled(false);
                        break;
                    case R.id.btnFrame3:
                        numberOfPhotos = 3;
                        button3.setEnabled(false);
                        break;
                }
            }
        };

        View.OnClickListener oclBtnQuality = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonQ100.setEnabled(true);
                buttonQ90.setEnabled(true);
                buttonQ80.setEnabled(true);
                buttonQ70.setEnabled(true);
                buttonQ60.setEnabled(true);
                switch (view.getId()) {
                    case R.id.btnQuality100:
                        jpegQuality = 100;
                        buttonQ100.setEnabled(false);
                        break;
                    case R.id.btnQuality90:
                        jpegQuality = 90;
                        buttonQ90.setEnabled(false);
                        break;
                    case R.id.btnQuality80:
                        jpegQuality = 80;
                        buttonQ80.setEnabled(false);
                        break;
                    case R.id.btnQuality70:
                        jpegQuality = 70;
                        buttonQ70.setEnabled(false);
                        break;
                    case R.id.btnQuality60:
                        jpegQuality = 60;
                        buttonQ60.setEnabled(false);
                        break;
                }
            }
        };

        button1.setOnClickListener(oclBtnFrame);
        button2.setOnClickListener(oclBtnFrame);
        button3.setOnClickListener(oclBtnFrame);
        buttonQ100.setOnClickListener(oclBtnQuality);
        buttonQ90.setOnClickListener(oclBtnQuality);
        buttonQ80.setOnClickListener(oclBtnQuality);
        buttonQ70.setOnClickListener(oclBtnQuality);
        buttonQ60.setOnClickListener(oclBtnQuality);

        if (numberOfPhotos == 1) button1.setEnabled(false);
        if (numberOfPhotos == 2) button2.setEnabled(false);
        if (numberOfPhotos == 3) button3.setEnabled(false);

        if (jpegQuality == 100) buttonQ100.setEnabled(false);
        if (jpegQuality == 90) buttonQ90.setEnabled(false);
        if (jpegQuality == 80) buttonQ80.setEnabled(false);
        if (jpegQuality == 70) buttonQ70.setEnabled(false);
        if (jpegQuality == 60) buttonQ60.setEnabled(false);
    }

    public void onClickBtnApply(View v) {
        if (TextUtils.isEmpty(editTextPatientName.getText().toString())
                || TextUtils.isEmpty(editTextServerHost.getText().toString())) {
            Toast.makeText(this, "Enter NAME and/or IP-adress", Toast.LENGTH_SHORT).show();
            patientName = null;
            serverHost = null;
            return;
        }
        patientName = editTextPatientName.getText().toString();
        serverHost = editTextServerHost.getText().toString();
    }

    public static String getPatientName() {
        return patientName;
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public static int getJpegQuality() {
        return jpegQuality;
    }
}
