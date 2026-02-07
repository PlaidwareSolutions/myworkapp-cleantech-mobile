package com.example.rfidapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.HistoryAdapter;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.databinding.FragmentBleHistoryBinding;
import com.example.rfidapp.entity.BleItemEntity;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class BleHistory extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public HistoryAdapter adapter;
    public FragmentBleHistoryBinding binding;
    public List<BleItemEntity> bleItemsList;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;

    public static BleHistory newInstance(String str, String str2) {
        BleHistory bleHistory = new BleHistory();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        bleHistory.setArguments(bundle);
        return bleHistory;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentBleHistoryBinding.inflate(getLayoutInflater(), viewGroup, false);
        this.mContext = (MainActivity) getActivity();
        init();
        return this.binding.getRoot();
    }

    private void init() {
        this.binding.bleHistoryRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.mContext.setTitle("BLE History Items");
        this.bleItemsList = new ArrayList();
        this.adapter = new HistoryAdapter(this.bleItemsList, requireContext());
        this.binding.bleHistoryRecycler.setAdapter(this.adapter);
        this.binding.tvHistoryName.setText(getArguments().getString("itemName"));
        String stringValue = PreferenceManager.getStringValue(Constants.SAVE_BLE_LIST);
        Log.d("TAG", "History ID to filter: " + stringValue);
        loadItemsFromDatabase(stringValue);
    }

    private void loadItemsFromDatabase(final String str) {
        Log.d("TAG", "Loading items from database for history ID: " + str);
        InvDB.getInstance(getContext()).bleHistoryDao().getBleItemsByHistoryID(str).observe(getViewLifecycleOwner(), new Observer<List<BleItemEntity>>() {
            public void onChanged(List<BleItemEntity> list) {
                if (list != null) {
                    try {
                        if (!list.isEmpty()) {
                            Log.d("TAG", "Received " + list.size() + " items from database");
                            BleHistory.this.binding.totalBleItems.setText(String.valueOf(list.size()));
                            BleHistory.this.bleItemsList.clear();
                            BleHistory.this.bleItemsList.addAll(list);
                            BleHistory.this.adapter.notifyDataSetChanged();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "onChanged: " + e);
                        return;
                    }
                }
                Log.d("TAG", "No items found in database for history ID: " + str);
            }
        });
    }
}
