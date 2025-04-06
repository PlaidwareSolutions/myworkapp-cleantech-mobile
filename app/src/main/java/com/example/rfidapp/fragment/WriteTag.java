package com.example.rfidapp.fragment;

import static java.util.Collections.binarySearch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.databinding.FragmentWriteTagBinding;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.util.tool.StringUtils;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.KeyEventCallback;


import java.util.ArrayList;
import java.util.List;

public class WriteTag extends KeyDownFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int bank = 1;
    FragmentWriteTagBinding binding;
    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            UHFTAGInfo uHFTAGInfo = (UHFTAGInfo) message.obj;
            WriteTag.this.addDataToList(uHFTAGInfo.getEPC(), "", uHFTAGInfo.getRssi());
        }
    };
    boolean isRead = false;
    int len = 6;
    public boolean loopFlag = false;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;
    int ptr = 2;
    boolean result = false;
    int scanTime = 0;
    String srlno;
    public List<String> tempDatas = new ArrayList();
    private long time;
    private int total = 0;
    UHFTAGInfo uhftagInfo;
    String wrtEpc;

    public static WriteTag newInstance(String str, String str2) {
        WriteTag writeTag = new WriteTag();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        writeTag.setArguments(bundle);
        return writeTag;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
            this.wrtEpc = getArguments().getString("epc");
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentWriteTagBinding.inflate(layoutInflater, viewGroup, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        this.mContext = mainActivity;
        mainActivity.initSound();
        setHasOptionsMenu(true);
        this.mContext.currentFrag = this;
        init();
        return this.binding.getRoot();
    }

    public void onResume() {
        this.mContext.checkBTConnect();
        super.onResume();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_power).setVisible(true);
        menu.findItem(R.id.menu_volume).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    public void onMyKeyDown() {
        startWrite();
        super.onMyKeyDown();
    }

    public void init() {
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && ReaderClass.mBtReader != null) {
            ReaderClass.mBtReader.setKeyEventCallback(new KeyEventCallback() {
                public void onKeyUp(int i) {
                }

                public void onKeyDown(int i) {
                    WriteTag.this.startWrite();
                }
            });
        }
        this.mContext.setTitle("Write Tag");
        if (this.wrtEpc != null) {
            this.binding.etSearch.setText(this.wrtEpc);
            this.binding.ivSearch.setVisibility(android.view.View.GONE);
            this.binding.ivClose.setVisibility(android.view.View.VISIBLE);
            this.binding.etDataWrite.setText(this.wrtEpc);
            this.binding.etSearch.setEnabled(false);
            btStart("appcolor");
            btCancel("appcolor");
            this.mContext.frm = 4;
        }
        this.binding.etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                WriteTag.this.binding.etDataWrite.setText(charSequence.toString());
            }

            public void afterTextChanged(Editable editable) {
                WriteTag writeTag = WriteTag.this;
                writeTag.wrtEpc = writeTag.binding.etDataWrite.getText().toString().trim();
                if (WriteTag.this.wrtEpc.length() > 0) {
                    WriteTag.this.btStart("appColor");
                    WriteTag.this.btCancel("appcolor");
                    WriteTag.this.binding.ivSearch.setVisibility(android.view.View.GONE);
                    WriteTag.this.binding.ivClose.setVisibility(android.view.View.VISIBLE);
                    return;
                }
                WriteTag.this.btStart("grey");
                WriteTag.this.btCancel("grey");
                WriteTag.this.binding.ivSearch.setVisibility(android.view.View.VISIBLE);
                WriteTag.this.binding.ivClose.setVisibility(android.view.View.GONE);
            }
        });
        this.binding.ivClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WriteTag.this.binding.etSearch.setText("");
                WriteTag.this.binding.ivSearch.setVisibility(android.view.View.VISIBLE);
                WriteTag.this.mContext.hideKeybaord(view);
            }
        });
        this.binding.etDataWrite.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                WriteTag writeTag = WriteTag.this;
                writeTag.wrtEpc = writeTag.binding.etDataWrite.getText().toString().trim();
                if (WriteTag.this.wrtEpc.length() > 0) {
                    WriteTag.this.btStart("appColor");
                    WriteTag.this.btCancel("appcolor");
                    return;
                }
                WriteTag.this.btStart("grey");
                WriteTag.this.btCancel("grey");
            }
        });
        this.binding.btWrite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WriteTag.this.startWrite();
            }
        });
        this.binding.btCancl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WriteTag.this.binding.etSearch.setText("");
                WriteTag.this.binding.etSearch.setEnabled(true);
                WriteTag.this.btCancel("grey");
                WriteTag.this.btStart("grey");
            }
        });
        this.binding.btRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WriteTag.this.scanFromRFID();
            }
        });
        this.binding.SpinnerBankWrite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                String obj = adapterView.getItemAtPosition(i).toString();
                if (obj.equals("EPC")) {
                    WriteTag.this.bank = 1;
                    WriteTag.this.binding.EtPtrWrite.setText(ExifInterface.GPS_MEASUREMENT_2D);
                    WriteTag.this.binding.EtLenWrite.setText("6");
                    WriteTag.this.binding.tvDataType.setText("Write Data (EPC): ");
                    WriteTag.this.binding.tvDataType.setText("Write Data (EPC): ");
                } else if (obj.equals("USER")) {
                    WriteTag.this.bank = 3;
                    WriteTag.this.binding.EtPtrWrite.setText("0");
                    WriteTag.this.binding.EtLenWrite.setText("32");
                    WriteTag.this.binding.tvDataType.setText("Write Data (USER): ");
                }
            }
        });
    }

    public void startWrite() {
        if (this.binding.etDataWrite.getText().toString().length() <= 0) {
            this.mContext.highlightToast("Please Enter Epc", 2);
        } else if (StringUtils.isHexNumber(this.binding.etSearch.getText().toString())) {
            writeTags();
        } else {
            Toast.makeText(this.mContext, "Kindly Use Hax Values to Write Tag.", Toast.LENGTH_SHORT).show();
        }
    }

    public void scanFromRFID() {
        if (ReaderClass.mReader != null || (ReaderClass.mBtReader != null && this.mContext.isBtConnect)) {
            if (ReaderClass.mBtReader == null || !this.mContext.isBtConnect) {
                this.uhftagInfo = ReaderClass.mReader.inventorySingleTag();
            } else {
                this.uhftagInfo = ReaderClass.mBtReader.inventorySingleTag();
            }
            int i = this.scanTime + 1;
            this.scanTime = i;
            if (this.uhftagInfo != null) {
                this.scanTime = 0;
                this.binding.etSearch.setText(this.uhftagInfo.getEPC());
                this.binding.etSearch.setEnabled(false);
                this.binding.etDataWrite.setText(this.uhftagInfo.getEPC());
                this.isRead = true;
            } else if (i < 3) {
                scanFromRFID();
            } else {
                this.scanTime = 0;
                Toast.makeText(this.mContext, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this.mContext, "Please Connect Device First", Toast.LENGTH_SHORT).show();
        }
    }

    public void writeTags() {
        this.ptr = Integer.parseInt(this.binding.EtPtrWrite.getText().toString().trim());
        this.len = Integer.parseInt(this.binding.EtLenWrite.getText().toString().trim());
        if (!this.isRead) {
            this.total = 0;
            readTag();
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    WriteTag.this.stopInventory();
                    if (WriteTag.this.tempDatas.size() == 0) {
                        WriteTag.this.mContext.highlightToast("No RFID found...", 2);
                    } else if (WriteTag.this.tempDatas.size() > 1) {
                        WriteTag.this.mContext.highlightToast("More than 1 RFIDs found..", 2);
                    } else if (WriteTag.this.mContext.isC5Device.booleanValue()) {
                        if (StringUtils.isHexNumber(WriteTag.this.binding.etSearch.getText().toString())) {
                            WriteTag writeTag = WriteTag.this;
                            writeTag.wrtC5EpcTag(writeTag.binding.etSearch.getText().toString().trim());
                        } else {
                            Toast.makeText(WriteTag.this.mContext, "Kindly Use Hax Values to Write Tag.", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } else if (!WriteTag.this.mContext.isBTDevice.booleanValue()) {
                        WriteTag.this.mContext.highlightToast("Kindly Use RFID Device", 2);
                    } else if (!WriteTag.this.mContext.isBtConnect) {
                        WriteTag.this.mContext.highlightToast("Please Connect Device First..", 2);
                    } else if (StringUtils.isHexNumber(WriteTag.this.binding.etSearch.getText().toString())) {
                        WriteTag writeTag2 = WriteTag.this;
                        writeTag2.wrtBtEpcTag(writeTag2.binding.etSearch.getText().toString().trim());
                    } else {
                        Toast.makeText(WriteTag.this.mContext, "Kindly Use Hax Values to Write Tag.", Toast.LENGTH_SHORT).show();
                    }
                    WriteTag.this.tempDatas.clear();
                }
            }, 500);
        } else if (this.mContext.isC5Device.booleanValue()) {
            if (StringUtils.isHexNumber(this.binding.etSearch.getText().toString())) {
                wrtC5EpcTag(this.binding.etSearch.getText().toString().trim());
            } else {
                Toast.makeText(this.mContext, "Kindly Use Hax Values to Write Tag.", Toast.LENGTH_SHORT).show();
            }
        } else if (!this.mContext.isBTDevice.booleanValue()) {
            this.mContext.highlightToast("Kindly Use RFID Device", 2);
        } else if (!this.mContext.isBtConnect) {
            this.mContext.highlightToast("Please Connect Device First..", 2);
        } else if (StringUtils.isHexNumber(this.binding.etSearch.getText().toString())) {
            wrtBtEpcTag(this.binding.etSearch.getText().toString().trim());
        } else {
            Toast.makeText(this.mContext, "Kindly Use Hax Values to Write Tag.", Toast.LENGTH_SHORT).show();
        }
    }

    public void wrtBtEpcTag(String str) {
        if (str.length() <= 0) {
            this.mContext.highlightToast("No RFID Value Found..", 2);
        } else if (this.isRead) {
            try {
                if (ReaderClass.mBtReader.writeData("00000000", 1, 32, 96, this.binding.etSearch.getText().toString().trim(), this.bank, this.ptr, this.len, this.wrtEpc)) {
                    this.result = true;
                    this.mContext.highlightToast("Writing Success...", 1);
                } else {
                    this.result = false;
                    this.mContext.highlightToast("Writing Fail...", 2);
                }
            } catch (Exception unused) {
                this.mContext.highlightToast("Writing problem..", 2);
            }
        } else if (ReaderClass.mBtReader.writeData("00000000", this.bank, this.ptr, this.len, this.wrtEpc)) {
            this.result = true;
            this.mContext.highlightToast("Writing Success...", 1);
        } else {
            this.result = false;
            this.mContext.highlightToast("Writing Fail...", 2);
        }
        if (!this.result) {
            this.mContext.playSound(2);
        } else {
            this.mContext.highlightToast("Tag Writting Done..", 1);
        }
    }

    public void wrtC5EpcTag(String str) {
        if (str.length() <= 0) {
            this.mContext.highlightToast("No RFID Value Found..", 2);
        } else if (this.isRead) {
            try {
                if (ReaderClass.mReader.writeData("00000000", 1, 32, 96, this.binding.etSearch.getText().toString().trim(), this.bank, this.ptr, this.len, this.wrtEpc)) {
                    this.result = true;
                    this.mContext.highlightToast("Writing Success...", 1);
                } else {
                    this.result = false;
                    this.mContext.highlightToast("Writing Fail...", 2);
                }
            } catch (Exception unused) {
                this.mContext.highlightToast("Writing problem..", 2);
            }
        } else if (ReaderClass.mReader.writeData("00000000", this.bank, this.ptr, this.len, this.wrtEpc)) {
            this.result = true;
            this.mContext.highlightToast("Writing Success...", 1);
        } else {
            this.result = false;
            this.mContext.highlightToast("Writing Fail...", 2);
        }
        if (!this.result) {
            this.mContext.playSound(2);
        } else {
            this.mContext.highlightToast("Tag Writting Done..", 1);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mContext.isScanning) {
            stopInventory();
        }
    }

    private void readTag() {
        if (ReaderClass.mReader != null) {
            if (ReaderClass.mReader.startInventoryTag()) {
                this.loopFlag = true;
                this.time = System.currentTimeMillis();
                new TagThread().start();
                return;
            }
            stopInventory();
            Toast.makeText(this.mContext, "Failed to read tags...", Toast.LENGTH_SHORT).show();
        } else if (ReaderClass.mBtReader != null && this.mContext.isBtConnect) {
            if (ReaderClass.mBtReader.startInventoryTag()) {
                this.loopFlag = true;
                this.time = System.currentTimeMillis();
                new TagThread().start();
                return;
            }
            stopInventory();
            Toast.makeText(this.mContext, "Failed to read tags...", Toast.LENGTH_SHORT).show();
        }
    }

    class TagThread extends Thread {
        TagThread() {
        }

        public void run() {
            UHFTAGInfo uHFTAGInfo = null;
            while (WriteTag.this.loopFlag) {
                if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && WriteTag.this.mContext.isBtConnect) {
                    uHFTAGInfo = ReaderClass.mBtReader.readTagFromBuffer();
                } else if (ReaderClass.mReader != null) {
                    uHFTAGInfo = ReaderClass.mReader.readTagFromBuffer();
                }
                if (uHFTAGInfo != null) {
                    System.out.println("tid");
                    System.out.println(uHFTAGInfo.getTid());
                    Log.e("as_con", "tid is buffer." + uHFTAGInfo.getTid());
                    Message obtainMessage = WriteTag.this.handler.obtainMessage();
                    obtainMessage.obj = uHFTAGInfo;
                    WriteTag.this.handler.sendMessage(obtainMessage);
                }
            }
        }
    }

    public void addDataToList(String str, String str2, String str3) {
        if (StringUtils.isNotEmpty(str) && checkIsExist(str) == -1) {
            Log.e("as_con", "temp datas : " + str);
            if (str.length() > 24) {
                str = str.substring(4, 28);
            }
            if (!this.tempDatas.contains(str)) {
                this.tempDatas.add(str);
            }
        }
    }

    public int checkIsExist(String str) {
        if (StringUtils.isEmpty(str)) {
            return -1;
        }
        return binarySearch(this.tempDatas, str);
    }

    public void stopInventory() {
        if (this.loopFlag) {
            this.loopFlag = false;
            if (!this.mContext.stopInventory()) {
                this.mContext.highlightToast("Stop...", 1);
            }
        }
    }

    public void btCancel(String str) {
        if (str.contains("grey")) {
            this.binding.btCancl.setEnabled(false);
            this.binding.btCancl.setBackgroundResource(R.drawable.bt_design_gray);
            return;
        }
        this.binding.btCancl.setEnabled(true);
        this.binding.btCancl.setBackgroundResource(R.drawable.bt_design);
    }

    public void btStart(String str) {
        if (str.contains("grey")) {
            this.binding.btWrite.setEnabled(false);
            this.binding.btWrite.setBackgroundResource(R.drawable.bt_design_gray);
        } else if (str.contains("red")) {
            this.binding.btWrite.setEnabled(true);
            this.binding.btWrite.setBackgroundResource(R.drawable.bt_design);
        } else {
            this.binding.btWrite.setEnabled(true);
            this.binding.btWrite.setBackgroundResource(R.drawable.bt_design);
        }
    }
}
