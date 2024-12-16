package com.example.rfidapp.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rfidapp.R;
import com.example.rfidapp.model.BleDeviceModel;

import java.util.List;
import java.util.Random;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.DeviceViewHolder> {
    private List<BleDeviceModel> devices;

    public double converter(int i) {
        double d = (double) (i + 20);
        return (((d * d) / 10000.0d) * 10.0d) / 5.0d;
    }

    public BleDeviceAdapter(List<BleDeviceModel> list) {
        this.devices = list;
    }

    public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new DeviceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ble_scan_list, viewGroup, false));
    }

    public void onBindViewHolder(DeviceViewHolder deviceViewHolder, int i) {
        BleDeviceModel bleDeviceModel = this.devices.get(i);
        BluetoothDevice device = bleDeviceModel.getDevice();
        int rssi = bleDeviceModel.getRssi();
        if (ActivityCompat.checkSelfPermission(deviceViewHolder.itemView.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String name = device.getName();
        if (name == null || name.isEmpty()) {
            deviceViewHolder.tvName.setText("N/A");
        } else {
            deviceViewHolder.tvName.setText(name);
        }
        deviceViewHolder.tvMacId.setText("Mac Id: " + device.getAddress());
        deviceViewHolder.tvRssi.setText(rssi + "\ndBm");
        calculateDistanceAsync(deviceViewHolder, rssi);
        setRandomBackgroundColor(deviceViewHolder);
    }

    public int getItemCount() {
        return this.devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDistance;
        TextView tvMacId;
        TextView tvName;
        TextView tvRssi;

        public DeviceViewHolder(View view) {
            super(view);
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.tvMacId = (TextView) view.findViewById(R.id.tvmacId);
            this.tvRssi = (TextView) view.findViewById(R.id.tvRssi);
            this.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setRandomBackgroundColor(DeviceViewHolder deviceViewHolder) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setSize(102, 100);
        gradientDrawable.setColor(new int[]{-3355444, -16776961, SupportMenu.CATEGORY_MASK, -7829368, -65281}[new Random().nextInt(5)]);
        deviceViewHolder.tvRssi.setBackground(gradientDrawable);
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateDistanceAsync(final DeviceViewHolder deviceViewHolder, int i) {
        new AsyncTask<Integer, Void, Double>() {
            @Override
            public Double doInBackground(Integer... numArr) {
                return Double.valueOf(BleDeviceAdapter.this.converter(numArr[0].intValue()));
            }

            @Override
            public void onPostExecute(Double d) {
                if (d.doubleValue() >= 0.0d) {
                    deviceViewHolder.tvDistance.setText("Aprx Distance: " + String.format("%.2f", new Object[]{d}) + " m");
                    return;
                }
                deviceViewHolder.tvDistance.setText("Aprx Distance: N/A");
            }
        }.execute(new Integer[]{Integer.valueOf(i)});
    }

    private double calculateDistance(int i) {
        return Math.pow(10.0d, (-75.0d - ((double) i)) / 200.0d);
    }

    public double calcDistbyRSSI(int i) {
        double pow;
        double abs = ((double) (Math.abs(i) - Math.abs(-69))) / 20.0d;
        if (Math.pow(10.0d, abs) * 3.2808d < 1.0d) {
            pow = Math.pow(10.0d, abs);
        } else if (Math.pow(10.0d, abs) * 3.2808d <= 1.0d || Math.pow(10.0d, abs) * 3.2808d >= 10.0d) {
            pow = Math.pow(10.0d, abs);
        } else {
            pow = Math.pow(10.0d, abs);
        }
        return pow * 3.2808d;
    }
}
