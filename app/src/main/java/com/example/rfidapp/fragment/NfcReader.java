package com.example.rfidapp.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.NfcReaderAdapter;
import com.example.rfidapp.databinding.FragmentNfcReaderBinding;
import com.example.rfidapp.model.NfcReadModel;
import com.example.rfidapp.nfc_docs.NdefMessageParser;
import com.example.rfidapp.nfc_docs.ParsedNdefRecord;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.cli.HelpFormatter;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.TokenParser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NfcReader extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String EXTRA_ATQA = "atqa";
    public static final String EXTRA_SAK = "sak";
    FragmentNfcReaderBinding binding;
    String canNfcRead = "No";
    byte[] id;
    String isWritable = "";
    private byte[] mAtqa;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;
    private short mSak;
    String memInfo = "";
    String memSize = "";
    String ndefType = "";
    NfcAdapter nfcAdapter;
    String postType = "";
    String preType = "";
    Tag tag;

    public void init() {
    }

    public static NfcReader newInstance(String str, String str2) {
        NfcReader nfcReader = new NfcReader();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        nfcReader.setArguments(bundle);
        return nfcReader;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentNfcReaderBinding.inflate(layoutInflater, viewGroup, false);
        this.mContext = (MainActivity) getActivity();
        init();
        return this.binding.getRoot();
    }

    public void onResume() {
        super.onResume();
        this.mContext.fragment = this;
        if (this.mContext.nfcAdapter != null) {
            if (!this.mContext.nfcAdapter.isEnabled()) {
                showWirelessSettings();
            }
            NfcAdapter nfcAdapter2 = this.mContext.nfcAdapter;
            MainActivity mainActivity = this.mContext;
            nfcAdapter2.enableForegroundDispatch(mainActivity, mainActivity.pendingIntent, (IntentFilter[]) null, (String[][]) null);
        }
    }

    public void resolveIntent(Intent intent) throws IOException {
        this.binding.approachTag.setVisibility(View.GONE);
        String action = intent.getAction();
        if ("android.nfc.action.TAG_DISCOVERED".equals(action) || "android.nfc.action.TECH_DISCOVERED".equals(action) || "android.nfc.action.NDEF_DISCOVERED".equals(action)) {
            Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            this.tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            this.id = intent.getByteArrayExtra("android.nfc.extra.ID");
            if (parcelableArrayExtra != null) {
                NdefMessage[] ndefMessageArr = new NdefMessage[parcelableArrayExtra.length];
                for (int i = 0; i < parcelableArrayExtra.length; i++) {
                    ndefMessageArr[i] = (NdefMessage) parcelableArrayExtra[i];
                }
                displayMsgs(ndefMessageArr);
            } else {
                this.id = intent.getByteArrayExtra("android.nfc.extra.ID");
                Tag tag2 = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
                this.tag = tag2;
                dumpTagData(tag2).getBytes();
            }
            Log.e("as_con", " tag value : " + dumptoHax(this.id));
            if (setTagType(this.tag).length() > 0) {
                this.binding.llType.setVisibility(View.VISIBLE);
                this.binding.nfcType.setText(setTagType(this.tag));
            }
            if (techValue(this.tag).length() > 0) {
                this.binding.llTech.setVisibility(android.view.View.VISIBLE);
                this.binding.nfcTech.setText(techValue(this.tag));
            }
            this.binding.llSerial.setVisibility(View.VISIBLE);
            this.binding.nfcSerial.setText(dumptoHax(this.id).trim().replace(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, ":"));
            if (String.valueOf(toHex(NfcA.get(this.tag).getAtqa()).trim().replace(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, "")).length() > 0) {
                this.binding.llAtqa.setVisibility(View.VISIBLE);
                this.binding.nfcAtqa.setText("0x" + toHex(NfcA.get(this.tag).getAtqa()).trim().replace(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, ""));
            }
            if (String.valueOf(NfcA.get(this.tag).getSak()).length() > 0) {
                this.binding.llSak.setVisibility(View.VISIBLE);
                this.binding.nfcSak.setText("0x0" + NfcA.get(this.tag).getSak());
            }
            if (setISO(techValue(this.tag)).length() > 0) {
                this.binding.tagType.setText(setISO(techValue(this.tag)));
            }
            if (this.memInfo.length() > 0) {
                this.binding.llMemory.setVisibility(android.view.View.VISIBLE);
                this.binding.nfcMemInfo.setText(this.memInfo);
            }
            if (this.memSize.length() > 0) {
                this.binding.llSize.setVisibility(android.view.View.VISIBLE);
                this.binding.nfcSize.setText(this.memSize);
            }
            if (this.isWritable.length() > 0) {
                this.binding.llWritable.setVisibility(android.view.View.VISIBLE);
                this.binding.nfcWritable.setText(this.isWritable);
            }
            if (this.canNfcRead.length() > 0) {
                this.binding.llReadMode.setVisibility(android.view.View.VISIBLE);
                this.binding.nfcCanRead.setText("" + this.canNfcRead);
            }
            Log.e("as_con", " tag value : " + getAtqa());
            Log.e("as_con", " tag value atqa : " + dumptoHax(NfcA.get(this.tag).getAtqa()));
        }
    }

    public String setISO(String str) {
        if (str.toString().contains("IsoDep")) {
            return "ISO 14443-4";
        }
        if (str.toString().contains("NfcA")) {
            return "ISO 14443-3A";
        }
        if (str.toString().contains("NfcB")) {
            return "ISO 14443-3B";
        }
        if (str.toString().contains("NfcF")) {
            return "JIS 6319-4";
        }
        return str.toString().contains("NfcV") ? "ISO 15693)" : "Unknown)";
    }

    public String setTagType(Tag tag2) {
        this.preType = "NXP";
        this.postType = "Unknown";
        for (String str : tag2.getTechList()) {
            if (str.toString().contains("NdefFormatable")) {
                this.postType = "Mifare";
            } else if (str.toString().contains("MifareUltralight")) {
                this.postType = "NTAG216";
                try {
                    if (str.toString().contains("Ndef")) {
                        Ndef ndef = Ndef.get(tag2);
                        if (ndef.isWritable()) {
                            this.isWritable = "Yes";
                        } else {
                            this.isWritable = "No";
                        }
                        if (ndef.getMaxSize() == 868) {
                            this.memInfo = "924 bytes: 231 pages (4 bytes each)";
                        } else {
                            this.memInfo = "180 bytes: 45 pages (4 bytes each)";
                        }
                        if (ndef.canMakeReadOnly()) {
                            this.canNfcRead = "Yes";
                        }
                        this.ndefType = ndef.getType();
                    }
                } catch (Exception unused) {
                }
            }
        }
        return this.preType + " - " + this.postType;
    }

    private String dumpTagData(Tag tag2) {
        int i;
        StringBuilder sb = new StringBuilder();
        tag2.getId();
        sb.append("Technologies: ");
        int i2 = 0;
        for (String substring : tag2.getTechList()) {
            sb.append(substring.substring(17));
            sb.append(", ");
        }
        int i3 = 2;
        sb.delete(sb.length() - 2, sb.length());
        String[] techList = tag2.getTechList();
        int length = techList.length;
        while (i2 < length) {
            String str = techList[i2];
            String str2 = "Unknown";
            if (str.equals(MifareClassic.class.getName())) {
                sb.append(10);
                try {
                    MifareClassic mifareClassic = MifareClassic.get(tag2);
                    int type = mifareClassic.getType();
                    String str3 = type != 0 ? type != 1 ? type != i3 ? str2 : "Pro" : "Plus" : "Classic";
                    sb.append("Mifare Classic type: ");
                    sb.append(str3);
                    sb.append(10);
                    Log.e("as_con", "Mifare Classic type: : " + str3);
                    sb.append("Mifare size: ");
                    sb.append(mifareClassic.getSize() + " bytes");
                    sb.append(10);
                    Log.e("as_con", "Mifare size: " + mifareClassic.getSize());
                    sb.append("Mifare sectors: ");
                    sb.append(mifareClassic.getSectorCount());
                    sb.append(10);
                    Log.e("as_con", "Mifare sectors: " + mifareClassic.getSectorCount());
                    sb.append("Mifare blocks: ");
                    sb.append(mifareClassic.getBlockCount());
                    Log.e("as_con", "Mifare blocks: " + mifareClassic.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }
            if (str.equals(MifareUltralight.class.getName())) {
                sb.append(10);
                MifareUltralight mifareUltralight = MifareUltralight.get(tag2);
                int type2 = mifareUltralight.getType();
                if (type2 != 1) {
                    i = 2;
                    if (type2 == 2) {
                        str2 = "Ultralight C";
                    }
                } else {
                    i = 2;
                    str2 = "Ultralight";
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(str2);
                Log.e("as_con", "Mifare Ultralight type:" + mifareUltralight.getMaxTransceiveLength());
            } else {
                i = 2;
            }
            i2++;
            i3 = i;
        }
        return sb.toString();
    }

    private String techValue(Tag tag2) {
        String[] techList = tag2.getTechList();
        String str = "";
        for (int i = 0; i < techList.length; i++) {
            if (i == 0) {
                str = str + techList[i].replace("android.nfc.tech.", "");
            } else {
                str = str + ", " + techList[i].replace("android.nfc.tech.", "");
            }
        }
        return str.trim();
    }

    private void displayMsgs(NdefMessage[] ndefMessageArr) {
        if (ndefMessageArr != null && ndefMessageArr.length != 0) {
            StringBuilder sb = new StringBuilder();
            List<ParsedNdefRecord> parse = NdefMessageParser.parse(ndefMessageArr[0]);
            List<String> parseType = NdefMessageParser.parseType(ndefMessageArr[0]);
            int size = parse.size();
            ArrayList arrayList = new ArrayList();
            this.binding.nfcRecyclerview.setLayoutManager(new LinearLayoutManager(this.mContext));
            for (int i = 0; i < size; i++) {
                String str = parseType.get(i);
                String str2 = parse.get(i).str();
                sb.append(str2).append("\n");
                Log.e("as_con", str + " dd msg data is: " + str2);
                arrayList.add(new NfcReadModel(str, str2));
                this.binding.nfcRecyclerview.setAdapter(new NfcReaderAdapter(this.mContext, arrayList));
            }
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this.mContext, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
    }

    private String toHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int length = bArr.length - 1; length >= 0; length--) {
            byte b = (byte) (bArr[length] & 255);
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
            if (length > 0) {
                sb.append(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR);
            }
        }
        return sb.toString();
    }

    private String dumptoHax(byte[] bArr) {
        String str = new String();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            str = str + hexString + TokenParser.SP;
        }
        return str.toString();
    }

    private String toReversedHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            if (i > 0) {
                sb.append(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR);
            }
            byte b = (byte) (bArr[i] & 255);
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bArr) {
        long j = 0;
        long j2 = 1;
        for (byte b : bArr) {
            j += (((long) b) & 255) * j2;
            j2 *= 256;
        }
        return j;
    }

    private long toReversedDec(byte[] bArr) {
        long j = 0;
        long j2 = 1;
        for (int length = bArr.length - 1; length >= 0; length--) {
            j += (((long) bArr[length]) & 255) * j2;
            j2 *= 256;
        }
        return j;
    }

    public byte[] getAtqa() {
        return this.mAtqa;
    }
}
