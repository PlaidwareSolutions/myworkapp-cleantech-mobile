package com.example.rfidapp.model;

public class NfcReadModel {
    String nfcLoc;
    String nfcType;

    public NfcReadModel(String str, String str2) {
        this.nfcType = str;
        this.nfcLoc = str2;
    }

    public String getNfcType() {
        return this.nfcType;
    }

    public void setNfcType(String str) {
        this.nfcType = str;
    }

    public String getNfcLoc() {
        return this.nfcLoc;
    }

    public void setNfcLoc(String str) {
        this.nfcLoc = str;
    }
}
