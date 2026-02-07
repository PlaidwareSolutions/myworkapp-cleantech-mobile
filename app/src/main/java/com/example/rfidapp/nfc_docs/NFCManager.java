package com.example.rfidapp.nfc_docs;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


import com.example.rfidapp.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NFCManager {
    MainActivity activity;
    NfcAdapter nfcAdpt;

    public static class NFCNotEnabled extends Exception {
    }

    public static class NFCNotSupported extends Exception {
    }

    public NFCManager(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    public void verifyNFC() throws NFCNotSupported, NFCNotEnabled {
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.activity);
        this.nfcAdpt = defaultAdapter;
        if (defaultAdapter == null) {
            throw new NFCNotSupported();
        } else if (!defaultAdapter.isEnabled()) {
            throw new NFCNotEnabled();
        }
    }

    public void disableDispatch() {
        this.nfcAdpt.disableForegroundDispatch(this.activity);
    }

    public void writeTag(Tag tag, NdefMessage ndefMessage) {
        if (tag != null) {
            try {
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                    if (ndefFormatable != null) {
                        ndefFormatable.connect();
                        ndefFormatable.format(ndefMessage);
                        ndefFormatable.close();
                        return;
                    }
                    return;
                }
                ndef.connect();
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public NdefMessage createUriMessage(String str, String str2) {
        return new NdefMessage(new NdefRecord[]{NdefRecord.createUri(str2 + str)});
    }

    public NdefMessage createTextMessage(String str) {
        try {
            byte[] bytes = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] bytes2 = str.getBytes("UTF-8");
            int length = bytes.length;
            int length2 = bytes2.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(length + 1 + length2);
            byteArrayOutputStream.write((byte) (length & 31));
            byteArrayOutputStream.write(bytes, 0, length);
            byteArrayOutputStream.write(bytes2, 0, length2);
            return new NdefMessage(new NdefRecord[]{new NdefRecord((short) 1, NdefRecord.RTD_TEXT, new byte[0], byteArrayOutputStream.toByteArray())});
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.activity, NotificationCompat.CATEGORY_ERROR + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public NdefMessage createGeoMessage() {
        return new NdefMessage(new NdefRecord[]{NdefRecord.createUri("geo:48.471066,35.038664")});
    }
}
