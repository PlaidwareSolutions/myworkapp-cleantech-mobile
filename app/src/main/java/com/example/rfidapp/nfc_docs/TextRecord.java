package com.example.rfidapp.nfc_docs;

import android.annotation.SuppressLint;
import android.nfc.NdefRecord;

import androidx.core.util.Preconditions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TextRecord implements ParsedNdefRecord {
    private final String mLanguageCode;
    private final String mText;

    @SuppressLint("RestrictedApi")
    public TextRecord(String str, String str2) {
        this.mLanguageCode = Preconditions.checkNotNull(str);
        this.mText = Preconditions.checkNotNull(str2);
    }

    public String str() {
        return this.mText;
    }

    public String getText() {
        return this.mText;
    }

    public String getLanguageCode() {
        return this.mLanguageCode;
    }

    @SuppressLint("RestrictedApi")
    public static TextRecord parse(NdefRecord ndefRecord) {
        Preconditions.checkArgument(ndefRecord.getTnf() == 1);
        Preconditions.checkArgument(Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT));
        try {
            byte[] payload = ndefRecord.getPayload();
            byte b = payload[0];
            String str = (b & 128) == 0 ? "UTF-8" : "UTF-16";
            byte b2 = (byte) (b & 63);
            return new TextRecord(new String(payload, 1, b2, StandardCharsets.US_ASCII), new String(payload, b2 + 1, (payload.length - b2) - 1, str));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isText(NdefRecord ndefRecord) {
        try {
            parse(ndefRecord);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }
}
