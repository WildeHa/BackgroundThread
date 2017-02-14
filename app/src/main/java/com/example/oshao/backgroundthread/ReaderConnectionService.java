package com.example.oshao.backgroundthread;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Set;

import hk.lscm.bluetoothrfidreaderlib.ReturnData;
import hk.lscm.bluetoothrfidreaderlib.RfidReader;

/**
 * Created by oshao on 1/5/2017.
 */

public class ReaderConnectionService extends IntentService {

    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private RfidReader rfidReader;

    private LocalBroadcastManager localBroadcastManager;

    private int count = 0;

    public ReaderConnectionService() {
        super("ReaderConnectionService");
    }


    private final String ACTION_EPC_BACKGROUNDTHREAD = "com.example.oshao.backgroundthread.epc";
    private final String KEY_EPC_BACKGROUNDTHREAD = "com.example.oshao.backgroundthread.epc";


    @Override
    public void onCreate() {
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        if (getDeviceAddress() != null) {

            rfidReader = new RfidReader(getDeviceAddress(), handler, GlobalVariable.getContext());
            rfidReader.connect();

        }

        sendServiceStatus("Starting service");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RfidReader.READER_RESPONSE[] wa = RfidReader.READER_RESPONSE.values();

            switch (wa[msg.what]) {
                case SOCKET_CONNECTED:
                    rfidReader.startInventory(4, 1, 400);
                    Log.v("ReaderConnection", "Connected");
                    sendServiceStatus("Connected");
                    break;
                case EXTRACTED_DATA:
                    ReturnData data = (ReturnData) msg.obj;
                    if (data.BodyMessageList.size() > 0) {

                        String epc = data.BodyMessageList.get(0);
                        epc = epc.substring(4, epc.length());
                        Log.v("ReaderConnectionSe",""+epc);
                        Intent intent = new Intent(ACTION_EPC_BACKGROUNDTHREAD);
                        sendBroadcast(intent);
                    }

                    break;
                case SOCKET_CONNECT_FAIL:
                    break;
            }
        }
    };


    private void sendServiceStatus(String status) {

        Intent intent = new Intent(MainActivity.ACTION_TYPE_SERVICE);
        intent.putExtra("status", status);
        Log.v("ReaderConnection : ", "Sending Intent");
        sendBroadcast(intent);

    }

    private void sendReaderCount(String count) {
        Intent intent = new Intent(MainActivity.ACTION_READER_COUNT);
        intent.putExtra("count", count);
        sendBroadcast(intent);
    }

    private String getDeviceAddress() {

        String deviceAddress = null;
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                if (bluetoothDevice.getName().contains("HC-06") || bluetoothDevice.getName().contains("EX1432")) {

                    deviceAddress = bluetoothDevice.getAddress();
                    break;

                }
            }
        }

        return deviceAddress;
    }

    public void destroy() {

        if (rfidReader.isConnected()) {
            rfidReader.stopInventory();
            rfidReader.clearBufferedMessage();
            rfidReader.close();
        }

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        sendServiceStatus("Starting Service");

    }

}

