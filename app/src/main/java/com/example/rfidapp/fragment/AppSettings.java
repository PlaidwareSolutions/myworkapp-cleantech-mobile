package com.example.rfidapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.activity.SettingsActivity;
import com.example.rfidapp.databinding.FragmentSettingsBinding;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class AppSettings extends KeyDownFragment {
    static final boolean $assertionsDisabled = false;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int READ_REQUEST_CODE = 123;
    AudioManager audioManager;
    FragmentSettingsBinding binding;
    MenuItem btMenu;
    List<String> listPwr = new ArrayList();
    SettingsActivity mContext;
    private String mParam1;
    private String mParam2;
    public String modelNo = "";
    int power = 6;
    int selectedDevice = 0;
    int volume = 0;

    public static AppSettings newInstance(String str, String str2) {
        AppSettings appSettings = new AppSettings();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        appSettings.setArguments(bundle);
        return appSettings;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentSettingsBinding inflate = FragmentSettingsBinding.inflate(layoutInflater, viewGroup, false);
        binding = inflate;
        return inflate.getRoot();
    }

    public void onActivityCreated(Bundle bundle) {
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        mContext = settingsActivity;
        settingsActivity.currentFrag = this;
        modelNo = Build.MODEL;
        setHasOptionsMenu(true);
        init();
        super.onActivityCreated(bundle);
    }

    public void onResume() {
        mContext.checkBTConnect();
        super.onResume();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(R.id.menu_volume);
        btMenu = menu.findItem(R.id.menu_bt);
        findItem.setVisible(false);
        if (mContext.isBTDevice.booleanValue()) {
            btMenu.setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    public void onMyKeyDown() {
        super.onMyKeyDown();
    }


    public void init() {
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        setPower();
        setVolume();
        binding.rgGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_barcode) {
                selectedDevice = 3;
                PreferenceManager.setStringValue(Constants.GET_DEVICE, ExifInterface.GPS_MEASUREMENT_3D);
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_bluetooth) {
                selectedDevice = 1;
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_screen) {
                if (modelNo.equals("C5P")) {
                    selectedDevice = 2;
                } else {
                    selectedDevice = 3;
                    mContext.highlightToast("Kindly Use RFID Device..", 2);
                    selectedDevice = 1;
                    binding.rbBluetooth.setChecked(true);
                    mContext.highlightToast("Kindly Use C5P Device..", 2);
                }
            }
        });
        binding.rgGroup.setOnClickListener(view -> {
            int i = selectedDevice;
            if (i == 1) {
                if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
                    mContext.isC5Device = false;
                    mContext.isBTDevice = true;
                    if (ReaderClass.mReader != null) {
                        ReaderClass.mReader.free();
                    }
                    btMenu.setVisible(true);
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mContext.initBtUHF();
                    }
                }, 1000);
                PreferenceManager.setStringValue(Constants.GET_DEVICE, "1");
                mContext.highlightToast("R6 Bluetooth Device Selected..", 1);
            } else if (i == 2) {
                if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1")) {
                    mContext.isBTDevice = false;
                    mContext.isC5Device = true;
                    if (SettingsActivity.mBtReader != null) {
                        SettingsActivity.mBtReader.free();
                    }
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mContext.initUHF();
                        }
                    }, 1000);
                    btMenu.setVisible(false);
                }
                mContext.highlightToast("C5 Screen Device Selected..", 1);
                PreferenceManager.setStringValue(Constants.GET_DEVICE, ExifInterface.GPS_MEASUREMENT_2D);
            }
        });
        /*
            r4 = this;
            com.ruddersoft.rfidscanner.MainActivity r0 = r4.mContext
            java.lang.String r1 = "Settings"
            r0.setTitle(r1)
            com.ruddersoft.rfidscanner.MainActivity r0 = r4.mContext
            r0.initSound()
            androidx.fragment.app.FragmentActivity r0 = r4.getActivity()
            java.lang.String r1 = "audio"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.media.AudioManager r0 = (android.media.AudioManager) r0
            r4.audioManager = r0
            r0 = 2
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.Boolean r1 = r1.isC5Device     // Catch:{ Exception -> 0x006d }
            boolean r1 = r1.booleanValue()     // Catch:{ Exception -> 0x006d }
            if (r1 == 0) goto L_0x0029
            r4.setPower()     // Catch:{ Exception -> 0x006d }
            goto L_0x004c
        L_0x0029:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.Boolean r1 = r1.isBTDevice     // Catch:{ Exception -> 0x006d }
            boolean r1 = r1.booleanValue()     // Catch:{ Exception -> 0x006d }
            if (r1 == 0) goto L_0x0045
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            boolean r1 = r1.isBtConnect     // Catch:{ Exception -> 0x006d }
            if (r1 == 0) goto L_0x003d
            r4.setPower()     // Catch:{ Exception -> 0x006d }
            goto L_0x004c
        L_0x003d:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.String r2 = "Please Connect Device First.."
            r1.highlightToast(r2, r0)     // Catch:{ Exception -> 0x006d }
            goto L_0x004c
        L_0x0045:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.String r2 = "Please Use Any RFID Device"
            r1.highlightToast(r2, r0)     // Catch:{ Exception -> 0x006d }
        L_0x004c:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.Boolean r1 = r1.isC5Device     // Catch:{ Exception -> 0x006d }
            boolean r1 = r1.booleanValue()     // Catch:{ Exception -> 0x006d }
            if (r1 != 0) goto L_0x0069
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.Boolean r1 = r1.isBTDevice     // Catch:{ Exception -> 0x006d }
            boolean r1 = r1.booleanValue()     // Catch:{ Exception -> 0x006d }
            if (r1 == 0) goto L_0x0061
            goto L_0x0069
        L_0x0061:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext     // Catch:{ Exception -> 0x006d }
            java.lang.String r2 = "Kindly Use RFID Device"
            r1.highlightToast(r2, r0)     // Catch:{ Exception -> 0x006d }
            goto L_0x0079
        L_0x0069:
            r4.setPower()     // Catch:{ Exception -> 0x006d }
            goto L_0x0079
        L_0x006d:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext
            java.lang.String r2 = "UHF Device Problem, Please Restart App."
            r3 = 0
            android.widget.Toast r1 = android.widget.Toast.makeText(r1, r2, r3)
            r1.show()
        L_0x0079:
            r4.setVolume()
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext
            java.lang.Boolean r1 = r1.isBTDevice
            boolean r1 = r1.booleanValue()
            r2 = 1
            if (r1 == 0) goto L_0x0091
            r4.selectedDevice = r2
            com.ruddersoft.rfidscanner.databinding.FragmentSettingsBinding r0 = r4.binding
            android.widget.RadioButton r0 = r0.rbBluetooth
            r0.setChecked(r2)
            goto L_0x00af
        L_0x0091:
            com.ruddersoft.rfidscanner.MainActivity r1 = r4.mContext
            java.lang.Boolean r1 = r1.isC5Device
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x00a5
            r4.selectedDevice = r0
            com.ruddersoft.rfidscanner.databinding.FragmentSettingsBinding r0 = r4.binding
            android.widget.RadioButton r0 = r0.rbScreen
            r0.setChecked(r2)
            goto L_0x00af
        L_0x00a5:
            r0 = 3
            r4.selectedDevice = r0
            com.ruddersoft.rfidscanner.databinding.FragmentSettingsBinding r0 = r4.binding
            android.widget.RadioButton r0 = r0.rbBarcode
            r0.setChecked(r2)
        L_0x00af:

            com.ruddersoft.rfidscanner.databinding.FragmentSettingsBinding r0 = r4.binding
            androidx.cardview.widget.CardView r0 = r0.llExportExl
            com.ruddersoft.rfidscanner.views.fragments.AppSettings$3 r1 = new com.ruddersoft.rfidscanner.views.fragments.AppSettings$3
            r1.<init>()
            r0.setOnClickListener(r1)
            return
        */
//        throw new UnsupportedOperationException("Method not decompiled: com.ruddersoft.rfidscanner.views.fragments.AppSettings.init():void");
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 123 && intent != null) {
            openCsvFile(intent.getData());
        }
    }

    private void openCsvFile(Uri uri) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(uri, "*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showToast("No app available to open the CSV file");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error opening CSV file");
        }
    }

    private void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }

    public void setPower() {
        if (mContext.isC5Device.booleanValue()) {
            power = ReaderClass.mReader.getPower();
        } else if (!mContext.isBTDevice.booleanValue()) {
            mContext.highlightToast("Please Use Any RFID Device", 2);
        } else if (mContext.isBtConnect) {
            power = SettingsActivity.mBtReader.getPower();
        } else {
            mContext.highlightToast("Please Connect Device First..", 2);
        }
        binding.slPwr.setValue(Float.parseFloat("" + power));
        binding.tvPwr.setText(power + " dBm");
        
        binding.slPwr.addOnChangeListener((slider, f, z) -> {
            if ((!PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D) || !ReaderClass.mReader.isWorking()) && (!PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1") || !mContext.isBtConnect || !SettingsActivity.mBtReader.isWorking())) {
                int i = (int) f;
                power = i;
                binding.tvPwr.setText(i + " dBm");
                return;
            }
            mContext.highlightToast("Please Stop Reading First", 2);
        });
        
        binding.btSetPwr.setOnClickListener(view -> {
            if (mContext.isC5Device.booleanValue()) {
                if (ReaderClass.mReader.setPower(power)) {
                    mContext.highlightToast("Power Set Successfully", 1);
                } else {
                    mContext.highlightToast("Power Set Failure", 2);
                }
            } else if (!mContext.isBTDevice.booleanValue()) {
                mContext.highlightToast("Please Use Any RFID Device", 2);
            } else if (!mContext.isBtConnect) {
                mContext.highlightToast("Please Connect Device First..", 2);
            } else if (SettingsActivity.mBtReader.setPower(power)) {
                mContext.highlightToast("Power Set Successfully", 1);
            } else {
                mContext.highlightToast("Power Set Failure", 2);
            }
        });
    }
    
    @SuppressLint("SetTextI18n")
    private void setVolume() {
        binding.slVol.setValueTo((float) audioManager.getStreamMaxVolume(3));
        volume = audioManager.getStreamVolume(3);
        binding.slVol.setValue((float) volume);
        binding.tvVol.setText(volume + " Unit");
        binding.slVol.addOnChangeListener((slider, f, z) -> {
            volume = (int) f;
            binding.tvVol.setText(volume + " Unit");
        });
        binding.btSetVol.setOnClickListener(view -> {
            audioManager.setStreamVolume(3, volume, 0);
            mContext.highlightToast("Volume Set Successfully", 1);
        });
    }
}
