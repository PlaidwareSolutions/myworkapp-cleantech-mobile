package com.example.rfidapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rfidapp.R;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.entity.InventoryListEntity;
import com.example.rfidapp.fragment.InventoryItems;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;

import java.util.List;

public class InvListAdapter extends RecyclerView.Adapter<InvListAdapter.ListViewHolder> {

    private final List<InventoryListEntity> invList;
    private final MainActivity mContext;
    private final OnListClick onListClick;

    public interface OnListClick {
        void onInvListClick(String name, String id, String updateTime, String size);
    }

    public InvListAdapter(List<InventoryListEntity> invList, MainActivity context, OnListClick onListClick) {
        this.invList = invList;
        this.mContext = context;
        this.onListClick = onListClick;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inv_list, parent, false);
        return new ListViewHolder(view, onListClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        InventoryListEntity inventoryListEntity = invList.get(position);

        holder.listName.setText(inventoryListEntity.getInvName());
        holder.listId.setText(String.valueOf(inventoryListEntity.getInvId()));
        holder.listUpdate.setText(inventoryListEntity.getUpdateTime());
        holder.listSize.setText(inventoryListEntity.getItems());

        if (inventoryListEntity.getType().equalsIgnoreCase("Rfid")) {
            holder.imageView.setImageResource(R.drawable.c5);
        } else {
            holder.imageView.setImageResource(R.drawable.qr);
        }

        holder.llList.setOnClickListener(view -> {
            if (inventoryListEntity.getType().equalsIgnoreCase("Rfid")) {
                PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Rfid");
                PreferenceManager.setStringValue(Constants.INV_ITEM_RFID, inventoryListEntity.getInvName());
                PreferenceManager.setStringValue(Constants.INV_ID_RFID, String.valueOf(inventoryListEntity.getInvId()));
            } else {
                PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Barcode");
                PreferenceManager.setStringValue(Constants.INV_ITEM_BAR, inventoryListEntity.getInvName());
                PreferenceManager.setStringValue(Constants.INV_ID_BAR, String.valueOf(inventoryListEntity.getInvId()));
            }
            mContext.frm = 3;
            mContext.setFragment(new InventoryItems(), "Tag Count");
        });
    }

    @Override
    public int getItemCount() {
        return invList.size();
    }

    public void addAll(List<InventoryListEntity> list) {
        for (InventoryListEntity entity : list) {
            add(entity);
        }
    }

    public void add(InventoryListEntity inventoryListEntity) {
        invList.add(inventoryListEntity);
        notifyItemInserted(invList.size() - 1);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        if (!invList.isEmpty()) {
            invList.clear();
            notifyDataSetChanged();
        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView;
        final TextView listId;
        final TextView listName;
        final TextView listSize;
        final TextView listUpdate;
        final LinearLayout llList;
        final OnListClick onListClick;

        public ListViewHolder(@NonNull View itemView, OnListClick onListClick) {
            super(itemView);
            this.llList = itemView.findViewById(R.id.ll_list_tile);
            this.listName = itemView.findViewById(R.id.list_name);
            this.listId = itemView.findViewById(R.id.list_id);
            this.listUpdate = itemView.findViewById(R.id.list_update);
            this.listSize = itemView.findViewById(R.id.list_items);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.onListClick = onListClick;
            llList.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            onListClick.onInvListClick(
                    listName.getText().toString().trim(),
                    listId.getText().toString().trim(),
                    listUpdate.getText().toString().trim(),
                    listSize.getText().toString().trim()
            );
        }
    }
}
