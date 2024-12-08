package com.example.rfidapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "InventoryItems")
public class InventoryItemsEntity {
    @PrimaryKey
    @NonNull
    String epc;
    String epcInv;
    String inventory;
    String timeStamp;

    public InventoryItemsEntity() {
    }

    public InventoryItemsEntity(String str, String str2, String str3, String str4) {
        this.epcInv = str;
        this.epc = str2;
        this.timeStamp = str3;
        this.inventory = str4;
    }

    public String getEpcInv() {
        return this.epcInv;
    }

    public void setEpcInv(String str) {
        this.epcInv = str;
    }

    public String getEpc() {
        return this.epc;
    }

    public void setEpc(String str) {
        this.epc = str;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(String str) {
        this.timeStamp = str;
    }

    public String getInventory() {
        return this.inventory;
    }

    public void setInventory(String str) {
        this.inventory = str;
    }
}
