package com.example.rfidapp.model;

public class EpcModel {
    String Epc;

    public EpcModel() {
    }

    public EpcModel(String str) {
        this.Epc = str;
    }

    public String getEpc() {
        return this.Epc;
    }

    public void setEpc(String str) {
        this.Epc = str;
    }
}
