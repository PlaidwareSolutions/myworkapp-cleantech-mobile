package com.example.rfidapp.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rfidapp.R;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.BleDeviceAdapter;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.databinding.FragmentBleScanBinding;
import com.example.rfidapp.entity.BleItemEntity;
import com.example.rfidapp.model.BleDeviceModel;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.Util;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.viewmodel.BleHistoryViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rscja.deviceapi.RFIDWithUHFBLE;
import com.rscja.deviceapi.interfaces.ScanBTCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class BleScan extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    public FragmentBleScanBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    public BleDeviceAdapter deviceAdapter;
    private int deviceCounter = 0;
    public List<BleDeviceModel> deviceList;
    String historyId;
    public boolean isScanning = false;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;
    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.FOUND".equals(intent.getAction())) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                short shortExtra = intent.getShortExtra("android.bluetooth.device.extra.RSSI", java.lang.Short.MIN_VALUE);
                if (bluetoothDevice != null) {
                    BleScan.this.deviceList.add(new BleDeviceModel(bluetoothDevice, shortExtra));
                    BleScan.this.deviceAdapter.notifyDataSetChanged();
                    BleScan.access$608(BleScan.this);
                    BleScan.this.updateCounter();
                }
            }
        }
    };
    RFIDWithUHFBLE uhf = RFIDWithUHFBLE.getInstance();

    static int access$608(BleScan bleScan) {
        int i = bleScan.deviceCounter;
        bleScan.deviceCounter = i + 1;
        return i;
    }

    public static BleScan newInstance(String str, String str2) {
        BleScan bleScan = new BleScan();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        bleScan.setArguments(bundle);
        return bleScan;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentBleScanBinding.inflate(layoutInflater, viewGroup, false);
        setHasOptionsMenu(true);
        return this.binding.getRoot();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mContext.setTitle("Scan BLE");
        this.deviceList = new ArrayList();
        this.deviceAdapter = new BleDeviceAdapter(this.deviceList);
        this.binding.bleRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.binding.bleRecyclerView.setAdapter(this.deviceAdapter);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
            this.mContext.initBtUHF();
        }
        if (this.bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return;
        }
        this.binding.btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!PreferenceManager.getStringValue(Constants.BTDEVICE).equals("")) {
                    BleScan.this.mContext.disconnect(true);
                    BleScan.this.scanBleDevice(true);
                    BleScan.this.binding.btnStop.setVisibility(View.VISIBLE);
                    BleScan.this.binding.btnScan.setVisibility(android.view.View.GONE);
                } else if (BleScan.this.isScanning) {
                    Toast.makeText(BleScan.this.mContext, "Already Scanning", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("as_con error", "try new scanning ......");
                    if (BleScan.this.mContext.checkLocationEnable()) {
                        try {
                            Log.e("as_con error", "e.toString()");
                            if (BleScan.this.mContext.checkBLEEnable()) {
                                BleScan.this.scanBleDevice(true);
                                BleScan.this.binding.btnStop.setVisibility(android.view.View.VISIBLE);
                                BleScan.this.binding.btnScan.setVisibility(android.view.View.GONE);
                            }
                        } catch (Exception e) {
                            Log.e("as_con error", e.toString());
                        }
                    } else {
                        Log.e("as_con error", "e.toString()1");
                    }
                }
            }
        });
        this.binding.btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BleScan.this.stopScan();
                BleScan.this.binding.btnStop.setVisibility(android.view.View.GONE);
                BleScan.this.binding.btnScan.setVisibility(android.view.View.VISIBLE);
            }
        });
    }

    public void onPause() {
        super.onPause();
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equalsIgnoreCase(ExifInterface.GPS_MEASUREMENT_2D)) {
            this.mContext.initUHF();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mContext = (MainActivity) context;
            return;
        }
        throw new ClassCastException("Parent activity must be MainActivity");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.ble_menu, menu);
        MenuItem findItem = menu.findItem(R.id.menu_ble_save);
        MenuItem findItem2 = menu.findItem(R.id.menu_ble_history);
        if (!(findItem == null || findItem2 == null)) {
            findItem.setVisible(true);
            findItem2.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem findItem = menu.findItem(R.id.menu_ble_save);
        MenuItem findItem2 = menu.findItem(R.id.menu_ble_history);
        if (findItem != null && findItem2 != null) {
            findItem.setVisible(true);
            findItem2.setVisible(true);
            findItem.setOnMenuItemClickListener(menuItem -> saveAction(menuItem));
            findItem2.setOnMenuItemClickListener(menuItem -> OpenBleItemHistoryName(menuItem));
            super.onPrepareOptionsMenu(menu);
        }
    }

    public boolean saveAction(MenuItem menuItem) {
        if (this.isScanning) {
            Toast.makeText(this.mContext, "First Stop BLE Scanning", android.widget.Toast.LENGTH_SHORT).show();
        } else if (this.deviceList.isEmpty()) {
            Toast.makeText(this.mContext, "BLE List is Empty", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            displayBottomSheet();
        }
        return false;
    }

    public boolean OpenBleItemHistoryName(MenuItem menuItem) {
        this.mContext.frm = 5;
        this.mContext.setFragment(new BleItemHistoryName(), "BLE History List");
        return false;
    }

    public void onDestroyView() {
        super.onDestroyView();
        stopScan();
        this.binding = null;
    }

    private void startScan() {
        this.deviceList.clear();
        this.deviceCounter = 0;
        updateCounter();
        this.deviceAdapter.notifyDataSetChanged();
        if (!this.bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
            return;
        }
        requireContext().registerReceiver(this.scanReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.bluetoothAdapter.startDiscovery();
        this.isScanning = true;
    }

    public void stopScan() {
        if (this.isScanning) {
            this.uhf.stopScanBTDevices();
            this.isScanning = false;
        }
    }

    public void updateCounter() {
        FragmentBleScanBinding fragmentBleScanBinding = this.binding;
        if (fragmentBleScanBinding != null) {
            fragmentBleScanBinding.tvcounter.setText(getString(R.string.device_counter, Integer.valueOf(this.deviceCounter)));
        }
    }

    private void displayBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(LayoutInflater.from(this.mContext).inflate(R.layout.bottomlsheet_ble_save, (ViewGroup) null));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
        ((Button) bottomSheetDialog.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBottomSheet(bottomSheetDialog.findViewById(R.id.et_ble), bottomSheetDialog);
            }
        });
        ((Button) bottomSheetDialog.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_close)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
    }

    public void displayBottomSheet(EditText editText, BottomSheetDialog bottomSheetDialog) {
        ArrayList arrayList = new ArrayList();
        String trim = editText.getText().toString().trim();
        if (trim.isEmpty()) {
            Toast.makeText(requireContext(), "Enter List Name", Toast.LENGTH_SHORT).show();
        } else if (bottomSheetDialog == null || !bottomSheetDialog.isShowing() || editText == null) {
            Toast.makeText(requireContext(), "Please enter list name", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            String generateUniqueId = generateUniqueId();
            this.historyId = generateUniqueId;
            ((BleHistoryViewModel) new ViewModelProvider(requireActivity()).get(BleHistoryViewModel.class)).saveBleEntity(trim, generateUniqueId);
            Log.d("TAG", "Uniq Id: " + this.historyId);
            for (BleDeviceModel next : this.deviceList) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                BleItemEntity bleItemEntity = new BleItemEntity(next.getDevice().getName(), next.getRssi(), next.getDevice().getAddress());
                bleItemEntity.setHistoryID(this.historyId);
                arrayList.add(bleItemEntity);
            }
            saveBleItemsToDatabase(arrayList);
            Toast.makeText(requireContext(), "BLE items saved to database", Toast.LENGTH_SHORT).show();
            this.deviceList.clear();
            this.binding.tvcounter.setText(getString(R.string.device_counter, 0));
            this.deviceAdapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        }
    }

    public void scanBleDevice(boolean z) {
        if (z) {
            this.isScanning = true;
            this.deviceList.clear();
            this.deviceCounter = 0;
            updateCounter();
            this.deviceAdapter.notifyDataSetChanged();
            this.uhf.startScanBTDevices(new ScanBTCallback() {
                public void getDevices(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                    String str = "N/A";
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String name = (bluetoothDevice == null || bluetoothDevice.getName() == null) ? str : bluetoothDevice.getName();
                    if (!(bluetoothDevice == null || bluetoothDevice.getAddress() == null)) {
                        str = bluetoothDevice.getAddress();
                    }
                    BleScan.this.addDevice(new BleDeviceModel(bluetoothDevice, i), i, name, str);
                }
            });
            return;
        }
        this.isScanning = false;
        stopScan();
    }

    public void addDevice(BleDeviceModel bleDeviceModel, int i, String str, String str2) {
        boolean z;
        Iterator<BleDeviceModel> it = this.deviceList.iterator();
        while (true) {
            if (it.hasNext()) {
                if (it.next().getDevice().equals(bleDeviceModel.getDevice())) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        if (!z) {
            BleDeviceModel bleDeviceModel2 = new BleDeviceModel(bleDeviceModel.getDevice(), i);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bleDeviceModel2.getDevice().getName();
            bleDeviceModel2.getDevice().getAddress();
            this.deviceList.add(bleDeviceModel2);
            this.deviceCounter++;
            updateCounter();
        }
        if (!z) {
            this.deviceAdapter.notifyDataSetChanged();
        }
    }

    private void saveBleItemsToDatabase(List<BleItemEntity> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InvDB.getInstance(mContext).bleItemsDao().insertAllBleItems(list);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showToast(requireContext(), "Scanned BLE List saved to History");
                    }
                });
            }
        }


        ).start();
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
