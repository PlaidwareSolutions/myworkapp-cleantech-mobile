package com.example.rfidapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.activity.AboutActivity;
import com.example.rfidapp.activity.AccountSettingsActivity;
import com.example.rfidapp.activity.DeviceSettingsActivity;
import com.example.rfidapp.activity.LoginActivity;
import com.example.rfidapp.activity.SettingsActivity;
import com.example.rfidapp.databinding.FragmentSettingsBinding;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.SharedPrefs;
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

        binding.about.setOnClickListener(view -> startActivity(new Intent(requireContext(), AboutActivity.class)));

        binding.accountSetting.setOnClickListener(view -> startActivity(new Intent(requireContext(), AccountSettingsActivity.class)));

        binding.deviceSeetings.setOnClickListener(view -> startActivity(new Intent(requireContext(), DeviceSettingsActivity.class)));
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
