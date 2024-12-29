package com.example.rfidapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;

import com.example.rfidapp.activity.DeviceListActivity;
import com.example.rfidapp.fragment.Dashboard;
import com.example.rfidapp.fragment.KeyDownFragment;
import com.example.rfidapp.util.FileUtils;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.SPUtils;
import com.example.rfidapp.util.Util;
import com.example.rfidapp.util.Utils;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.views.UhfInfo;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.barcode.BarcodeFactory;
import com.rscja.deviceapi.RFIDWithUHFBLE;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.deviceapi.interfaces.ConnectionStatus;
import com.rscja.deviceapi.interfaces.ConnectionStatusCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReaderClass extends AppCompatActivity {
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 100;
    private static final int BLUETOOTH_PERMISSION_REQUEST = 103;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 102;
    private static final int RECONNECT_NUM = Integer.MAX_VALUE;
    private static final int REQUEST_ACTION_LOCATION_SETTINGS = 3;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_SELECT_DEVICE = 1;
    private static final int RUNNING_DISCONNECT_TIMER = 10;
    public static final String SHOW_HISTORY_CONNECTED_LIST = "showHistoryConnectedList";
    private static final String TAG = "MainActivity";
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 101;
    public static int barcodeResultCode = 123;
    public static RFIDWithUHFBLE mBtReader;
    public static RFIDWithUHFUART mReader;
    private AudioManager am;
    public BarcodeDecoder barcodeDecoder = BarcodeFactory.getInstance().getBarcodeDecoder();
    public MenuItem btItem;
    BTStatus btStatus = new BTStatus();
    private Button btn_connect;
    private Button btn_search;
    public List<IConnectStatus> connectStatusList = new ArrayList();
    public KeyDownFragment currentFrag = null;
    Dashboard dashboard;
    AlertDialog dialog;
    public String epc = "";
    private FragmentManager fm;
    public Boolean isBTDevice = false;
    public boolean isBtConnect = false;
    public Boolean isC5Device = false;
    public boolean isScanning = false;
    private long lastTouchTime = System.currentTimeMillis();
    public BluetoothAdapter mBtAdapter = null;
    public BluetoothDevice mDevice = null;
    private Timer mDisconnectTimer = new Timer();
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 10) {
                ReaderClass.this.formatConnectButton(((Long) message.obj).longValue());
            }
        }
    };
    public boolean mIsActiveDisconnect = true;
    public int mReConnectCount = Integer.MAX_VALUE;
    private FragmentTabHost mTabHost;
    public long period = 30000;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                ReaderClass.this.isBtConnect = true;
                ReaderClass.this.checkBTConnect();
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action) || "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED".equals(action)) {
            } else {
                if ("android.bluetooth.device.action.FOUND".equals(action)) {
                    ReaderClass.this.isBtConnect = true;
                    ReaderClass.this.checkBTConnect();
                } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                    ReaderClass.this.isBtConnect = false;
                    ReaderClass.this.btItem.setIcon(R.drawable.bt_d);
                }
            }
        }
    };
    public String remoteBTAdd = "";
    public String remoteBTName = "";
    HashMap<Integer, Integer> soundMap = new HashMap<>();
    private SoundPool soundPool;
    String srlno = "";
    public long timeCountCur;
    private DisconnectTimerTask timerTask;
    private Toast toast;
    private TextView tvAddress;
    public UhfInfo uhfInfo = new UhfInfo();
    Util util;
    private float volumnRatio;

    public interface IConnectStatus {
        void getStatus(ConnectionStatus connectionStatus);
    }

    static  long access$322(ReaderClass readerClass, long j) {
        long j2 = readerClass.timeCountCur - j;
        readerClass.timeCountCur = j2;
        return j2;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_reader_class);
        PreferenceManager.init(this);
        this.util = new Util(this);
        allowPermissionForFile();
        this.dashboard = new Dashboard();
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("null") || PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("")) {
            if (Build.MANUFACTURER.equalsIgnoreCase("Chainway")) {
                PreferenceManager.setStringValue(Constants.GET_DEVICE, ExifInterface.GPS_MEASUREMENT_2D);
                Toast.makeText(this, "Your device is " + Build.MODEL, Toast.LENGTH_LONG).show();
            } else {
                PreferenceManager.setStringValue(Constants.GET_DEVICE, "1");
                initBtUHF();
            }
        }
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1")) {
            this.isBTDevice = true;
            if (!this.isBtConnect) {
                /*this.dialog = new AlertDialog.Builder(this).setTitle((CharSequence) "Alert").setMessage((CharSequence) "It is not a UHF enabled device. Please connect UHF device via bluetooth to use UHF functionality").setCancelable(false).setPositiveButton((CharSequence) "Ok", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();*/
            }
            initBtUHF();
        } else if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
            this.isC5Device = true;
            initUHF();
        } else {
            this.isBTDevice = false;
            this.isC5Device = false;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        getApplication().registerReceiver(this.receiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopInventory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        close();
        freeReaderRes();
    }

    private void freeReaderRes() {
        RFIDWithUHFUART rFIDWithUHFUART = mReader;
        if (rFIDWithUHFUART != null) {
            rFIDWithUHFUART.free();
            return;
        }
        RFIDWithUHFBLE rFIDWithUHFBLE = mBtReader;
        if (rFIDWithUHFBLE != null) {
            rFIDWithUHFBLE.free();
        }
    }

    public boolean stopInventory() {
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase("1") && this.isBtConnect) {
            return mBtReader.stopInventory();
        }
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
            return mReader.stopInventory();
        }
        return false;
    }

    public void initSound() {
        this.soundPool = new SoundPool(10, 3, 5);
        this.soundMap.put(1, Integer.valueOf(this.soundPool.load(this, R.raw.barcodebeep, 1)));
        this.soundMap.put(2, Integer.valueOf(this.soundPool.load(this, R.raw.serror, 1)));
        this.am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public void playSound(int i) {
        this.volumnRatio = ((float) this.am.getStreamVolume(3)) / ((float) this.am.getStreamMaxVolume(3));
        try {
            SoundPool soundPool2 = this.soundPool;
            int intValue = this.soundMap.get(Integer.valueOf(i)).intValue();
            float f = this.volumnRatio;
            soundPool2.play(intValue, f, f, 1, 0, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseSoundPool() {
        SoundPool soundPool2 = this.soundPool;
        if (soundPool2 != null) {
            soundPool2.release();
            this.soundPool = null;
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        KeyDownFragment keyDownFragment;
        Log.e("as_con", "key code is :" + i);
        if (i != 293) {
            return super.onKeyDown(i, keyEvent);
        }
        if (keyEvent.getRepeatCount() == 0 && (keyDownFragment = this.currentFrag) != null) {
            keyDownFragment.onMyKeyDown();
        }
        return true;
    }

    public void initUHF() {
        try {
            mReader = getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mReader != null) {
            new InitTask().execute(new String[0]);
        }
    }

    public static RFIDWithUHFUART getInstance() throws ConfigurationException {
        if (mReader == null) {
            mReader = RFIDWithUHFUART.getInstance();
        }
        return mReader;
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        public InitTask() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public Boolean doInBackground(String... strArr) {
            try {
                ReaderClass.mBtReader.free();
            } catch (Exception unused) {
            }
            return Boolean.valueOf(ReaderClass.mReader.init());
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
        }
    }

    public void stopReadingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Stop Reading")
                .setMessage("Please Stop Reading First")
                .setPositiveButton("ok", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    public void initBtUHF() {
        try {
            RFIDWithUHFBLE instance = RFIDWithUHFBLE.getInstance();
            mBtReader = instance;
            if (instance != null) {
                new InitBtReader().execute(new String[0]);
            }
        } catch (Exception unused) {
        }
    }

    public void showBluetoothDevice(boolean z) {
        Log.e("as_con", "Bluetooth scanning is start....");
        if (this.mBtAdapter == null) {
            showToast("Bluetooth is not available");
        }
        if (!this.mBtAdapter.isEnabled()) {
            Log.e("as_con", "Bluetooth IS NOT ENABLED ....");
            Log.i(TAG, "onClick - BT not enabled yet");
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 2);
            return;
        }
        Log.e("as_con", "Bluetooth IS NOT ENABLED 2....");
        Intent intent = new Intent(this, DeviceListActivity.class);
        intent.putExtra(SHOW_HISTORY_CONNECTED_LIST, z);
        startActivityForResult(intent, 1);
        cancelDisconnectTimer();
    }

    public void connect(String str) {
        RFIDWithUHFBLE rFIDWithUHFBLE = mBtReader;
        if (rFIDWithUHFBLE != null && rFIDWithUHFBLE.getConnectStatus() == ConnectionStatus.CONNECTING) {
            showToast("2131951684");
        } else if (mBtReader != null) {
            PreferenceManager.setStringValue(Constants.BTDEVICE, str);
            mBtReader.connect(str, this.btStatus);
            new Handler().postDelayed(() -> mConnect(), 2500);
        }
    }

    public void mConnect() {
        if (mBtReader.getConnectStatus().equals(ConnectionStatus.CONNECTED)) {
            this.isBtConnect = true;
            this.btItem.setIcon(R.drawable.bt_e);
        }
    }

    public void disconnect(boolean z) {
        cancelDisconnectTimer();
        PreferenceManager.setStringValue(Constants.BTDEVICE, "");
        this.mIsActiveDisconnect = z;
        try {
            mBtReader.disconnect();
        } catch (Exception unused) {
        }
        this.btItem.setIcon(R.drawable.ic_signal_wifi_0_bar);
    }

    public void reConnect(String str) {
        if (!this.mIsActiveDisconnect && this.mReConnectCount > 0) {
            connect(str);
            this.mReConnectCount--;
        }
    }


    public boolean shouldShowDisconnected() {
        return this.mIsActiveDisconnect || this.mReConnectCount == 0;
    }


    public void formatConnectButton(long j) {
        if (mBtReader.getConnectStatus() == ConnectionStatus.CONNECTED && !this.isScanning && System.currentTimeMillis() - this.lastTouchTime > 30000 && this.timerTask != null) {
            long j2 = (j / 1000) / 60;
        }
    }

    public void saveConnectedDevice(String str, String str2) {
        ArrayList<String[]> readXmlList = FileUtils.readXmlList();
        int i = 0;
        while (true) {
            if (i >= readXmlList.size()) {
                break;
            } else if (str.equals(readXmlList.get(i)[0])) {
                readXmlList.remove(readXmlList.get(i));
                break;
            } else {
                i++;
            }
        }
        readXmlList.add(0, new String[]{str, str2});
        FileUtils.saveXmlList(readXmlList);
    }

    public void initUI() {
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        this.fm = getSupportFragmentManager();
        /*this.mTabHost = (FragmentTabHost) findViewById(16908306);*/
    }

    public boolean checkLocationEnable() {
        boolean z;
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 100);
            z = false;
        } else {
            z = true;
        }
        if (!isLocationEnabled()) {
            Utils.alert((Activity) this, R.string.get_location_permission, getString(R.string.tips_open_the_ocation_permission), R.drawable.webtext, (dialogInterface, i) -> this.checkLocationEnable(dialogInterface, i));
        }
        return z;
    }

    public void checkLocationEnable(DialogInterface dialogInterface, int i) {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 3);
    }

    private boolean isLocationEnabled() {
        try {
            return Settings.Secure.getInt(getContentResolver(), "location_mode") != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void startDisconnectTimer(long j) {
        this.timeCountCur = j;
        DisconnectTimerTask disconnectTimerTask = new DisconnectTimerTask();
        this.timerTask = disconnectTimerTask;
        this.mDisconnectTimer.schedule(disconnectTimerTask, 0, this.period);
    }

    public void cancelDisconnectTimer() {
        this.timeCountCur = 0;
        DisconnectTimerTask disconnectTimerTask = this.timerTask;
        if (disconnectTimerTask != null) {
            disconnectTimerTask.cancel();
            this.timerTask = null;
        }
    }

    public void resetDisconnectTime() {
        long sPLong = SPUtils.getInstance(getApplicationContext()).getSPLong(SPUtils.DISCONNECT_TIME, 0);
        this.timeCountCur = sPLong;
        if (sPLong > 0) {
            formatConnectButton(sPLong);
        }
    }

    public void showToast(String str) {
        Toast toast2 = this.toast;
        if (toast2 != null) {
            toast2.cancel();
        }
        this.toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
    }

    private boolean isBLEEnabled() {
        try {
            return this.mBtAdapter.isEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("as_con", "bt enable?  " + e.toString());
            return false;
        }
    }

    public void checkBTConnect() {
        try {
            PreferenceManager.getStringValue(Constants.BTDEVICE);
            if (PreferenceManager.getStringValue(Constants.BTDEVICE).length() == 0) {
                this.isBtConnect = false;
            } else {
                new Handler().postDelayed(() -> bTConnect(), 1000);
            }
        } catch (Exception unused) {
        }
    }

    public  void bTConnect() {
        RFIDWithUHFBLE rFIDWithUHFBLE = mBtReader;
        if (rFIDWithUHFBLE == null || rFIDWithUHFBLE.getConnectStatus() != ConnectionStatus.CONNECTED) {
            connect(PreferenceManager.getStringValue(Constants.BTDEVICE));
            return;
        }
        this.isBtConnect = true;
        this.btItem.setIcon(R.drawable.bt_e);
    }

    public boolean checkBLEEnable() {
        boolean z;
        if (Build.VERSION.SDK_INT < 31 || checkSelfPermission("android.permission.BLUETOOTH_SCAN") == PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.BLUETOOTH_CONNECT") == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            z = true;
        } else {
            requestPermissions(new String[]{"android.permission.BLUETOOTH_SCAN", "android.permission.BLUETOOTH_CONNECT"}, 103);
            z = false;
        }
        if (!isBLEEnabled()) {
            Utils.alert((Activity) this, (int) R.string.get_location_permission, getString(R.string.tips_open_the_ocation_permission), (int) R.drawable.webtext, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ReaderClass.this.startActivityForResult(new Intent("android.settings.BLUETOOTH_SETTINGS"), 103);
                }
            });
        }
        return z;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkBTConnect();
    }

    public class InitBtReader extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        public InitBtReader() {
        }

        @Override
        public Boolean doInBackground(String... strArr) {
            try {
                ReaderClass.mReader.free();
            } catch (Exception unused) {
            }
            return Boolean.valueOf(ReaderClass.mBtReader.init(ReaderClass.this));
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            this.mypDialog.cancel();
            if (!bool.booleanValue()) {
                Toast.makeText(ReaderClass.this, "init fail", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            ProgressDialog progressDialog = new ProgressDialog(ReaderClass.this);
            this.mypDialog = progressDialog;
            progressDialog.setProgressStyle(0);
            this.mypDialog.setMessage("init...");
            this.mypDialog.setCanceledOnTouchOutside(false);
            this.mypDialog.show();
        }
    }

    public class BTStatus implements ConnectionStatusCallback<Object> {
        public BTStatus() {
        }

        public void getStatus(final ConnectionStatus connectionStatus, final Object obj) {
            ReaderClass.this.runOnUiThread(new Runnable() {
                public void run() {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) obj;
                    ReaderClass.this.remoteBTName = "";
                    ReaderClass.this.remoteBTAdd = "";
                    if (connectionStatus == ConnectionStatus.CONNECTED) {
                        ReaderClass.this.remoteBTAdd = bluetoothDevice.getAddress();
                        if (ReaderClass.this.shouldShowDisconnected()) {
                            ReaderClass.this.showToast("2131951683");
                        }
                        long unused = ReaderClass.this.timeCountCur = SPUtils.getInstance(ReaderClass.this.getApplicationContext()).getSPLong(SPUtils.DISCONNECT_TIME, 0);
                        if (ReaderClass.this.timeCountCur > 0) {
                            ReaderClass.this.startDisconnectTimer(ReaderClass.this.timeCountCur);
                        } else {
                            ReaderClass.this.formatConnectButton(ReaderClass.this.timeCountCur);
                        }
                        if (!TextUtils.isEmpty(ReaderClass.this.remoteBTAdd)) {
                            ReaderClass.this.saveConnectedDevice(ReaderClass.this.remoteBTAdd, ReaderClass.this.remoteBTName);
                        }
                        boolean unused2 = ReaderClass.this.mIsActiveDisconnect = false;
                        int unused3 = ReaderClass.this.mReConnectCount = Integer.MAX_VALUE;
                    } else if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                        ReaderClass.this.cancelDisconnectTimer();
                        ReaderClass.this.formatConnectButton(ReaderClass.this.timeCountCur);
                        if (bluetoothDevice != null) {
                            ReaderClass.this.remoteBTAdd = bluetoothDevice.getAddress();
                        }
                        if (ReaderClass.this.shouldShowDisconnected()) {
                            ReaderClass.this.showToast("2131951687");
                        }
                        boolean sPBoolean = SPUtils.getInstance(ReaderClass.this.getApplicationContext()).getSPBoolean(SPUtils.AUTO_RECONNECT, false);
                        if (ReaderClass.this.mDevice != null && sPBoolean) {
                            ReaderClass.this.reConnect(ReaderClass.this.mDevice.getAddress());
                        }
                    }
                    for (IConnectStatus iConnectStatus : ReaderClass.this.connectStatusList) {
                        if (iConnectStatus != null) {
                            iConnectStatus.getStatus(connectionStatus);
                        }
                    }
                }
            });
        }
    }

    private class DisconnectTimerTask extends TimerTask {
        private DisconnectTimerTask() {
        }

        public void run() {
            Log.e(ReaderClass.TAG, "timeCountCur = " + ReaderClass.this.timeCountCur);
            ReaderClass.this.mHandler.sendMessage(ReaderClass.this.mHandler.obtainMessage(10, Long.valueOf(ReaderClass.this.timeCountCur)));
            if (ReaderClass.this.isScanning) {
                ReaderClass.this.resetDisconnectTime();
            } else if (ReaderClass.this.timeCountCur <= 0) {
                ReaderClass.this.disconnect(true);
            }
            ReaderClass readerClass = ReaderClass.this;
            ReaderClass.access$322(readerClass, readerClass.period);
        }
    }

    public void stop() {
        this.barcodeDecoder.stopScan();
    }

    public void close() {
        this.barcodeDecoder.close();
    }

    private void allowPermissionForFile() {
        if (Build.VERSION.SDK_INT >= 29) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 2);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 2);
        }
    }


}
