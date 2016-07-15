package br.com.jucemar_dimon.balizapp;

/**
 * Created by Jucemar on 11/07/2016.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ConnectionThread extends Thread {

    BluetoothSocket btSocket = null;
    BluetoothServerSocket btServerSocket = null;
    InputStream input = null;
    static OutputStream output = null;
    String btDevAddress = null;
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    boolean server;
    boolean running = false;

    public ConnectionThread() {

        this.server = true;
    }


    public ConnectionThread(String btDevAddress) {

        this.server = false;
        this.btDevAddress = btDevAddress;
    }


    public void run() {

        this.running = true;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (this.server) {


            try {


                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("Super Bluetooth", UUID.fromString(myUUID));
                btSocket = btServerSocket.accept();


                if (btSocket != null) {

                    btServerSocket.close();
                }

            } catch (IOException e) {


                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }


        } else {


            try {


                BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAddress);
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));


                btAdapter.cancelDiscovery();


                if (btSocket != null)
                    btSocket.connect();

            } catch (IOException e) {


                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }

        }


        if (btSocket != null) {


            toMainActivity("---S".getBytes());

            try {


                input = btSocket.getInputStream();
                output = btSocket.getOutputStream();


                final byte delimiter = 35;

                Boolean stopWorker = false;
                int readBufferPosition = 0;
                byte[] readBuffer = new byte[1024];

                while (true) {
                    int bytesAvailable = input.available();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        input.read(packetBytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                                toEstacionamentoActivity(data);
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                }


            } catch (IOException e) {


                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }

    }

    private void toMainActivity(byte[] data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        MainActivity.handler.sendMessage(message);
    }

    private void toEstacionamentoActivity(String data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        message.setData(bundle);
        EstacionarActivity.handler.sendMessage(message);
    }


    public void write(byte[] data) {

        if (output != null) {
            try {


                output.write(data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {


            toMainActivity("---N".getBytes());
        }
    }


    public void cancel() {

        try {

            running = false;
            btServerSocket.close();
            btSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }


    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String data = bundle.getString("data");
            try {
                output.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    };


}

