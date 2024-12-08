package com.example.rfidapp.util;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static String ADDR = "btaddress";
    public static String NAME = "btname";
    static String TAG = "FileUtils";
    public static String filePath = (Environment.getExternalStorageDirectory() + File.separator + "BTDeviceList.xml");

    public static void saveXmlList(List<String[]> list) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            XmlSerializer newSerializer = Xml.newSerializer();
            newSerializer.setOutput(fileOutputStream, "utf-8");
            newSerializer.startDocument("utf-8", true);
            newSerializer.startTag((String) null, "root");
            for (int i = 0; i < list.size(); i++) {
                newSerializer.startTag((String) null, "bt");
                newSerializer.startTag((String) null, ADDR);
                newSerializer.text(list.get(i)[0]);
                newSerializer.endTag((String) null, ADDR);
                newSerializer.startTag((String) null, NAME);
                newSerializer.text(list.get(i)[1]);
                newSerializer.endTag((String) null, NAME);
                newSerializer.endTag((String) null, "bt");
            }
            newSerializer.endTag((String) null, "root");
            newSerializer.endDocument();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String[]> readXmlList() {
        ArrayList<String[]> arrayList = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return arrayList;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            XmlPullParser newPullParser = Xml.newPullParser();
            newPullParser.setInput(fileInputStream, "utf-8");
            String str = null;
            String str2 = null;
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                String name = newPullParser.getName();
                if (eventType != 2) {
                    if (eventType == 3) {
                        if ("bt".equals(name)) {
                            Log.i(TAG, "addr---" + str);
                            Log.i(TAG, "name---" + str2);
                            arrayList.add(new String[]{str, str2});
                        }
                    }
                } else if (!"root".equals(name)) {
                    if (!"bt".equals(name)) {
                        if (ADDR.equals(name)) {
                            str = newPullParser.nextText();
                        } else if (NAME.equals(name)) {
                            str2 = newPullParser.nextText();
                        }
                    }
                }
            }
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static void clearXmlList() {
        ArrayList<String[]> readXmlList = readXmlList();
        readXmlList.clear();
        saveXmlList(readXmlList);
    }
}
