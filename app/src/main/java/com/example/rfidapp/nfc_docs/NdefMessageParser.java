package com.example.rfidapp.nfc_docs;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import androidx.core.os.EnvironmentCompat;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {
    List<String> typeList;

    private NdefMessageParser(List<String> list) {
        this.typeList = list;
    }

    public static List<ParsedNdefRecord> parse(NdefMessage ndefMessage) {
        return getRecords(ndefMessage.getRecords());
    }

    public static List<String> parseType(NdefMessage ndefMessage) {
        return getRecordType(ndefMessage.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] ndefRecordArr) {
        ArrayList arrayList = new ArrayList();
        for (final NdefRecord ndefRecord : ndefRecordArr) {
            if (UriRecord.isUri(ndefRecord)) {
                arrayList.add(UriRecord.parse(ndefRecord));
            } else if (TextRecord.isText(ndefRecord)) {
                arrayList.add(TextRecord.parse(ndefRecord));
            } else if (SmartPoster.isPoster(ndefRecord)) {
                arrayList.add(SmartPoster.parse(ndefRecord));
            } else {
                arrayList.add(new ParsedNdefRecord() {
                    public String str() {
                        return new String(ndefRecord.getPayload());
                    }
                });
            }
        }
        return arrayList;
    }

    public static List<String> getRecordType(NdefRecord[] ndefRecordArr) {
        ArrayList arrayList = new ArrayList();
        for (NdefRecord ndefRecord : ndefRecordArr) {
            if (UriRecord.isUri(ndefRecord)) {
                arrayList.add("Uri Record");
            } else if (TextRecord.isText(ndefRecord)) {
                arrayList.add("Text Record");
            } else if (SmartPoster.isPoster(ndefRecord)) {
                arrayList.add("poster");
            } else {
                arrayList.add(EnvironmentCompat.MEDIA_UNKNOWN);
            }
        }
        return arrayList;
    }
}
