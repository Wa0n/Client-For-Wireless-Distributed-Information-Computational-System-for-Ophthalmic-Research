package com.pantasenko.retinaexam.app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vladimir Pantasenko
 */
public class CameraActivity extends Activity {
    private static final String TAG = "MyLogs";
    private Camera mCamera;
    private PreviewActivity mPreview;
    private Context mContext;
    private FrameLayout preview;
    private ImageButton captureButton;
    private int camCounter = 0;
    private int numberOfPhotos = SettingsActivity.getNumberOfPhotos();
    private String patientName = SettingsActivity.getPatientName();
    private Client client;
    private String filePath;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mContext = this;

        client = new Client();
        Thread clientThread = new Thread(client);
        clientThread.start();

        client.addListener(new CommandListener() {
            @Override
            public void onCommandPerformed() {
                takePhoto();
            }
        });

        captureButton = (ImageButton) findViewById(R.id.capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void takePhoto() {
        try {
            mCamera.autoFocus(
                    new Camera.AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            mCamera.takePicture(null, null, mPictureCallback);
                            captureButton.setEnabled(false);
                        }
                    }
            );
        } catch (Exception e) {
            mCamera.takePicture(null, null, mPictureCallback);
            captureButton.setEnabled(false);
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "Camera is not available (in use or does not exist)");
        }
        return c;
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mCamera.startPreview();
            File pictureFile = getOutputMediaFile();
            filePath = pictureFile.getPath();
            fileName = pictureFile.getName();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions.");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.flush();
                fos.close();
                Log.d(TAG, "Photo is saved on SD card.");
                Toast.makeText(mContext, "Photo saved to : " + filePath, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            try {
                client.sendFileToServer(filePath, fileName, patientName);
            } catch (Exception e) {
                Toast.makeText(mContext, "Server is not available.", Toast.LENGTH_SHORT).show();
            }

            try {
                camCounter++;
                if (camCounter < numberOfPhotos) {
                    mCamera.takePicture(null, null, mPictureCallback);
                } else {
                    camCounter = 0;
                    captureButton.setEnabled(true);
                }
            } catch (Exception ex) {
                Log.d(TAG, "Cannot enable burst mode.");
            }
        }
    };

    private File getOutputMediaFile() {
        if (patientName == null) {
            patientName = "Unnamed";
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                "RetinaExam" + File.separator + patientName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            preview.removeView(mPreview);
            mPreview = null;
            Log.d(TAG, "Camera is released.");
        } else {
            Log.d(TAG, "Camera is not release.");
        }
        client.stopClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
            mPreview = new PreviewActivity(this, mCamera);
            preview = (FrameLayout) findViewById(R.id.preview_layout);
            preview.addView(mPreview, 0);
            mCamera.startPreview();
            Log.d(TAG, "Camera is resumed.");
        } else {
            Log.d(TAG, "Camera is in use.");
        }
        client.startClient();
    }
}
