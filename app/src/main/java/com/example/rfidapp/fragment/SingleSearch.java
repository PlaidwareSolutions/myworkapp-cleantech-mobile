package com.example.rfidapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.activity.TagFinderActivity;
import com.example.rfidapp.databinding.FragmentSingleSearchBinding;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.util.tool.StringUtils;
import com.rscja.deviceapi.interfaces.KeyEventCallback;


public class SingleSearch extends KeyDownFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentSingleSearchBinding binding;
    int btGoClick;
    String epc;
    Handler handler;
    public boolean isSound = false;
    TagFinderActivity mContext;
    private String mParam1;
    private String mParam2;
    boolean result;
    int soundInt = 0;
    String srlno;
    boolean stLoc = false;

    public static SingleSearch newInstance(String str, String str2) {
        SingleSearch singleSearch = new SingleSearch();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        singleSearch.setArguments(bundle);
        return singleSearch;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
            this.epc = getArguments().getString("epc");
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentSingleSearchBinding.inflate(layoutInflater, viewGroup, false);
        setHasOptionsMenu(true);
        return this.binding.getRoot();
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        TagFinderActivity tagFinderActivity = (TagFinderActivity) getActivity();
        this.mContext = tagFinderActivity;
        tagFinderActivity.currentFrag = this;
        init();
    }

    public void onResume() {
        this.mContext.checkBTConnect();
        super.onResume();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_power).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    public void onMyKeyDown() {
        checkSearch();
        super.onMyKeyDown();
    }

    public void init() {
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && ReaderClass.mBtReader != null) {
            ReaderClass.mBtReader.setKeyEventCallback(new KeyEventCallback() {
                public void onKeyUp(int i) {
                }

                public void onKeyDown(int i) {
                    SingleSearch.this.checkSearch();
                }
            });
        }
        this.mContext.setTitle("Tag Finder");
        if (this.epc != null) {
            this.binding.etSearch.setText(this.epc);
            this.binding.etSearch.setEnabled(false);
            this.binding.ivSearch.setVisibility(View.GONE);
            this.binding.ivClose.setVisibility(View.VISIBLE);
            btStart("appcolor");
            btCancel("appcolor");
            /*this.mContext.frm = 4;*/
        }
        this.handler = new Handler();
        this.btGoClick = 1;
        this.binding.btStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SingleSearch.this.checkSearch();
            }
        });
        this.binding.btCancl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SingleSearch.this.btStart("grey");
                SingleSearch.this.btCancel("grey");
                SingleSearch.this.binding.etSearch.setText("");
                SingleSearch.this.binding.tvEpc.setText("");
                SingleSearch.this.binding.tvRssi.setText("");
                SingleSearch.this.isSound = false;
            }
        });
        this.binding.etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                SingleSearch singleSearch = SingleSearch.this;
                singleSearch.epc = singleSearch.binding.etSearch.getText().toString().trim();
                if (SingleSearch.this.epc.length() > 0) {
                    SingleSearch.this.btStart("appcolor");
                    SingleSearch.this.btCancel("appcolor");
                    SingleSearch.this.binding.ivSearch.setVisibility(View.GONE);
                    SingleSearch.this.binding.ivClose.setVisibility(View.VISIBLE);
                    return;
                }
                SingleSearch.this.btStart("grey");
                SingleSearch.this.btCancel("grey");
                SingleSearch.this.binding.etSearch.setEnabled(true);
                SingleSearch.this.binding.ivSearch.setVisibility(android.view.View.VISIBLE);
                SingleSearch.this.binding.ivClose.setVisibility(android.view.View.GONE);
            }
        });
        this.binding.ivClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if ((ReaderClass.mReader == null || !ReaderClass.mReader.isWorking()) && (ReaderClass.mBtReader == null || !SingleSearch.this.mContext.isBtConnect || !ReaderClass.mBtReader.isWorking())) {
                    SingleSearch.this.binding.etSearch.setText("");
                    SingleSearch.this.binding.tvEpc.setText("");
                    SingleSearch.this.binding.tvRssi.setText("");
                    SingleSearch.this.binding.ivSearch.setVisibility(View.VISIBLE);
                    SingleSearch.this.mContext.hideKeybaord(view);
                    return;
                }
                SingleSearch.this.mContext.highlightToast("Kindly Stop Searching First..", 2);
            }
        });
    }

    public void checkSearch() {
        if (this.binding.etSearch.getText().toString().length() <= 0) {
            this.mContext.highlightToast("Please Enter EPC", 2);
        } else if (this.mContext.isC5Device.booleanValue()) {
            if (StringUtils.isHexNumber(this.binding.etSearch.getText().toString())) {
                startAndStop();
            } else {
                this.mContext.highlightToast("Kindly Use Hax Values to Search Tag.", 2);
            }
        } else if (!this.mContext.isBTDevice.booleanValue()) {
            this.mContext.highlightToast("Kindly Use RFID Device", 2);
        } else if (!this.mContext.isBtConnect) {
            this.mContext.highlightToast("Please Connect Device First..", 2);
        } else if (StringUtils.isHexNumber(this.binding.etSearch.getText().toString())) {
            startAndStop();
        } else {
            this.mContext.highlightToast("Kindly Use Hax Values to Search Tag.", 2);
        }
    }

    public void btStart(String str) {
        if (str.contains("grey")) {
            this.binding.btStart.setEnabled(false);
            this.binding.btStart.setBackgroundResource(R.drawable.bt_design_gray);
        } else if (str.contains("red")) {
            this.binding.btStart.setEnabled(true);
            this.binding.btStart.setBackgroundResource(R.drawable.bt_design);
        } else {
            this.binding.btStart.setEnabled(true);
            this.binding.btStart.setBackgroundResource(R.drawable.bt_design);
        }
    }

    public void onPause() {
        super.onPause();
        stopLocation();
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

    public void startAndStop() {
        int i = this.btGoClick;
        if (i == 1) {
            this.mContext.initSound();
            startLoc();
        } else if (i == 2) {
            stopLocation();
            this.binding.btStart.setText("START");
            btStart("app_color");
            this.btGoClick = 1;
            btCancel("appcolor");
        }
    }

    private void startLoc() {
        if (ReaderClass.mReader != null || (ReaderClass.mBtReader != null && this.mContext.isBtConnect)) {
            this.binding.tvEpc.setText(this.epc);
            if (this.epc.length() > 0) {
                startLocation();
            }
            this.binding.btStart.setText("STOP");
            btStart("red");
            this.btGoClick = 2;
            btCancel("grey");
            return;
        }
        this.mContext.highlightToast("Please connect device first", 2);
    }

    private void startLocation() {
        this.stLoc = true;
        if (ReaderClass.mBtReader != null && this.mContext.isBtConnect) {
            this.result = ReaderClass.mBtReader.startLocation(this.mContext, this.epc, 1, 32, i -> SingleSearch.this.startLocationLogic(i, false/* pass static data, as not receiving from callback*/));
        } else if (ReaderClass.mReader != null) {
            this.result = ReaderClass.mReader.startLocation(this.mContext, this.epc, 1, 32, i -> SingleSearch.this.startLocationLogic(i, false /* pass static data, as not receiving from callback*/));
        }
        if (!this.result) {
            this.mContext.highlightToast("failed..", 2);
        }
    }

    public void startLocationLogic1(int r3, boolean r4) {
        /*
            r2 = this;
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r0 = ""
            r4.<init>(r0)
            java.lang.StringBuilder r4 = r4.append(r3)
            java.lang.String r4 = r4.toString()
            java.lang.String r0 = "rssi"
            android.util.Log.e(r0, r4)
            boolean r4 = r2.stLoc
            r0 = 0
            if (r4 == 0) goto L_0x0030
            if (r3 <= 0) goto L_0x002d
            r4 = 1
            r2.isSound = r4
            int r0 = 100 - r3
            int r0 = r0 * 30
            r2.soundInt = r0
            com.ruddersoft.rfidscanner.MainActivity r0 = r2.mContext
            r0.playSound(r4)
            r2.startSound()
            goto L_0x0033
        L_0x002d:
            r2.isSound = r0
            goto L_0x0032
        L_0x0030:
            r2.isSound = r0
        L_0x0032:
            r3 = r0
        L_0x0033:
            int r4 = r3 / 3
            int r4 = r4 + r3
            r0 = 100
            if (r4 <= r0) goto L_0x003b
            r4 = r0
        L_0x003b:
            java.lang.String r0 = r2.epc
            java.lang.String r4 = java.lang.String.valueOf(r4)
            r2.addEPCToList(r0, r4)
            com.ruddersoft.rfidscanner.databinding.FragmentSingleSearchBinding r4 = r2.binding
            android.widget.TextView r4 = r4.tvRssi
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "RSSI = "
            r0.<init>(r1)
            java.lang.StringBuilder r3 = r0.append(r3)
            java.lang.String r3 = r3.toString()
            r4.setText(r3)
            return

            throw new UnsupportedOperationException("Method not decompiled: com.ruddersoft.rfidscanner.views.fragments.SingleSearch.startLocationLogic(int, boolean):void");
         */

        addEPCToList(epc, java.lang.String.valueOf(r4));
        java.lang.StringBuilder r0 = new java.lang.StringBuilder();
        java.lang.String r1 = "RSSI = ";
        binding.tvRssi.setText(r1 + r0.append(r3).toString());
    }

    public void startLocationLogic(int rssi, boolean isActive) {
        String logMessage = String.valueOf(rssi);
        Log.e("rssi", logMessage);

        boolean isSound = false;
        if (stLoc) {
            if (rssi > 0) {
                isSound = true;
                int soundDelay = (100 - rssi) * 30;
                soundInt = soundDelay;
                /*mContext.playSound(true);*/
                startSound();
            } else {
                isSound = false;
            }
        }

        this.isSound = isSound;

        int adjustedRssi = rssi / 3 + rssi;
        if (adjustedRssi > 100) {
            adjustedRssi = 100;
        }

        addEPCToList(epc, String.valueOf(adjustedRssi));

        String rssiText = "RSSI = " + rssi;
        binding.tvRssi.setText(rssiText);
    }

    public void startSound() {
        this.handler.postDelayed(() -> {
            if (SingleSearch.this.isSound) {
                SingleSearch.this.startSound();
            }
        }, (long) this.soundInt);
    }

    private void addEPCToList(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            if (Float.valueOf(str2).floatValue() > 90.0f) {
                this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_4_bar));
            } else if (Float.valueOf(str2).floatValue() > 80.0f) {
                this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_4_bar));
            } else if (Float.valueOf(str2).floatValue() > 70.0f) {
                this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_4_bar));
            } else if (Float.valueOf(str2).floatValue() > 50.0f) {
                this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_3_bar));
            } else if (Float.valueOf(str2).floatValue() > 30.0f) {
                this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_2_bar));
            } else if (Float.valueOf(str2).floatValue() > 10.0f) {
                this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_1_bar));
            } else {
                try {
                    this.binding.ivRange.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_wifi_0_bar));
                } catch (Exception unused) {
                }
            }
            Log.i("RSSIs: ", str2);
        }
    }

    public void stopLocation() {
        this.stLoc = false;
        if (this.mContext.isBTDevice.booleanValue()) {
            if (this.mContext.isBtConnect) {
                ReaderClass.mBtReader.stopLocation();
            }
        } else if (this.mContext.isC5Device.booleanValue()) {
            ReaderClass.mReader.stopLocation();
        }
    }
}
