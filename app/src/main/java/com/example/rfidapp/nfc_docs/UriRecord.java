package com.example.rfidapp.nfc_docs;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.nfc.NdefRecord;

import androidx.core.net.MailTo;
import androidx.core.util.Preconditions;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.base.Ascii;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableBiMap;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.primitives.Bytes;

import java.nio.charset.Charset;
import java.util.Arrays;

public class UriRecord implements ParsedNdefRecord {
    private static final byte[] EMPTY = new byte[0];
    public static final String RECORD_TYPE = "UriRecord";
    private static final String TAG = "UriRecord";
    private static final ImmutableBiMap<Object, Object> URI_PREFIX_MAP = ImmutableBiMap.builder().put((byte) 0, "").put((byte) 1, "http://www.").put((byte) 2, "https://www.").put((byte) 3, "http://").put((byte) 4, "https://").put((byte) 5, "tel:").put((byte) 6, MailTo.MAILTO_SCHEME).put((byte) 7, "ftp://anonymous:anonymous@").put((byte) 8, "ftp://ftp.").put((byte) 9, "ftps://").put((byte) 10, "sftp://").put((byte) 11, "smb://").put((byte) 12, "nfs://").put(Byte.valueOf(Ascii.CR), "ftp://").put(Byte.valueOf(Ascii.SO), "dav://").put(Byte.valueOf(Ascii.SI), "news:").put(Byte.valueOf(Ascii.DLE), "telnet://").put((byte) 17, "imap:").put(Byte.valueOf(Ascii.DC2), "rtsp://").put((byte) 19, "urn:").put(Byte.valueOf(Ascii.DC4), "pop:").put(Byte.valueOf(Ascii.NAK), "sip:").put(Byte.valueOf(Ascii.SYN), "sips:").put(Byte.valueOf(Ascii.ETB), "tftp:").put(Byte.valueOf(Ascii.CAN), "btspp://").put(Byte.valueOf(Ascii.EM), "btl2cap://").put(Byte.valueOf(Ascii.SUB), "btgoep://").put(Byte.valueOf(Ascii.ESC), "tcpobex://").put(Byte.valueOf(Ascii.FS), "irdaobex://").put((byte) 29, "file://").put((byte) 30, "urn:epc:id:").put(Byte.valueOf(Ascii.US), "urn:epc:tag:").put((byte) 32, "urn:epc:pat:").put((byte) 33, "urn:epc:raw:").put((byte) 34, "urn:epc:").put((byte) 35, "urn:nfc:").build();
    private final Uri mUri;

    @SuppressLint("RestrictedApi")
    public UriRecord(Uri uri) {
        this.mUri = (Uri) Preconditions.checkNotNull(uri);
    }

    public String str() {
        return this.mUri.toString();
    }

    public Uri getUri() {
        return this.mUri;
    }

    public static UriRecord parse(NdefRecord ndefRecord) {
        short tnf = ndefRecord.getTnf();
        if (tnf == 1) {
            return parseWellKnown(ndefRecord);
        }
        if (tnf == 3) {
            return parseAbsolute(ndefRecord);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }

    private static UriRecord parseAbsolute(NdefRecord ndefRecord) {
        return new UriRecord(Uri.parse(new String(ndefRecord.getPayload(), Charset.forName("UTF-8"))));
    }

    @SuppressLint("RestrictedApi")
    private static UriRecord parseWellKnown(NdefRecord ndefRecord) {
        Preconditions.checkArgument(Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI));
        byte[] payload = ndefRecord.getPayload();
        return new UriRecord(Uri.parse(new String(Bytes.concat(((String) URI_PREFIX_MAP.get(Byte.valueOf(payload[0]))).getBytes(Charset.forName("UTF-8")), Arrays.copyOfRange(payload, 1, payload.length)), Charset.forName("UTF-8"))));
    }

    public static boolean isUri(NdefRecord ndefRecord) {
        try {
            parse(ndefRecord);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }
}
