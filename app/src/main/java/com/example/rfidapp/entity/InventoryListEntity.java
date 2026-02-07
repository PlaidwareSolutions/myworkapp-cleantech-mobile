package com.example.rfidapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "InventoryList")
public class InventoryListEntity {
    String createTime;

    @PrimaryKey
    @NonNull
    int invId;
    String invName;
    String items;
    String type;
    String updateTime;

    public InventoryListEntity() {
    }

    public InventoryListEntity(String str, String str2, String str3, String str4, String str5) {
        this.invName = str;
        this.createTime = str2;
        this.updateTime = str3;
        this.items = str4;
        this.type = str5;
    }

    public int getInvId() {
        return this.invId;
    }

    public void setInvId(int i) {
        this.invId = i;
    }

    public String getInvName() {
        return this.invName;
    }

    public void setInvName(String str) {
        this.invName = str;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String str) {
        this.createTime = str;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String str) {
        this.updateTime = str;
    }

    public String getItems() {
        return this.items;
    }

    public void setItems(String str) {
        this.items = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }
}
