package com.example.rfidapp.model;

import android.bluetooth.BluetoothDevice;

public class BleDeviceModel {
    private BluetoothDevice device;
    private int rssi;

    public BleDeviceModel(BluetoothDevice bluetoothDevice, int i) {
        this.device = bluetoothDevice;
        this.rssi = i;
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    public int getRssi() {
        return this.rssi;
    }
}
