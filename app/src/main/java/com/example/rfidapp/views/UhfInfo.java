package com.example.rfidapp.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UhfInfo {
    private int count = 0;
    private int selectIndex = -1;
    private String selectItem;
    private ArrayList<HashMap<String, String>> tagList = null;
    private int tagNumber = 0;
    private List<String> tempDatas;
    private long time;

    public ArrayList<HashMap<String, String>> getTagList() {
        return this.tagList;
    }

    public void setTagList(ArrayList<HashMap<String, String>> arrayList) {
        this.tagList = arrayList;
    }

    public void setTagSearchList(ArrayList<HashMap<String, String>> arrayList) {
        this.tagList = arrayList;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long j) {
        this.time = j;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public int getTagNumber() {
        return this.tagNumber;
    }

    public void setTagNumber(int i) {
        this.tagNumber = i;
    }

    public int getSelectIndex() {
        return this.selectIndex;
    }

    public void setSelectIndex(int i) {
        this.selectIndex = i;
    }

    public List<String> getTempDatas() {
        return this.tempDatas;
    }

    public void setTempDatas(List<String> list) {
        this.tempDatas = list;
    }

    public String getSelectItem() {
        return this.selectItem;
    }

    public void setSelectItem(String str) {
        this.selectItem = str;
    }
}
