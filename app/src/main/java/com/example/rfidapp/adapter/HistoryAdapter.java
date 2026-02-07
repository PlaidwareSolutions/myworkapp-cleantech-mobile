package com.example.rfidapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rfidapp.R;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.entity.BleItemEntity;

import java.util.List;
import java.util.Random;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<BleItemEntity> bleItemsList;
    private Context context;

    public HistoryAdapter(List<BleItemEntity> list, Context context2) {
        this.bleItemsList = list;
        this.context = context2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ble_scan_list, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        BleItemEntity bleItemEntity = this.bleItemsList.get(i);
        int rssi = bleItemEntity.getRssi();
        String deviceName = bleItemEntity.getDeviceName();
        if (deviceName == null || deviceName.isEmpty()) {
            viewHolder.tvName.setText("N/A");
        } else {
            viewHolder.tvName.setText(deviceName);
        }
        viewHolder.tvRssi.setText(bleItemEntity.getRssi() + "\ndBm");
        viewHolder.tvmacId.setText("MAC ID: " + bleItemEntity.getMacAddress());
        setRandomBackgroundColor(viewHolder);
        calculateDistanceAsync(viewHolder, rssi);
    }

    public int getItemCount() {
        return this.bleItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDistance;
        TextView tvName;
        TextView tvRssi;
        TextView tvmacId;

        ViewHolder(View view) {
            super(view);
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            this.tvmacId = (TextView) view.findViewById(R.id.tvmacId);
            this.tvRssi = (TextView) view.findViewById(R.id.tvRssi);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setRandomBackgroundColor(ViewHolder viewHolder) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setSize(102, 100);
        gradientDrawable.setColor(new int[]{-3355444, -16776961, SupportMenu.CATEGORY_MASK, -7829368, -65281}[new Random().nextInt(5)]);
        viewHolder.tvRssi.setBackground(gradientDrawable);
    }

    private void deleteItemFromDatabase(Context context2, final BleItemEntity bleItemEntity) {
        final InvDB instance = InvDB.getInstance(context2);
        new Thread(new Runnable() {
            public void run() {
                instance.bleItemsDao().deleteBleItem(String.valueOf(bleItemEntity));
            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateDistanceAsync(final ViewHolder viewHolder, int i) {
        new AsyncTask<Integer, Void, Double>() {
            @Override
            public Double doInBackground(Integer... numArr) {
                return Double.valueOf(HistoryAdapter.this.calculateDistance(numArr[0].intValue()));
            }

            @Override
            public void onPostExecute(Double d) {
                if (d.doubleValue() >= 0.0d) {
                    viewHolder.tvDistance.setText("Aprx Distance: " + String.format("%.2f", new Object[]{d}) + " m");
                    return;
                }
                viewHolder.tvDistance.setText("Aprx Distance: N/A");
            }
        }.execute(new Integer[]{Integer.valueOf(i)});
    }

    public double calculateDistance(int i) {
        return Math.pow(10.0d, (-59.0d - ((double) i)) / 20.0d);
    }
}
