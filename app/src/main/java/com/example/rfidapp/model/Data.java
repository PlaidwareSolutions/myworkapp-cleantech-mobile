package com.example.rfidapp.model;

public class Data {
    private String tagEpc;
    private String tagCount;
    private String tagRssiNumber;

    // Constructor
    public Data(String tagEpc, String tagCount, String tagRssiNumber) {
        this.tagEpc = tagEpc;
        this.tagCount = tagCount;
        this.tagRssiNumber = tagRssiNumber;
    }

    public String getTagEpc() {
        return tagEpc;
    }

    public void setTagEpc(String tagEpc) {
        this.tagEpc = tagEpc;
    }

    public String getTagCount() {
        return tagCount;
    }

    public void setTagCount(String tagCount) {
        this.tagCount = tagCount;
    }

    public String getTagRssiNumber() {
        return tagRssiNumber;
    }

    public void setTagRssiNumber(String tagRssiNumber) {
        this.tagRssiNumber = tagRssiNumber;
    }

    @Override
    public String toString() {
        return "Data{" +
                "tagEpc='" + tagEpc + '\'' +
                ", tagCount='" + tagCount + '\'' +
                ", tagRssiNumber='" + tagRssiNumber + '\'' +
                '}';
    }
}