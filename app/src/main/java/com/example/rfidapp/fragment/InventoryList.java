package com.example.rfidapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rfidapp.R;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.InvListAdapter;
import com.example.rfidapp.dao.InvListDao;
import com.example.rfidapp.databinding.FragmentInventoryListBinding;
import com.example.rfidapp.entity.InventoryListEntity;
import com.example.rfidapp.synthetic.InventoryList$$ExternalSyntheticLambda0;
import com.example.rfidapp.synthetic.InventoryList$$ExternalSyntheticLambda1;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.Util;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.viewmodel.InvListViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class InventoryList extends KeyDownFragment implements InvListAdapter.OnListClick {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentInventoryListBinding binding;
    InvListAdapter invListAdapter;
    InvListViewModel invListViewModel;
    List<InventoryListEntity> inv_list;
    int limit = 20;
    InvListDao listDao;
    MainActivity mContext;
    private String mParam1;
    private String mParam2;
    public String miliSec;
    int page = 0;
    String type = "Rfid";

    public void onInvListClick(String str, String str2, String str3, String str4) {
    }

    public static InventoryList newInstance(String str, String str2) {
        InventoryList inventoryList = new InventoryList();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        inventoryList.setArguments(bundle);
        return inventoryList;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentInventoryListBinding.inflate(layoutInflater, viewGroup, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        this.mContext = mainActivity;
        mainActivity.currentFrag = this;
        this.invListViewModel = (InvListViewModel) new ViewModelProvider(this).get(InvListViewModel.class);
        this.inv_list = new ArrayList();
        setHasOptionsMenu(true);
        initList();
        return this.binding.getRoot();
    }

    public void onResume() {
        this.mContext.checkBTConnect();
        super.onResume();
    }

    public void onMyKeyDown() {
        super.onMyKeyDown();
    }

    public void initList() {
        this.mContext.setTitle("Inventory Cycles");
        this.binding.rvProd.setLayoutManager(new LinearLayoutManager(getContext()));
        this.invListAdapter = new InvListAdapter(this.inv_list, this.mContext, new InventoryList$$ExternalSyntheticLambda1(this));
        this.binding.rvProd.setAdapter(this.invListAdapter);
        loadProducts("", this.page, this.limit);
        this.binding.fbAddInv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InventoryList.this.invBottomSheet();
            }
        });
        this.binding.etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                String trim = InventoryList.this.binding.etSearch.getText().toString().trim();
                InventoryList inventoryList = InventoryList.this;
                inventoryList.loadProducts(trim, inventoryList.page, InventoryList.this.limit);
                if (trim.length() > 0) {
                    InventoryList.this.binding.btSearch.setVisibility(View.GONE);
                    InventoryList.this.binding.btCan.setVisibility(View.VISIBLE);
                    return;
                }
                InventoryList.this.binding.btCan.setVisibility(android.view.View.GONE);
                InventoryList.this.binding.btSearch.setVisibility(android.view.View.VISIBLE);
            }
        });
        this.binding.btCan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InventoryList.this.binding.etSearch.setText("");
                InventoryList.this.mContext.hideKeybaord(view);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void loadProducts(String str, final int i, int i2) {
        this.binding.progressBar.setVisibility(android.view.View.VISIBLE);
        this.invListViewModel.getAllProducts(str, i, i2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.from(Looper.myLooper())).subscribeWith(new DisposableSingleObserver<List<InventoryListEntity>>() {
            public void onSuccess(List<InventoryListEntity> list) {
                Collections.reverse(list);
                if (list.size() > 0) {
                    if (i == 0) {
                        InventoryList.this.invListAdapter.clear();
                    }
                    InventoryList.this.invListAdapter.addAll(list);
                    InventoryList.this.binding.nestedSV.setVisibility(android.view.View.VISIBLE);
                    InventoryList.this.binding.view.setVisibility(android.view.View.VISIBLE);
                    InventoryList.this.binding.llNoProd.setVisibility(android.view.View.GONE);
                    InventoryList.this.binding.llMain.setVisibility(android.view.View.VISIBLE);
                } else if (i == 0) {
                    InventoryList.this.binding.nestedSV.setVisibility(android.view.View.VISIBLE);
                    InventoryList.this.binding.view.setVisibility(android.view.View.GONE);
                    InventoryList.this.binding.llMain.setVisibility(android.view.View.GONE);
                    InventoryList.this.binding.llNoProd.setVisibility(android.view.View.VISIBLE);
                }
            }

            public void onError(Throwable th) {
                Log.e("as_con", " found e : " + th);
                InventoryList.this.binding.nestedSV.setVisibility(android.view.View.VISIBLE);
                InventoryList.this.binding.view.setVisibility(android.view.View.GONE);
                InventoryList.this.binding.llMain.setVisibility(android.view.View.GONE);
                InventoryList.this.binding.llNoProd.setVisibility(android.view.View.VISIBLE);
                InventoryList.this.binding.llMain.setVisibility(android.view.View.GONE);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void getInvCount(final String str) {
        this.invListViewModel.getCount(str).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.from(Looper.myLooper())).subscribeWith(new DisposableSingleObserver<List<InventoryListEntity>>() {
            public void onSuccess(List<InventoryListEntity> list) {
                Collections.reverse(list);
                Log.e("as_con", str + "count is: " + list.size());
                if (list.size() < 5) {
                    InventoryList.this.insertValues();
                } else {
                    Util.showfreeDialog(InventoryList.this.mContext);
                }
            }

            public void onError(Throwable th) {
                Toast.makeText(InventoryList.this.mContext, "Someting went wrong, please try later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: private */
    public void invBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(View.inflate(requireContext(), R.layout.create_inv, (ViewGroup) null));
        this.type = "RFID";
        ((RadioGroup) bottomSheetDialog.findViewById(R.id.rd_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_rfid) {
                    InventoryList.this.type = "RFID";
                } else {
                    InventoryList.this.type = "Barcode";
                }
            }
        });
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_inv_name)).setText(getMiliSec());
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_create)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InventoryList inventoryList = InventoryList.this;
                inventoryList.getInvCount(inventoryList.type);
                bottomSheetDialog.dismiss();
            }
        });
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_right)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_exit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_close)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    /* access modifiers changed from: private */
    public void insertValues() {
        Completable.fromAction(new InventoryList$$ExternalSyntheticLambda0(this)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((CompletableObserver) new CompletableObserver() {
            public void onError(Throwable th) {
            }

            public void onSubscribe(Disposable disposable) {
            }

            public void onComplete() {
                if (InventoryList.this.type.equalsIgnoreCase("Rfid")) {
                    PreferenceManager.setStringValue(Constants.INV_ITEM_RFID, InventoryList.this.miliSec);
                    PreferenceManager.setStringValue(Constants.INV_ID_RFID, String.valueOf(InventoryList.this.mContext.getInvId(InventoryList.this.miliSec)));
                } else {
                    PreferenceManager.setStringValue(Constants.INV_ITEM_BAR, InventoryList.this.miliSec);
                    PreferenceManager.setStringValue(Constants.INV_ID_BAR, String.valueOf(InventoryList.this.mContext.getInvId(InventoryList.this.miliSec)));
                }
                InventoryList inventoryList = InventoryList.this;
                inventoryList.loadProducts("", inventoryList.page, InventoryList.this.limit);
            }
        });
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$insertValues$0$com-ruddersoft-rfidscanner-views-fragments-InventoryList  reason: not valid java name */
    public /* synthetic */ void m555lambda$insertValues$0$comruddersoftrfidscannerviewsfragmentsInventoryList() {
        this.invListViewModel.insert(setInventoryValue());
    }

    private InventoryListEntity setInventoryValue() {
        return new InventoryListEntity(this.miliSec, Util.getDateTime(), Util.getDateTime(), "0", this.type);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add).setVisible(true);
        menu.findItem(R.id.menu_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                InventoryList.this.invBottomSheet();
                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    public String getMiliSec() {
        String str = "RS_INV_" + String.valueOf(Calendar.getInstance().getTimeInMillis());
        this.miliSec = str;
        return str;
    }
}
