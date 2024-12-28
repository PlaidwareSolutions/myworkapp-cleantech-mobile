package com.example.rfidapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.internal.view.SupportMenu;

import com.example.rfidapp.R;
import com.example.rfidapp.ReaderClass;
import com.example.rfidapp.util.FileUtils;
import com.rscja.deviceapi.RFIDWithUHFBLE;
import com.rscja.deviceapi.interfaces.ScanBTCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceListActivity extends AppCompatActivity {
    private static final long SCAN_PERIOD = 10000;
    public static final String TAG = "DeviceListActivity";
    public Map<String, Integer> devRssiValues;
    private DeviceAdapter deviceAdapter;
    public List<MyDevice> deviceList;
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            MyDevice myDevice = (MyDevice) DeviceListActivity.this.deviceList.get(i);
            DeviceListActivity.this.uhf.stopScanBTDevices();
            if (!TextUtils.isEmpty(myDevice.getAddress().trim())) {
                Bundle bundle = new Bundle();
                bundle.putString("android.bluetooth.device.extra.DEVICE", myDevice.getAddress());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                DeviceListActivity.this.setResult(-1, intent);
                DeviceListActivity.this.finish();
            }
        }
    };
    private TextView mEmptyList;
    private Handler mHandler = new Handler();
    private boolean mScanning;
    private TextView tvTitle;
    RFIDWithUHFBLE uhf = RFIDWithUHFBLE.getInstance();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_device_list);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView((int) R.layout.device_list);
        if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        init();
    }

    private void init() {
        this.tvTitle = (TextView) findViewById(R.id.title_devices);
        this.mEmptyList = (TextView) findViewById(R.id.empty);
        findViewById(R.id.close).setOnClickListener(view -> this.init(view));
        this.devRssiValues = new HashMap();
        this.deviceList = new ArrayList();
        this.deviceAdapter = new DeviceAdapter(this, this.deviceList);
        Button button = (Button) findViewById(R.id.btn_cancel);
        button.setOnClickListener(view -> this.mInit(view));
        Button button2 = (Button) findViewById(R.id.btnClearHistory);
        button2.setOnClickListener(view -> this.init2(view));
        if (getIntent().getBooleanExtra(ReaderClass.SHOW_HISTORY_CONNECTED_LIST, false)) {
            this.tvTitle.setText(R.string.history_connected_device);
            this.mEmptyList.setText(R.string.no_history);
            button.setVisibility(View.GONE);
            for (String[] next : FileUtils.readXmlList()) {
                MyDevice myDevice = new MyDevice(next[0], next[1]);
                Log.e("as_con", "" + myDevice);
                addDevice(myDevice, 0);
            }
        } else {
            this.tvTitle.setText(R.string.select_device);
            this.mEmptyList.setText(R.string.scanning);
            button2.setVisibility(android.view.View.GONE);
            scanLeDevice(true);
        }
        ListView listView = (ListView) findViewById(R.id.new_devices);
        listView.setAdapter(this.deviceAdapter);
        listView.setOnItemClickListener(this.mDeviceClickListener);
    }


    public void init(View view) {
        finish();
    }


    public void mInit(View view) {
        if (!this.mScanning) {
            scanLeDevice(true);
        } else {
            finish();
        }
    }


    public void init2(View view) {
        FileUtils.clearXmlList();
        this.deviceList.clear();
        this.deviceAdapter.notifyDataSetChanged();
        this.mEmptyList.setVisibility(View.VISIBLE);
    }

    private void scanLeDevice(boolean z) {
        Button button = (Button) findViewById(R.id.btn_cancel);
        if (z) {
            this.mHandler.postDelayed(() -> scanLeDevice(button), SCAN_PERIOD);
            this.mScanning = true;
            this.uhf.startScanBTDevices(new ScanBTCallback() {
                public void getDevices(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                    DeviceListActivity.this.runOnUiThread(() -> getDevicesList(bluetoothDevice, i));
                }


                public void getDevicesList(BluetoothDevice bluetoothDevice, int i) {
                    Log.d(DeviceListActivity.TAG, "扫描成功");
                    Log.d(DeviceListActivity.TAG, "扫描成功");
                    if (ActivityCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    DeviceListActivity.this.addDevice(new MyDevice(bluetoothDevice.getAddress(), bluetoothDevice.getName()), i);
                }
            });
            button.setText(R.string.cancel);
            return;
        }
        this.mScanning = false;
        this.uhf.stopScanBTDevices();
        button.setText(R.string.scan);
    }


    public void scanLeDevice(Button button) {
        this.mScanning = false;
        this.uhf.stopScanBTDevices();
        button.setText(R.string.scan);
    }

    public void onStop() {
        super.onStop();
        this.uhf.stopScanBTDevices();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.uhf.stopScanBTDevices();
    }

    public void addDevice(MyDevice myDevice, int i) {
        boolean z;
        Iterator<MyDevice> it = this.deviceList.iterator();
        while (true) {
            if (it.hasNext()) {
                if (it.next().getAddress().equals(myDevice.getAddress())) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        this.devRssiValues.put(myDevice.getAddress(), Integer.valueOf(i));
        if (!z) {
            this.deviceList.add(myDevice);
            this.mEmptyList.setVisibility(View.GONE);
        }
        Collections.sort(this.deviceList, (obj, obj2) -> this.addDevice((DeviceListActivity.MyDevice) obj, (DeviceListActivity.MyDevice) obj2));
        if (!z) {
            this.deviceAdapter.notifyDataSetChanged();
        }
    }


    public int addDevice(MyDevice myDevice, MyDevice myDevice2) {
        String address = myDevice.getAddress();
        String address2 = myDevice2.getAddress();
        return Integer.compare(this.devRssiValues.get(address2).intValue(), this.devRssiValues.get(address).intValue());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "scanLeDevice==============>");
        scanLeDevice(false);
    }

    public class MyDevice {
        private String address;
        private int bondState;
        private String name;

        public MyDevice() {
        }

        public MyDevice(String str, String str2) {
            this.address = str;
            this.name = str2;
        }

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String str) {
            this.address = str;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public int getBondState() {
            return this.bondState;
        }

        public void setBondState(int i) {
            this.bondState = i;
        }
    }

    class DeviceAdapter extends BaseAdapter {
        Context context;
        List<MyDevice> devices;
        LayoutInflater inflater;

        public long getItemId(int i) {
            return (long) i;
        }

        public DeviceAdapter(Context context2, List<MyDevice> list) {
            this.context = context2;
            this.inflater = LayoutInflater.from(context2);
            this.devices = list;
        }

        public int getCount() {
            return this.devices.size();
        }

        public Object getItem(int i) {
            return this.devices.get(i);
        }

        @SuppressLint("RestrictedApi")
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewGroup viewGroup2;
            if (view != null) {
                viewGroup2 = (ViewGroup) view;
            } else {
                viewGroup2 = (ViewGroup) this.inflater.inflate(R.layout.device_element, (ViewGroup) null);
            }
            MyDevice myDevice = this.devices.get(i);
            TextView textView = (TextView) viewGroup2.findViewById(R.id.address);
            TextView textView2 = (TextView) viewGroup2.findViewById(R.id.name);
            TextView textView3 = (TextView) viewGroup2.findViewById(R.id.paired);
            TextView textView4 = (TextView) viewGroup2.findViewById(R.id.rssi);
            int intValue = ((Integer) DeviceListActivity.this.devRssiValues.get(myDevice.getAddress())).intValue();
            if (intValue != 0) {
                textView4.setText(String.format("Rssi = %d", new Object[]{Integer.valueOf(intValue)}));
                textView4.setTextColor(-16777216);
                textView4.setVisibility(View.VISIBLE);
            }
            textView2.setText(myDevice.getName());
            textView2.setTextColor(-16777216);
            textView.setText(myDevice.getAddress());
            textView.setTextColor(-16777216);
            if (myDevice.getBondState() == 12) {
                Log.i(DeviceListActivity.TAG, "device::" + myDevice.getName());
                textView3.setText(R.string.paired);
                textView3.setTextColor(SupportMenu.CATEGORY_MASK);
                textView3.setVisibility(android.view.View.VISIBLE);
            } else {
                textView3.setVisibility(android.view.View.GONE);
            }
            return viewGroup2;
        }
    }
}
