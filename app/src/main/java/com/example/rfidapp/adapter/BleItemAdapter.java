package com.example.rfidapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rfidapp.R;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.entity.BleEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BleItemAdapter extends RecyclerView.Adapter<BleItemAdapter.ViewHolder> {
    private List<BleEntity> bleEntities;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(BleEntity bleEntity);
    }

    public BleItemAdapter(List<BleEntity> list, OnItemClickListener onItemClickListener) {
        this.bleEntities = list;
        this.itemClickListener = onItemClickListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ble_item_list, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final BleEntity bleEntity = this.bleEntities.get(i);
        viewHolder.tvItemName.setText(bleEntity.getItemName());
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        viewHolder.tvDateTime.setText(bleEntity.getTime());
        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BleItemAdapter.this.deleteItemFromDatabase(view.getContext(), bleEntity);
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BleItemAdapter.this.openBleHistoryFragment(bleEntity);
            }
        });
    }

    public void deleteItemFromDatabase(final Context context, final BleEntity bleEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((CharSequence) "Delete Item");
        builder.setMessage((CharSequence) "Are you sure you want to delete this item?");
        builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                final InvDB instance = InvDB.getInstance(context);
                new Thread(new Runnable() {
                    public void run() {
                        instance.bleHistoryDao().deleteBleItemsByHistoryID(bleEntity.getId());
                        instance.bleHistoryDao().deleteBleItem(bleEntity);
                    }
                }).start();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public int getItemCount() {
        return this.bleEntities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime;
        TextView tvDelete;
        TextView tvItemName;

        public ViewHolder(View view) {
            super(view);
            this.tvItemName = (TextView) view.findViewById(R.id.item_name);
            this.tvDelete = (TextView) view.findViewById(R.id.tvDelete);
            this.tvDateTime = (TextView) view.findViewById(R.id.tv_date_time);
        }
    }

    public void openBleHistoryFragment(BleEntity bleEntity) {
        OnItemClickListener onItemClickListener = this.itemClickListener;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(bleEntity);
        }
    }
}
