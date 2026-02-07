package com.example.rfidapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ble_history")
public class BleEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String itemName;
    private String time;

    public BleEntity() {
    }
    public BleEntity(String str, String str2) {
        this.id = str;
        this.itemName = str2;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String str) {
        this.itemName = str;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String str) {
        this.time = str;
    }
}
