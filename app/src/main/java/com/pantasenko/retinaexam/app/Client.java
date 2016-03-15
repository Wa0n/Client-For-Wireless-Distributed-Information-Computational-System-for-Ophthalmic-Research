package com.pantasenko.retinaexam.app;

import android.os.Looper;
import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Vladimir Pantasenko
 */
public class Client implements Runnable {
    private static final String TAG = "MyLogs";
    private Socket connection;
    private String host = SettingsActivity.getServerHost();
    private int port = 8080;
    private DataOutputStream outputFileName;
    private DataOutputStream outputPatientName;
    private DataInputStream inputCommand;
    private boolean running = false;
    private ArrayList<CommandListener> listeners = new ArrayList<CommandListener>();

    public void addListener(CommandListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CommandListener listener) {
        listeners.remove(listener);
    }

    public void fireListeners() {
        for (CommandListener listener : listeners) {
            listener.onCommandPerformed();
        }
    }

    public void startClient() {
        this.running = true;
    }

    public void stopClient() {
        this.running = false;
        if (connection != null) {
            try {
                outputFileName.close();
                outputPatientName.close();
                inputCommand.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            while (running) {
                try {
                    connection = new Socket(InetAddress.getByName(host), port);
                    try {
                        outputFileName = new DataOutputStream(connection.getOutputStream());
                        outputPatientName = new DataOutputStream(connection.getOutputStream());
                        inputCommand = new DataInputStream(connection.getInputStream());
                        receiveCommand();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        outputFileName.close();
                        outputPatientName.close();
                        inputCommand.close();
                        connection.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receiveCommand() {
        try {
            String messageFromServer = inputCommand.readUTF();
            if (messageFromServer.equals("Take photo please")) {
                fireListeners();
                Log.d(TAG, "Message from server : " + messageFromServer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFileToServer(String filePath, String fileName, String patientName) {
        try {
            Log.d(TAG, "Opening file...");
            File file = new File(filePath);
            Log.d(TAG, "File is opened.");
            Log.d(TAG, "Create new stream...");
            FileInputStream inputFile = new FileInputStream(file);
            Log.d(TAG, "New input file from SD card stream is created.");
            int size = (int) file.length();
            byte[] buffer = new byte[size];

            outputFileName.writeUTF(fileName);
            outputFileName.flush();
            Log.d(TAG, "file name is sent.");

            if (patientName == null) {
                patientName = "Unnamed";
            }
            outputPatientName.writeUTF(patientName);
            outputPatientName.flush();
            Log.d(TAG, "patient name is sent.");

            DataOutputStream outputFile = new DataOutputStream(connection.getOutputStream());
            int receivedBytes = 0;
            while (true) {
                receivedBytes = inputFile.read(buffer);
                if (receivedBytes > 0) {
                    outputFile.write(buffer, 0, receivedBytes);
                    outputFile.flush();
                }
                if (receivedBytes == -1) {
                    Log.d(TAG, "File sent.");
                    break;
                }
            }
            inputFile.close();
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
