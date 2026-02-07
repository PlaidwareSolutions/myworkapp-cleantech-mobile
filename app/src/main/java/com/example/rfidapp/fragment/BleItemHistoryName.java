package com.example.rfidapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.BleItemAdapter;
import com.example.rfidapp.databinding.FragmentBleItemHistoryNameBinding;
import com.example.rfidapp.entity.BleEntity;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.viewmodel.BleHistoryViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BleItemHistoryName extends Fragment implements BleItemAdapter.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BleItemAdapter adapter;
    FragmentBleItemHistoryNameBinding binding;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;

    public static BleItemHistoryName newInstance(String str, String str2) {
        BleItemHistoryName bleItemHistoryName = new BleItemHistoryName();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        bleItemHistoryName.setArguments(bundle);
        return bleItemHistoryName;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentBleItemHistoryNameBinding inflate = FragmentBleItemHistoryNameBinding.inflate(getLayoutInflater(), viewGroup, false);
        this.binding = inflate;
        inflate.historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ArrayList arrayList = new ArrayList();
        this.adapter = new BleItemAdapter(arrayList, this::onItemClick);
        this.binding.historyRecyclerView.setAdapter(this.adapter);
        this.mContext.setTitle("BLE History List");
        BleHistoryViewModel viewModel = new ViewModelProvider(requireActivity()).get(BleHistoryViewModel.class);
        viewModel.getAllBleEntities().observe(getViewLifecycleOwner(), obj ->
                updateData(arrayList, (List) obj)
        );        return this.binding.getRoot();
    }

    public void updateData(List list, List list2) {
        list.clear();
        list.addAll(list2);
        Collections.reverse(list);
        if (list.isEmpty()) {
            this.binding.llNoData.setVisibility(View.VISIBLE);
            Log.d("TAG", "llNoData Visible: ");
        } else {
            this.binding.llNoData.setVisibility(View.GONE);
            Log.d("TAG", "llNoData Gone: ");
        }
        this.adapter.notifyDataSetChanged();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mContext = (MainActivity) context;
            return;
        }
        throw new ClassCastException("Parent activity must be MainActivity");
    }

    @Override
    public void onItemClick(BleEntity bleEntity) {
        MainActivity mainActivity = this.mContext;
        if (mainActivity != null) {
            mainActivity.frm = 6;
            PreferenceManager.setStringValue(Constants.SAVE_BLE_LIST, bleEntity.getId());
            String itemName = bleEntity.getItemName();
            Bundle bundle = new Bundle();
            bundle.putString("itemName", itemName);
            BleHistory bleHistory = new BleHistory();
            bleHistory.setArguments(bundle);
            this.mContext.setFragment(bleHistory, "History");
        }
    }
}
