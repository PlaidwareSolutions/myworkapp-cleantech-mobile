package com.example.rfidapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "BleItem")
public class BleItemEntity {
    private String deviceName;
    private String historyID;

    @PrimaryKey
    @NonNull
    private int id;
    private String macAddress;
    private int rssi;

    public BleItemEntity() {
    }

    public BleItemEntity(String str, int i, String str2) {
        this.deviceName = str;
        this.rssi = i;
        this.macAddress = str2;
    }

    public String getHistoryID() {
        return this.historyID;
    }

    public void setHistoryID(String str) {
        this.historyID = str;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String str) {
        this.deviceName = str;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int i) {
        this.rssi = i;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public void setMacAddress(String str) {
        this.macAddress = str;
    }
}
