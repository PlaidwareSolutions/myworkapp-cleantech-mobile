package com.example.rfidapp.model;

public class SliderItems {
    private String identifier;
    private int image;

    public SliderItems(int i, String str) {
        this.image = i;
        this.identifier = str;
    }

    public int getImage() {
        return this.image;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
