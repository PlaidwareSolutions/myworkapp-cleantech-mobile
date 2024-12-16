package com.example.rfidapp.nfc_docs;

import android.annotation.SuppressLint;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import androidx.core.util.Preconditions;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableMap;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import kotlin.text.Charsets;

public class SmartPoster implements ParsedNdefRecord {
    private static final byte[] ACTION_RECORD_TYPE = {97, 99, 116};
    private static final byte[] TYPE_TYPE = {116};
    private final RecommendedAction mAction;
    private final TextRecord mTitleRecord;
    private final String mType;
    private final UriRecord mUriRecord;

    @SuppressLint("RestrictedApi")
    public SmartPoster(UriRecord uriRecord, TextRecord textRecord, RecommendedAction recommendedAction, String str) {
        this.mUriRecord = (UriRecord) Preconditions.checkNotNull(uriRecord);
        this.mTitleRecord = textRecord;
        this.mAction = (RecommendedAction) Preconditions.checkNotNull(recommendedAction);
        this.mType = str;
    }

    public UriRecord getUriRecord() {
        return this.mUriRecord;
    }

    public TextRecord getTitle() {
        return this.mTitleRecord;
    }

    @SuppressLint("RestrictedApi")
    public static SmartPoster parse(NdefRecord ndefRecord) {
        boolean z = true;
        if (ndefRecord.getTnf() != 1) {
            z = false;
        }
        Preconditions.checkArgument(z);
        Preconditions.checkArgument(Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_SMART_POSTER));
        try {
            return parse(new NdefMessage(ndefRecord.getPayload()).getRecords());
        } catch (FormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static SmartPoster parse(NdefRecord[] ndefRecordArr) {
        try {
            List<ParsedNdefRecord> records = NdefMessageParser.getRecords(ndefRecordArr);
            return new SmartPoster((UriRecord) Iterables.getOnlyElement(Iterables.filter((Iterable<?>) records, UriRecord.class)), (TextRecord) getFirstIfExists(records, TextRecord.class), parseRecommendedAction(ndefRecordArr), parseType(ndefRecordArr));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isPoster(NdefRecord ndefRecord) {
        try {
            parse(ndefRecord);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public String str() {
        if (this.mTitleRecord != null) {
            return this.mTitleRecord.str() + "\n" + this.mUriRecord.str();
        }
        return this.mUriRecord.str();
    }

    private static <T> T getFirstIfExists(Iterable<?> iterable, Class<T> cls) {
        Iterable<T> filter = Iterables.filter(iterable, cls);
        if (!Iterables.isEmpty(filter)) {
            return Iterables.get(filter, 0);
        }
        return null;
    }

    public enum RecommendedAction {
        UNKNOWN((byte) -1),
        DO_ACTION((byte) 0),
        SAVE_FOR_LATER((byte) 1),
        OPEN_FOR_EDITING((byte) 2);

        public static ImmutableMap<Byte, RecommendedAction> LOOKUP = null;
        private final byte mAction;

        static {
            int i;
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (RecommendedAction recommendedAction : values()) {
                builder.put(Byte.valueOf(recommendedAction.getByte()), recommendedAction);
            }
            LOOKUP = builder.build();
        }

        private RecommendedAction(byte b) {
            this.mAction = b;
        }

        private byte getByte() {
            return this.mAction;
        }
    }

    private static NdefRecord getByType(byte[] bArr, NdefRecord[] ndefRecordArr) {
        for (NdefRecord ndefRecord : ndefRecordArr) {
            if (Arrays.equals(bArr, ndefRecord.getType())) {
                return ndefRecord;
            }
        }
        return null;
    }

    private static RecommendedAction parseRecommendedAction(NdefRecord[] ndefRecordArr) {
        NdefRecord byType = getByType(ACTION_RECORD_TYPE, ndefRecordArr);
        if (byType == null) {
            return RecommendedAction.UNKNOWN;
        }
        byte b = byType.getPayload()[0];
        if (RecommendedAction.LOOKUP.containsKey(Byte.valueOf(b))) {
            return (RecommendedAction) RecommendedAction.LOOKUP.get(Byte.valueOf(b));
        }
        return RecommendedAction.UNKNOWN;
    }

    private static String parseType(NdefRecord[] ndefRecordArr) {
        NdefRecord byType = getByType(TYPE_TYPE, ndefRecordArr);
        if (byType == null) {
            return null;
        }
        return new String(byType.getPayload(), Charsets.UTF_8);
    }
}
