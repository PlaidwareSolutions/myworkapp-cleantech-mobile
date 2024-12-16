package com.example.rfidapp.fragment;

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
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.databinding.FragmentSettingsBinding;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;
import com.google.android.material.slider.Slider;

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
    MainActivity mContext;
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
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentSettingsBinding inflate = FragmentSettingsBinding.inflate(layoutInflater, viewGroup, false);
        this.binding = inflate;
        return inflate.getRoot();
    }

    public void onActivityCreated(Bundle bundle) {
        MainActivity mainActivity = (MainActivity) getActivity();
        this.mContext = mainActivity;
        mainActivity.currentFrag = this;
        this.modelNo = Build.MODEL;
        setHasOptionsMenu(true);
       /* init();*/
        super.onActivityCreated(bundle);
    }

    public void onResume() {
        this.mContext.checkBTConnect();
        super.onResume();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(R.id.menu_volume);
        this.btMenu = menu.findItem(R.id.menu_bt);
        findItem.setVisible(false);
        if (this.mContext.isBTDevice.booleanValue()) {
            this.btMenu.setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    public void onMyKeyDown() {
        super.onMyKeyDown();
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0091  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void init() {
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
            android.widget.RadioGroup r0 = r0.rgGroup
            com.ruddersoft.rfidscanner.views.fragments.AppSettings$$ExternalSyntheticLambda0 r1 = new com.ruddersoft.rfidscanner.views.fragments.AppSettings$$ExternalSyntheticLambda0
            r1.<init>(r4)
            r0.setOnCheckedChangeListener(r1)
            com.ruddersoft.rfidscanner.databinding.FragmentSettingsBinding r0 = r4.binding
            android.widget.TextView r0 = r0.btSelect
            com.ruddersoft.rfidscanner.views.fragments.AppSettings$$ExternalSyntheticLambda1 r1 = new com.ruddersoft.rfidscanner.views.fragments.AppSettings$$ExternalSyntheticLambda1
            r1.<init>(r4)
            r0.setOnClickListener(r1)
            com.ruddersoft.rfidscanner.databinding.FragmentSettingsBinding r0 = r4.binding
            androidx.cardview.widget.CardView r0 = r0.llExportExl
            com.ruddersoft.rfidscanner.views.fragments.AppSettings$3 r1 = new com.ruddersoft.rfidscanner.views.fragments.AppSettings$3
            r1.<init>()
            r0.setOnClickListener(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ruddersoft.rfidscanner.views.fragments.AppSettings.init():void");
    }


    public  void m528lambda$init$0$comruddersoftrfidscannerviewsfragmentsAppSettings(RadioGroup radioGroup, int i) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_barcode) {
            this.selectedDevice = 3;
            PreferenceManager.setStringValue(Constants.GET_DEVICE, ExifInterface.GPS_MEASUREMENT_3D);
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_bluetooth) {
            this.selectedDevice = 1;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_screen) {
            if (this.modelNo.equals("C5P")) {
                this.selectedDevice = 2;
            } else {
                this.selectedDevice = 3;
                this.mContext.highlightToast("Kindly Use RFID Device..", 2);
                this.selectedDevice = 1;
                this.binding.rbBluetooth.setChecked(true);
                this.mContext.highlightToast("Kindly Use C5P Device..", 2);
            }
        }
    }


    public void m529lambda$init$1$comruddersoftrfidscannerviewsfragmentsAppSettings(View view) {
        int i = this.selectedDevice;
        if (i == 1) {
            if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
                this.mContext.isC5Device = false;
                this.mContext.isBTDevice = true;
                if (ReaderClass.mReader != null) {
                    ReaderClass.mReader.free();
                }
                this.btMenu.setVisible(true);
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    AppSettings.this.mContext.initBtUHF();
                }
            }, 1000);
            PreferenceManager.setStringValue(Constants.GET_DEVICE, "1");
            this.mContext.highlightToast("R6 Bluetooth Device Selected..", 1);
        } else if (i == 2) {
            if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1")) {
                this.mContext.isBTDevice = false;
                this.mContext.isC5Device = true;
                if (ReaderClass.mBtReader != null) {
                    ReaderClass.mBtReader.free();
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        AppSettings.this.mContext.initUHF();
                    }
                }, 1000);
                this.btMenu.setVisible(false);
            }
            this.mContext.highlightToast("C5 Screen Device Selected..", 1);
            PreferenceManager.setStringValue(Constants.GET_DEVICE, ExifInterface.GPS_MEASUREMENT_2D);
        }
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
            if (intent.resolveActivity(this.mContext.getPackageManager()) != null) {
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
        Toast.makeText(this.mContext, str, Toast.LENGTH_LONG).show();
    }

    public void setPower() {
        if (this.mContext.isC5Device.booleanValue()) {
            this.power = ReaderClass.mReader.getPower();
        } else if (!this.mContext.isBTDevice.booleanValue()) {
            this.mContext.highlightToast("Please Use Any RFID Device", 2);
        } else if (this.mContext.isBtConnect) {
            this.power = MainActivity.mBtReader.getPower();
        } else {
            this.mContext.highlightToast("Please Connect Device First..", 2);
        }
        this.binding.slPwr.setValue(Float.parseFloat("" + this.power));
        this.binding.tvPwr.setText(this.power + " dBm");
        this.binding.slPwr.addOnChangeListener((slider, f, z) -> {
            this.m530lambda$setPower$2$comruddersoftrfidscannerviewsfragmentsAppSettings(slider, f, z);
        });
        this.binding.btSetPwr.setOnClickListener(view -> {
            this.m531lambda$setPower$3$comruddersoftrfidscannerviewsfragmentsAppSettings(view);
        });    }


    public void m530lambda$setPower$2$comruddersoftrfidscannerviewsfragmentsAppSettings(Slider slider, float f, boolean z) {
        if ((!PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D) || !ReaderClass.mReader.isWorking()) && (!PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1") || !this.mContext.isBtConnect || !ReaderClass.mBtReader.isWorking())) {
            int i = (int) f;
            this.power = i;
            this.binding.tvPwr.setText(i + " dBm");
            return;
        }
        this.mContext.highlightToast("Please Stop Reading First", 2);
    }

    public void m531lambda$setPower$3$comruddersoftrfidscannerviewsfragmentsAppSettings(View view) {
        if (this.mContext.isC5Device.booleanValue()) {
            if (ReaderClass.mReader.setPower(this.power)) {
                this.mContext.highlightToast("Power Set Successfull", 1);
            } else {
                this.mContext.highlightToast("Power Set Failure", 2);
            }
        } else if (!this.mContext.isBTDevice.booleanValue()) {
            this.mContext.highlightToast("Please Use Any RFID Device", 2);
        } else if (!this.mContext.isBtConnect) {
            this.mContext.highlightToast("Please Connect Device First..", 2);
        } else if (ReaderClass.mBtReader.setPower(this.power)) {
            this.mContext.highlightToast("Power Set Successfull", 1);
        } else {
            this.mContext.highlightToast("Power Set Failure", 2);
        }
    }

    private void setVolume() {
        this.binding.slVol.setValueTo((float) this.audioManager.getStreamMaxVolume(3));
        this.volume = this.audioManager.getStreamVolume(3);
        this.binding.slVol.setValue((float) this.volume);
        this.binding.tvVol.setText(this.volume + " Unit");
        this.binding.slVol.addOnChangeListener((slider, f, z) -> {
            this.m532lambda$setVolume$4$comruddersoftrfidscannerviewsfragmentsAppSettings(slider, f, z);
        });        this.binding.btSetVol.setOnClickListener(view -> {
            this.m533lambda$setVolume$5$comruddersoftrfidscannerviewsfragmentsAppSettings(view);
        });    }


    public void m532lambda$setVolume$4$comruddersoftrfidscannerviewsfragmentsAppSettings(Slider slider, float f, boolean z) {
        this.volume = (int) f;
        this.binding.tvVol.setText(this.volume + " Unit");
    }


    public void m533lambda$setVolume$5$comruddersoftrfidscannerviewsfragmentsAppSettings(View view) {
        this.audioManager.setStreamVolume(3, this.volume, 0);
        this.mContext.highlightToast("Volume Set Successfull", 1);
    }
}
