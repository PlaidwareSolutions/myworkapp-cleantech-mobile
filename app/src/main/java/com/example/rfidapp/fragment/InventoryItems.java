package com.example.rfidapp.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.example.rfidapp.R;
import com.example.rfidapp.activity.InventoryItemsActivity;
import com.example.rfidapp.activity.PrepareShipment1Activity;
import com.example.rfidapp.activity.ShipmentDetailActivity;
import com.example.rfidapp.adapter.InvItemAdapter;
import com.example.rfidapp.dao.InvItemsDao;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.databinding.FragmentInventoryItemsBinding;
import com.example.rfidapp.entity.InventoryItemsEntity;
import com.example.rfidapp.entity.InventoryListEntity;
import com.example.rfidapp.model.Data;
import com.example.rfidapp.model.EpcModel;
import com.example.rfidapp.model.OrderShipmentData;
import com.example.rfidapp.model.network.CreateShipmentRequest;
import com.example.rfidapp.model.network.CreateShipmentResponse;
import com.example.rfidapp.model.network.Driver;
import com.example.rfidapp.model.network.InputBol;
import com.example.rfidapp.model.network.OrderDetail;
import com.example.rfidapp.model.network.Shipment;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.ScreenState;
import com.example.rfidapp.util.Util;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.util.core.ShipmentUtil;
import com.example.rfidapp.util.tool.StringUtils;
import com.example.rfidapp.util.tool.UIHelper;
import com.example.rfidapp.viewmodel.InvItemsViewModel;
import com.example.rfidapp.viewmodel.InvListViewModel;
import com.example.rfidapp.viewmodel.ShipmentViewModel;
import com.example.rfidapp.views.UhfInfo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.mlkit.common.sdkinternal.OptionalModuleUtils;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.KeyEventCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import kotlinx.coroutines.CoroutineScope;

@AndroidEntryPoint
public class InventoryItems extends KeyDownFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_RSSI = "tagRssi";

    public static final String TAG_RSSI_NUMBER = "tagRssiNumber";
    public static String[] storage_permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    public static String[] storage_permissions_33 = {"android.permission.READ_MEDIA_IMAGES", "android.permission.MANAGE_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public int SearchSelectItem = -1;
    MyAdapter adapter;
    MenuItem add;
    FragmentInventoryItemsBinding binding;
    public ClearDataAsyncTask clearDataAsyncTask;
    String create = "";
    MenuItem csv;
    private String cycle_Name_str;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message message) {
            UHFTAGInfo uHFTAGInfo = (UHFTAGInfo) message.obj;
            String dateTime = Util.getDateTime();
            Log.e("as_con", new Gson().toJson(uHFTAGInfo));
            Log.e("as_con", uHFTAGInfo.getEPC());
            InventoryItems.this.addDataToList(
                    uHFTAGInfo.getEPC(),
                    InventoryItems.this.mergeTidEpc(
                            uHFTAGInfo.getTid(),
                            uHFTAGInfo.getEPC(),
                            uHFTAGInfo.getUser()
                    ), dateTime,
                    uHFTAGInfo.getRssi(),
                    true
            );
            InventoryItems.this.setTotalTime();
        }
    };
    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    MenuItem inv;
    String invCycle = "";
    InvItemAdapter invItemAdapter;
    InvItemsViewModel invItemsViewModel;
    InvListViewModel invListViewModel;

    ShipmentViewModel shipmentViewModel;
    List<EpcModel> inv_epc;
    boolean isAleart = false;
    boolean isBar = false;
    boolean isReaderStart;
    boolean isSearch = false;
    boolean isStart = false;
    int limit = 200000;
    public boolean loopFlag = false;
    InventoryItemsActivity mContext;
    private String mParam1;
    private String mParam2;
    private HashMap<String, String> map;
    public String miliSec;
    MySearchAdapter mySearchAdapter;
    int page = 0;
    MenuItem power;

    int scannedItems = 0;
    private ArrayList<InventoryItemsEntity> searchDataArrayList;
    private HashMap<String, String> searchMap;
    public int selectItem = -1;
    String size = "";
    boolean stScn = false;
    private String status_str;
    private HashSet<String> stringHashSet;
    public ArrayList<HashMap<String, String>> tagList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> tagSearchList;
    private final List<String> tempDatas = new ArrayList();
    private long time;
    private int total;
    UhfInfo uhfInfo = new UhfInfo();
    String update = "";
    Util utils;

    OrderDetail orderDetail;
    Shipment shipment;
    String shipmentId = null;

    public interface ClickListner {
        void onClickListener(String data);
    }
    private ClickListner callback;

    public void setCallback(ClickListner callback) {
        this.callback = callback;
    }

    private final ActivityResultCallback<ActivityResult> resultCallback =
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    shipmentId = result.getData().getStringExtra("shipmentId");
                }
            };

    private final androidx.activity.result.ActivityResultLauncher<Intent> startActivityForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultCallback);

    static int binarySearch(List<String> list, String str) {
        int i = 0;
        for (int size2 = list.size() - 1; i <= size2; size2--) {
            if (compareString(list.get(i), str)) {
                return i;
            }
            if (i != size2 && compareString(list.get(size2), str)) {
                return size2;
            }
            i++;
        }
        return -1;
    }

    static boolean compareString(String str, String str2) {
        if (str.length() != str2.length() || str.hashCode() != str2.hashCode()) {
            return false;
        }
        char[] charArray = str.toCharArray();
        char[] charArray2 = str2.toCharArray();
        int length = charArray.length;
        for (int i = 0; i < length; i++) {
            if (charArray[i] != charArray2[i]) {
                return false;
            }
        }
        return true;
    }

    public static InventoryItems newInstance(String str, String shipmentString) {
        InventoryItems inventoryItems = new InventoryItems();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, shipmentString);
        inventoryItems.setArguments(bundle);
        return inventoryItems;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
            orderDetail = new Gson().fromJson(mParam1, OrderDetail.class);
            shipment = new Gson().fromJson(mParam2, Shipment.class);
            Log.e("TAG243", "onCreate: "+orderDetail );
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.inv_epc = new ArrayList();
        this.binding = FragmentInventoryItemsBinding.inflate(layoutInflater, viewGroup, false);
        this.invItemsViewModel = new ViewModelProvider(this).get(InvItemsViewModel.class);
        this.invListViewModel = new ViewModelProvider(this).get(InvListViewModel.class);
        this.shipmentViewModel = new ViewModelProvider(this).get(ShipmentViewModel.class);
        this.binding.rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        this.invItemAdapter = new InvItemAdapter(this.inv_epc, getContext());
        this.binding.rvItems.setAdapter(this.invItemAdapter);
        setHasOptionsMenu(true);
        this.intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> this.resultListner(result)
        );
        setupUI();
        bindListeners();
        return this.binding.getRoot();
    }

    private void setupUI() {
        if (orderDetail != null) {
            binding.orderDate.setText(orderDetail.getCreatedAt());
            binding.orderId.setText(orderDetail.getReferenceId());
            binding.customerName.setText(orderDetail.getCustomer().getName());
            binding.carrierName.setText(orderDetail.getCarrier().getName());
        } else if (shipment != null) {
            binding.orderDate.setText(shipment.getCreatedAt());
            binding.orderId.setText(shipment.getReferenceId());
            binding.customerName.setText(shipment.getCreatedBy().getName());
            binding.carrierName.setText(shipment.getCarrier().getName());
        } else {
            binding.lnrItem.setVisibility(View.GONE);
        }

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetail != null){
                    //Create Shipment Flow
                    CreateShipmentRequest createShipmentRequest = new CreateShipmentRequest();
                    ArrayList<InputBol> bills = new ArrayList<>();
                    List<String> tagsList = tagList.stream()
                            .map(map -> map.get(InventoryItems.TAG_EPC))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    bills.add(new InputBol(orderDetail.getId(),tagsList));

                    createShipmentRequest.setBols(bills);
                    createShipmentRequest.setCarrier(orderDetail.getCarrier().getId());
                    Date date = new Date();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    String formattedDate = formatter.format(date);
                    createShipmentRequest.setShipmentDate(formattedDate);
                    createShipmentRequest.setDriver(new Driver("", ""));
                    ShipmentUtil.INSTANCE.setCreateShipment(createShipmentRequest);
                    OrderShipmentData orderShipmentData = ShipmentUtil.INSTANCE.getOrderToShipmentById(orderDetail.getId());
                    if (orderShipmentData == null) {
                        orderShipmentData = new OrderShipmentData(
                                orderDetail.getId(),
                                orderDetail.getReferenceId(),
                                orderDetail.getTotalCount(),
                                tagsList.size(),
                                (ArrayList<String>) tagsList
                        );
                    } else {
                        //todo:update logic here
                        ArrayList<String> tags = orderShipmentData.getTags();
                        tags.addAll(tagsList);
                        tags.stream().distinct();
                        orderShipmentData.setTags(tags);
                    }
                    ShipmentUtil.INSTANCE.addOrUpdateOrderToShipment(orderShipmentData);
                    if (orderDetail != null) {
                        Intent intent = new Intent(requireActivity(), PrepareShipment1Activity.class);
                        startActivity(intent);
//                        mContext.finish();
                    }
//                startActivityForResult.launch(intent);
//                mContext.finish();
                /*if (shipmentId == null) {
                    //Create
                    shipmentViewModel.createShipments(createShipmentRequest);
                } else {
                    //Update
                    shipmentViewModel.updateShipments(shipmentId, createShipmentRequest);
                }*/
                }
                else if (shipment != null) {
                    List<String> tagsList = tagList.stream()
                            .map(map -> map.get(InventoryItems.TAG_EPC))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    Intent intent = new Intent(requireActivity(), ShipmentDetailActivity.class);
                    intent.putExtra("tags",new Gson().toJson(tagsList));
                    intent.putExtra("SHIPMENT",new Gson().toJson(shipment));
                    startActivity(intent);
                }
                else {

                }
            }
        });
    }

    private void bindListeners(){
        shipmentViewModel.getCreateShipmentListLiveData().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ScreenState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof ScreenState.Success) {
                binding.progressBar.setVisibility(View.GONE);
                CreateShipmentResponse response = ((ScreenState.Success<CreateShipmentResponse>) state).getResponse();
                Toast.makeText(requireActivity(),"Items saved successfully",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), PrepareShipment1Activity.class);
                intent.putExtra("shipmentData", new Gson().toJson(response));
                startActivityForResult.launch(intent);
            } else if (state instanceof ScreenState.Error) {
                String errorMessage = ((ScreenState.Error) state).getMessage();
                Toast.makeText(requireActivity(),errorMessage,Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void resultListner(ActivityResult activityResult) {
        if (activityResult.getResultCode() == InventoryItemsActivity.barcodeResultCode) {
            Intent data = activityResult.getData();
            mContext.playSound(1);
            addDataToList(data.getStringExtra(OptionalModuleUtils.BARCODE).trim(), "", Util.getDateTime(), "",true);
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        InventoryItemsActivity mainActivity = (InventoryItemsActivity) getActivity();
        this.mContext = mainActivity;
        mainActivity.currentFrag = this;
        mContext.initSound();
        init();
    }

    public void init() {
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equalsIgnoreCase("Rfid")) {
            this.mContext.setTitle("Scan Tag");
            if (PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).equals("null") || PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).equals("")) {
                this.binding.llInvCycle.setVisibility(View.GONE);
                mContext.showToast("233");
            } else {
                this.binding.tvCycle.setText(PreferenceManager.getStringValue(Constants.INV_ITEM_RFID));
            }
        } else {
            if (this.mContext.isC5Device.booleanValue()) {
                new InitBarcodeTask().execute(new String[0]);
            }
            this.mContext.setTitle("Scan QR");
            if (PreferenceManager.getStringValue(Constants.INV_ITEM_BAR).equals("null") || PreferenceManager.getStringValue(Constants.INV_ITEM_BAR).equals("")) {
                this.binding.llInvCycle.setVisibility(android.view.View.GONE);
            } else {
                this.binding.tvCycle.setText(PreferenceManager.getStringValue(Constants.INV_ITEM_BAR));
            }
        }
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && InventoryItemsActivity.mBtReader != null) {
            InventoryItemsActivity.mBtReader.setKeyEventCallback(new KeyEventCallback() {
                public void onKeyUp(int i) {
                }

                public void onKeyDown(int i) {
                    if (!PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                        return;
                    }
                    if (PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).equals("")) {
                        String unused = InventoryItems.this.insertValues();
                    } else {
                        InventoryItems.this.readTag();
                    }
                }
            });
        }
        this.tagSearchList = new ArrayList<>();
        this.adapter = new MyAdapter(this.mContext);
        this.mySearchAdapter = new MySearchAdapter(this.mContext);
        this.binding.LvTags.setAdapter(this.adapter);
        this.binding.LvSearchTags.setAdapter(this.mySearchAdapter);
        this.utils = new Util(getContext());
        this.binding.btStart.setOnClickListener(this);
        this.binding.btCan.setOnClickListener(this);
        this.binding.btSch.setOnClickListener(this);
        this.binding.btClear.setOnClickListener(this);
        this.binding.tvEdit.setOnClickListener(this);
        this.binding.etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                String trim = InventoryItems.this.binding.etSearch.getText().toString().trim();
                if (!trim.isEmpty()) {
                    InventoryItems.this.isSearch = true;
                    InventoryItems.this.loadEpc(trim);
                    InventoryItems.this.binding.LvTags.setVisibility(android.view.View.GONE);
                    InventoryItems.this.binding.LvSearchTags.setVisibility(android.view.View.VISIBLE);
                    return;
                }
                InventoryItems.this.isSearch = false;
                InventoryItems.this.binding.LvSearchTags.setVisibility(android.view.View.GONE);
                InventoryItems.this.binding.LvTags.setVisibility(android.view.View.VISIBLE);
            }
        });
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
            loadData(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
        } else {
            loadData(PreferenceManager.getStringValue(Constants.INV_ID_BAR));
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.bt_can) {
            this.isSearch = false;
            this.binding.etSearch.setText("");
            this.binding.llSch.setVisibility(View.GONE);
            this.binding.llTitle.setVisibility(View.VISIBLE);
            this.binding.LvSearchTags.setVisibility(View.GONE);
            this.binding.LvTags.setVisibility(View.VISIBLE);
            this.mContext.hideKeybaord(view);
        } else if (view.getId() == R.id.bt_clear) {
            if (this.mContext.isBTDevice.booleanValue()) {
                if (InventoryItemsActivity.mBtReader.isWorking()) {
                    this.mContext.highlightToast("Kindly Stop Reading First..", 2);
                } else {
                    clearDialog();
                }
            } else if (!this.mContext.isC5Device.booleanValue()) {
                clearDialog();
            } else if (InventoryItemsActivity.mReader.isWorking()) {
                this.mContext.highlightToast("Kindly Stop Reading First..", 2);
            } else {
                clearDialog();
            }
        } else if (view.getId() == R.id.bt_sch) {
            if (this.mContext.isBTDevice.booleanValue()) {
                if (InventoryItemsActivity.mBtReader.isWorking()) {
                    this.mContext.highlightToast("Kindly Stop Reading First..", 2);
                } else {
                    this.binding.llTitle.setVisibility(View.GONE);
                    this.binding.llSch.setVisibility(View.VISIBLE);
                }
            } else if (!this.mContext.isC5Device.booleanValue()) {
                this.binding.llTitle.setVisibility(View.GONE);
                this.binding.llSch.setVisibility(View.VISIBLE);
            } else if (InventoryItemsActivity.mReader.isWorking()) {
                this.mContext.highlightToast("Kindly Stop Reading First..", 2);
            } else {
                this.binding.llTitle.setVisibility(View.GONE);
                this.binding.llSch.setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.bt_start) {
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                if (PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).equals("")) {
                    insertValues();
                } else if (this.mContext.isC5Device.booleanValue()) {
                    readTag();
                } else if (!this.mContext.isBTDevice.booleanValue()) {
                    this.mContext.highlightToast("Kindly Use RFID Device", 2);
                } else if (this.mContext.isBtConnect) {
                    readTag();
                } else {
                    this.mContext.highlightToast("Please Connect Device First..", 2);
                }
            } else if (this.mContext.isC5Device.booleanValue()) {
                if (!this.isBar) {
                    this.isBar = true;
                    start();
                } else {
                    this.mContext.stop();
                }
            } else if (PreferenceManager.getStringValue(Constants.INV_ITEM_BAR).equals("")) {
                insertValues();
            } else {
                startBarcode();
            }
        } else if (view.getId() == R.id.tv_edit) {
            if (!PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                editBottomSheet(PreferenceManager.getStringValue(Constants.INV_ID_BAR));
            } else if (this.mContext.isBTDevice.booleanValue()) {
                if (InventoryItemsActivity.mBtReader.isWorking()) {
                    this.mContext.highlightToast("Kindly Stop Reading First..", 2);
                } else {
                    editBottomSheet(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
                }
            } else if (!this.mContext.isC5Device.booleanValue()) {
                // No operation as per the original code
            } else {
                if (InventoryItemsActivity.mReader.isWorking()) {
                    this.mContext.highlightToast("Kindly Stop Reading First..", 2);
                } else {
                    editBottomSheet(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
                }
            }
        }
    }

    public void startBarcode() {
        /*this.intentActivityResultLauncher.launch(new Intent(this.mContext, BarcodeActivity.class));*/
    }

    public void editBottomSheet(String str) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.mContext);
        bottomSheetDialog.setContentView(View.inflate(this.mContext, R.layout.inv_update, (ViewGroup) null));
        TextView textView = (TextView) bottomSheetDialog.findViewById(R.id.tv_inv_name);
        TextView textView2 = (TextView) bottomSheetDialog.findViewById(R.id.tv_right);
        TextView textView3 = (TextView) bottomSheetDialog.findViewById(R.id.tv_close);
        EditText editText = (EditText) bottomSheetDialog.findViewById(R.id.et_new_inv_name);
        TextView textView4 = (TextView) bottomSheetDialog.findViewById(R.id.tv_cancel);
        TextView textView5 = (TextView) bottomSheetDialog.findViewById(R.id.tv_update);
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
            textView.setText(PreferenceManager.getStringValue(Constants.INV_ITEM_RFID));
        } else {
            textView.setText(PreferenceManager.getStringValue(Constants.INV_ITEM_BAR));
        }
        editText.setText(textView.getText());
        textView5.setOnClickListener(view -> this.editBottomSheet(editText, str, bottomSheetDialog, view));
        textView2.setOnClickListener(view -> this.editBottomSheet2(editText, str, bottomSheetDialog, view));
        textView4.setOnClickListener(view -> bottomSheetDialog.dismiss());
        textView3.setOnClickListener(view -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    public void editBottomSheet(EditText editText, String str, BottomSheetDialog bottomSheetDialog, View view) {
        String trim = editText.getText().toString().trim();
        if (trim.length() <= 0) {
            this.mContext.highlightToast("Please Enter New Name...", 2);
        } else if (this.mContext.setInventoryName(str, trim) == 1) {
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                PreferenceManager.setStringValue(Constants.INV_ITEM_RFID, trim);
            } else {
                PreferenceManager.setStringValue(Constants.INV_ITEM_BAR, trim);
            }
            this.binding.tvCycle.setText(trim);
            bottomSheetDialog.dismiss();
        } else {
            this.mContext.highlightToast("Please Try Again.", 2);
        }
    }

    public void editBottomSheet2(EditText editText, String str, BottomSheetDialog bottomSheetDialog, View view) {
        String trim = editText.getText().toString().trim();
        if (trim.length() <= 0) {
            this.mContext.highlightToast("Please Enter New Name...", 2);
        } else if (this.mContext.setInventoryName(str, trim) == 1) {
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                PreferenceManager.setStringValue(Constants.INV_ITEM_RFID, trim);
            } else {
                PreferenceManager.setStringValue(Constants.INV_ITEM_BAR, trim);
            }
            this.binding.tvCycle.setText(trim);
            bottomSheetDialog.dismiss();
        } else {
            this.mContext.highlightToast("Please Try Again.", 2);
        }
    }

    public void btCancel(String str) {
        if (str.contains("grey")) {
            this.binding.btClear.setEnabled(false);
            return;
        }
        this.binding.btClear.setEnabled(true);
    }

    @SuppressLint("CheckResult")
    public void loadEpc(String str) {
        String str2;
        this.tagSearchList.clear();
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
            str2 = PreferenceManager.getStringValue(Constants.INV_ID_RFID);
        } else {
            str2 = PreferenceManager.getStringValue(Constants.INV_ID_BAR);
        }
        this.invItemsViewModel.getEpcData(str2, str, 0, 100).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<InventoryItemsEntity>>() {
            public void onSuccess(List<InventoryItemsEntity> list) {
                if (!list.isEmpty()) {
                    new setEpc().execute(list);
                    InventoryItems.this.binding.llNoEpc.setVisibility(View.GONE);
                    return;
                }
                InventoryItems.this.utils.hideProgressBar();
                InventoryItems.this.mySearchAdapter.notifyDataSetChanged();
                InventoryItems.this.binding.LvSearchTags.setVisibility(View.GONE);
                InventoryItems.this.binding.llNoEpc.setVisibility(View.VISIBLE);
            }

            public void onError(Throwable th) {
                InventoryItems.this.binding.llNoEpc.setVisibility(android.view.View.GONE);
                InventoryItems.this.mContext.highlightToast("error in search...", 2);
                Toast.makeText(InventoryItems.this.mContext, "error in search...", android.widget.Toast.LENGTH_SHORT).show();
                InventoryItems.this.mySearchAdapter.notifyDataSetChanged();
            }
        });
    }

    private String getMiliSec() {
        String str = "RS_INV_" + String.valueOf(Calendar.getInstance().getTimeInMillis());
        this.miliSec = str;
        return str;
    }

    public String insertValues() {
        Completable.fromAction(() -> this.mInsertValues())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable disposable) {}

                    @Override
                    public void onComplete() {
                        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                            PreferenceManager.setStringValue(Constants.INV_ITEM_RFID, InventoryItems.this.miliSec);
                            PreferenceManager.setStringValue(Constants.INV_ID_RFID, String.valueOf(InventoryItems.this.mContext.getInvId(InventoryItems.this.miliSec)));
                        } else {
                            PreferenceManager.setStringValue(Constants.INV_ITEM_BAR, InventoryItems.this.miliSec);
                            PreferenceManager.setStringValue(Constants.INV_ID_BAR, String.valueOf(InventoryItems.this.mContext.getInvId(InventoryItems.this.miliSec)));
                        }
                        InventoryItems.this.binding.tvCycle.setText(InventoryItems.this.miliSec);
                        InventoryItems.this.binding.llInvCycle.setVisibility(View.VISIBLE);
                        InventoryItems.this.readTag();
                    }

                    @Override
                    public void onError(Throwable th) {}
                });
        return null;
    }

    public void mInsertValues() throws Exception {
        this.invListViewModel.insert(setInventoryValue());
    }

    private InventoryListEntity setInventoryValue() {
        return new InventoryListEntity(getMiliSec(), Util.getDateTime(), Util.getDateTime(), "0", PreferenceManager.getStringValue(Constants.CUR_SC_TYPE));
    }

    public void onPause() {
        if (this.mContext.isScanning) {
            stopInventory();
        }
        this.mContext.close();
        super.onPause();
    }

    public void onResume() {
        this.mContext.checkBTConnect();
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Barcode") && this.mContext.isC5Device.booleanValue()) {
            new InitBarcodeTask().execute(new String[0]);
        }
        super.onResume();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        /*this.add = menu.findItem(R.id.menu_add).setVisible(false);
        this.csv = menu.findItem(R.id.menu_csv);
        this.power = menu.findItem(R.id.menu_power);
        this.inv = menu.findItem(R.id.menu_inv);
        this.csv.setVisible(true);
        this.power.setVisible(true);
        this.inv.setVisible(true);
        this.csv.setOnMenuItemClickListener(menuItem -> this.onPrepareOptionsMenu(menuItem));
        this.inv.setOnMenuItemClickListener(menuItem -> this.onPrepareOptionsMenuInv(menuItem));
        super.onPrepareOptionsMenu(menu);*/
    }

    public boolean onPrepareOptionsMenu(MenuItem menuItem) {
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
            loadExportData(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
            return false;
        }
        loadExportData(PreferenceManager.getStringValue(Constants.INV_ID_BAR));
        return false;
    }

    public boolean onPrepareOptionsMenuInv(MenuItem menuItem) {
        /*this.mContext.frm = 4;
        this.mContext.setFragment(new InventoryList(), "Inventory List");*/
        return false;
    }

    public void onMyKeyDown() {
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equalsIgnoreCase("Rfid")) {
            if (PreferenceManager.getStringValue(Constants.INV_ITEM_RFID) == null || PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).equals("")) {
                insertValues();
            } else if (this.mContext.isC5Device.booleanValue()) {
                readTag();
            } else if (!this.mContext.isBTDevice.booleanValue()) {
                this.mContext.highlightToast("Kindly Use RFID Device", 2);
            } else if (this.mContext.isBtConnect) {
                readTag();
            } else {
                this.mContext.highlightToast("Please Connect Device First..", 2);
            }
        } else if (PreferenceManager.getStringValue(Constants.INV_ITEM_BAR) == null || PreferenceManager.getStringValue(Constants.INV_ITEM_BAR).equals("")) {
            insertValues();
        } else if (!this.mContext.isC5Device.booleanValue()) {
            this.mContext.highlightToast("Kindly Use C5 Device...", 2);
        } else if (!this.isBar) {
            this.isBar = true;
            start();
        } else {
            this.mContext.stop();
        }
        super.onMyKeyDown();
    }

    public void readTag() {
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1")) {
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Barcode")) {
                startBarcode();
            } else if (!this.mContext.isBtConnect) {
                this.mContext.highlightToast("Kindly Connect Device First..", 2);
            } else if (InventoryItemsActivity.mBtReader.startInventoryTag()) {
//                binding.imgScan.setVisibility(View.GONE);
//                binding.LvTags.setVisibility(View.VISIBLE);
                this.binding.btStart.setText(this.mContext.getString(R.string.title_stop_Inventory));
                this.loopFlag = true;
                this.time = System.currentTimeMillis();
                new TagThread().start();
            } else {
                stopInventory();
            }
        } else if (InventoryItemsActivity.mReader == null) {
        } else {
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Barcode")) {
                if (!this.isBar) {
                    this.isBar = true;
                    start();
                    return;
                }
                this.mContext.stop();
            } else if (InventoryItemsActivity.mReader.startInventoryTag()) {
//                binding.imgScan.setVisibility(View.GONE);
                this.binding.btStart.setText(this.mContext.getString(R.string.title_stop_Inventory));
                this.loopFlag = true;
                this.time = System.currentTimeMillis();
                new TagThread().start();
            } else {
                stopInventory();
            }
        }
    }

    public InventoryItemsEntity setItemsDetail(String str) {
        InventoryItemsEntity inventoryItemsEntity = new InventoryItemsEntity();
        inventoryItemsEntity.setEpc(str);
        inventoryItemsEntity.setTimeStamp(Util.getDateTime());
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
            inventoryItemsEntity.setEpcInv(str + "rs" + PreferenceManager.getStringValue(Constants.INV_ID_RFID));
            inventoryItemsEntity.setInventory(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
        } else {
            inventoryItemsEntity.setEpcInv(str + "rs" + PreferenceManager.getStringValue(Constants.INV_ID_BAR));
            inventoryItemsEntity.setInventory(PreferenceManager.getStringValue(Constants.INV_ID_BAR));
        }
        return inventoryItemsEntity;
    }

    private void insertValues(final InventoryItemsEntity inventoryItemsEntity, String str) {
        Completable.fromAction(() -> this.insertValues(inventoryItemsEntity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onComplete() {
                        InventoryItems.this.scannedItems++;
                    }

                    @Override
                    public void onError(Throwable th) {
                        InventoryItems.this.updateValues(inventoryItemsEntity);
                    }
                });
    }


    public void insertValues(InventoryItemsEntity inventoryItemsEntity) throws Exception {
        this.invItemsViewModel.insert(inventoryItemsEntity);
    }

    public void loadData(String str) {
        this.utils.showProgressBar("Please wait while data is loading...");
        this.invItemsViewModel.getInvData(str, this.page, this.limit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<InventoryItemsEntity>>() {
            public void onSuccess(List<InventoryItemsEntity> list) {
                if (list.size() > 0) {
                    new setEpc().execute(new List[]{list});
                    InventoryItems.this.btCancel("appColor");
                    return;
                }
                InventoryItems.this.btCancel("grey");
                InventoryItems.this.utils.hideProgressBar();
            }

            public void onError(Throwable th) {
                InventoryItems.this.utils.hideProgressBar();
            }
        });
    }


    public void updateValues(InventoryItemsEntity inventoryItemsEntity) {
        Completable.fromAction(() -> this.updateValuesEntity(inventoryItemsEntity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable disposable) {}

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(Throwable th) {}
                });
    }

    public void updateValuesEntity(InventoryItemsEntity inventoryItemsEntity) throws Exception {
        this.invItemsViewModel.update(inventoryItemsEntity);
    }

    private void stopInventory() {
        if (this.loopFlag) {
            this.loopFlag = false;
            if (!PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") || InventoryItemsActivity.mBtReader == null || !this.mContext.isBtConnect) {
                if (InventoryItemsActivity.mReader != null) {
                    if (InventoryItemsActivity.mReader.stopInventory()) {
                        this.binding.btStart.setText(this.mContext.getString(R.string.btInventory));
                    } else {
                        UIHelper.ToastMessage((Context) this.mContext, (int) R.string.uhf_msg_inventory_stop_fail);
                    }
                }
            } else if (InventoryItemsActivity.mBtReader.stopInventory()) {
                this.binding.btStart.setText(this.mContext.getString(R.string.btInventory));
            } else {
                UIHelper.ToastMessage((Context) this.mContext, (int) R.string.uhf_msg_inventory_stop_fail);
            }
        }
        InventoryItemsActivity mainActivity = this.mContext;
        if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
            mainActivity.setInventoryItemCount(mainActivity.getItemCount());
            return;
        }
        mainActivity.setInventoryItemBarCount(mainActivity.getItemBarCount());
    }

    class TagThread extends Thread {
        TagThread() {
        }

        public void run() {
            UHFTAGInfo uHFTAGInfo = null;
            while (InventoryItems.this.loopFlag) {
                if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && InventoryItemsActivity.mBtReader != null && InventoryItems.this.mContext.isBtConnect) {
                    uHFTAGInfo = InventoryItemsActivity.mBtReader.readTagFromBuffer();
                } else if (InventoryItemsActivity.mReader != null) {
                    uHFTAGInfo = InventoryItemsActivity.mReader.readTagFromBuffer();
                }
                if (uHFTAGInfo != null) {
                    Message obtainMessage = InventoryItems.this.handler.obtainMessage();
                    obtainMessage.obj = uHFTAGInfo;
                    InventoryItems.this.handler.sendMessage(obtainMessage);
                    InventoryItems.this.mContext.playSound(1);
                }
            }
        }
    }

    public String mergeTidEpc(String str, String str2, String str3) {
        String str4 = "EPC:" + str2;
        if (!TextUtils.isEmpty(str) && !str.equals("0000000000000000") && !str.equals("000000000000000000000000")) {
            str4 = str4 + "\nTID:" + str;
        }
        return (str3 == null || str3.length() <= 0) ? str4 : str4 + "\nUSER:" + str3;
    }

    public void setTotalTime() {
        System.currentTimeMillis();
    }

    public void addDataToList(String str, String str2, String str3,String str4, boolean z) {
        if (StringUtils.isNotEmpty(str)) {
            int checkIsExist = checkIsExist(str);
            HashMap<String, String> hashMap = new HashMap<>();
            this.map = hashMap;
            hashMap.put(TAG_EPC, str);
            this.map.put(TAG_EPC_TID, str2);
            this.map.put(TAG_COUNT, String.valueOf(1));
            this.map.put(TAG_RSSI, str3);
            if (checkIsExist != -1) {
                try {
                    this.map.put(TAG_COUNT, String.valueOf(Integer.parseInt((String) this.tagList.get(checkIsExist).get(TAG_COUNT), 10) + 1));
                    this.map.put(TAG_EPC_TID, str2);
                    this.map.put(TAG_RSSI_NUMBER, str4);
                    this.tagList.set(checkIsExist, this.map);
                    updateValues(setItemsDetail(str));
                } catch (Exception unused) {
                    this.tempDatas.remove(str);
                }
            } else if (this.tempDatas.size() < 200) {
                this.isAleart = false;
                this.tagList.add(this.map);
                this.tempDatas.add(str);
                this.binding.tvCount.setText(String.valueOf(this.adapter.getCount()));
                if (z) {
                    insertValues(setItemsDetail(str), str);
                }
                if (this.binding.tvCount.getText().toString().equals("0")) {
                    btCancel("grey");
                } else {
                    btCancel("appColor");
                }
            } else if (!this.isAleart) {
                this.isAleart = true;
                stopInventory();
                Util.showfreeDialog(this.mContext);
            }
            if (z) {
                this.adapter.notifyDataSetChanged();
            }
            this.uhfInfo.setTempDatas(this.tempDatas);
            this.uhfInfo.setTagList(this.tagList);
            this.uhfInfo.setCount(this.total);
            this.uhfInfo.setTagNumber(this.adapter.getCount());
        }
    }

    public int checkIsExist(String str) {
        if (StringUtils.isEmpty(str)) {
            return -1;
        }
        return binarySearch(this.tempDatas, str);
    }

    public final class ViewHolder {
        public TextView dots;
        public LinearLayout llList;
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;

        public ViewHolder() {
        }
    }

    public class MyAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public long getItemId(int i) {
            return (long) i;
        }

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return InventoryItems.this.tagList.size();
        }

        public Object getItem(int i) {
            return InventoryItems.this.tagList.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view2 = this.mInflater.inflate(R.layout.list_tag_item, (ViewGroup) null);
                viewHolder.tvEPCTID = (TextView) view2.findViewById(R.id.TvTagUii);
                viewHolder.tvTagCount = (TextView) view2.findViewById(R.id.TvTagCount);
                viewHolder.tvTagRssi = (TextView) view2.findViewById(R.id.TvTagRssi);
                viewHolder.llList = (LinearLayout) view2.findViewById(R.id.ll_list);
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tvEPCTID.setText("EPC [Hex]: "+(CharSequence) InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_EPC));
            viewHolder.tvTagCount.setText("Read Count: "+(CharSequence) InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_COUNT));
//            viewHolder.tvTagRssi.setText("RSSI: "+(CharSequence) InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_RSSI));
            viewHolder.tvTagRssi.setText("RSSI: "+(CharSequence) InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_RSSI_NUMBER));
            viewHolder.llList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> stringStringHashMap = InventoryItems.this.tagList.get(i);
                    Data data = new Data(
                            stringStringHashMap.getOrDefault(InventoryItems.TAG_EPC, ""),
                            stringStringHashMap.getOrDefault(InventoryItems.TAG_COUNT, ""),
                            stringStringHashMap.getOrDefault(InventoryItems.TAG_RSSI_NUMBER, "")
                    );
                    callback.onClickListener(new Gson().toJson(data));
                }
            });
            if (i == InventoryItems.this.selectItem) {
                view2.setBackgroundColor(InventoryItems.this.mContext.getResources().getColor(R.color.app_color));
            } else {
                view2.setBackgroundColor(0);
            }
            return view2;
        }


        public void showPopup(int i, View view) {
            InventoryItems inventoryItems = InventoryItems.this;
            inventoryItems.showPopup(view, (String) inventoryItems.tagList.get(i).get(InventoryItems.TAG_EPC));
        }

        public void setSelectItem(int i) {
            if (InventoryItems.this.selectItem == i) {
                int unused = InventoryItems.this.selectItem = -1;
                InventoryItems.this.uhfInfo.setSelectItem("");
                InventoryItems.this.uhfInfo.setSelectIndex(InventoryItems.this.selectItem);
                return;
            }
            int unused2 = InventoryItems.this.selectItem = i;
            InventoryItems.this.uhfInfo.setSelectItem((String) InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_EPC));
            InventoryItems.this.uhfInfo.setSelectIndex(InventoryItems.this.selectItem);
        }
    }

    /*public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final LayoutInflater mInflater;
        private final Context mContext;
        private List<Map<String, String>> tagList = new ArrayList<>();
        private int selectItem = -1;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            this.mContext = context;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvEPCTID, tvTagCount, tvTagRssi;
            LinearLayout llList;

            public ViewHolder(View itemView) {
                super(itemView);
                tvEPCTID = itemView.findViewById(R.id.TvTagUii);
                tvTagCount = itemView.findViewById(R.id.TvTagCount);
                tvTagRssi = itemView.findViewById(R.id.TvTagRssi);
                llList = itemView.findViewById(R.id.ll_list);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.listtag_items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvEPCTID.setText(tagList.get(position).get(InventoryItems.TAG_EPC));
            holder.tvTagCount.setText(tagList.get(position).get(InventoryItems.TAG_COUNT));
            holder.tvTagRssi.setText(tagList.get(position).get(InventoryItems.TAG_RSSI));

            holder.llList.setOnClickListener(v -> showPopup(position, v));

            if (position == selectItem) {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.app_color));
            } else {
                holder.itemView.setBackgroundColor(0);
            }
        }

        @Override
        public int getItemCount() {
            return tagList.size();
        }

        public void updateList(List<Map<String, String>> newList) {
            this.tagList = newList != null ? newList : new ArrayList<>();
            notifyDataSetChanged();
        }

        private void showPopup(int position, View view) {
            InventoryItems inventoryItems = InventoryItems.this; // Replace with appropriate access to InventoryItems instance
            inventoryItems.showPopup(view, tagList.get(position).get(InventoryItems.TAG_EPC));
        }

        public void setSelectItem(int position) {
            if (selectItem == position) {
                selectItem = -1;
                InventoryItems.this.uhfInfo.setSelectItem("");
                InventoryItems.this.uhfInfo.setSelectIndex(selectItem);
            } else {
                selectItem = position;
                InventoryItems.this.uhfInfo.setSelectItem(tagList.get(position).get(InventoryItems.TAG_EPC));
                InventoryItems.this.uhfInfo.setSelectIndex(selectItem);
            }
            notifyDataSetChanged();
        }
    }*/

    public void showPopup(View view, String str) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.search_menu, popupMenu.getMenu());
        MenuItem findItem = popupMenu.getMenu().findItem(R.id.menu_search);
        MenuItem findItem2 = popupMenu.getMenu().findItem(R.id.menu_write);
        findItem.setVisible(true);
        findItem2.setVisible(true);
        findItem.setOnMenuItemClickListener(menuItem -> this.showPopup(str, menuItem));
        findItem2.setOnMenuItemClickListener(menuItem -> this.showPopup1(str, menuItem));
        popupMenu.show();
    }

    public boolean showPopup(String str, MenuItem menuItem) {
        SingleSearch singleSearch = new SingleSearch();
        Bundle bundle = new Bundle();
        bundle.putString("epc", str);
        singleSearch.setArguments(bundle);
        /*this.mContext.setFragment(singleSearch, "Single Search");*/
        return false;
    }


    public boolean showPopup1(String str, MenuItem menuItem) {
        WriteTag writeTag = new WriteTag();
        Bundle bundle = new Bundle();
        bundle.putString("epc", str);
        writeTag.setArguments(bundle);
        /*this.mContext.setFragment(writeTag, "Write Tag");*/
        return false;
    }

    public class setEpc extends AsyncTask<List<InventoryItemsEntity>, Boolean, Boolean> {
        public setEpc() {
        }

        @Override
        public Boolean doInBackground(List<InventoryItemsEntity>... listArr) {
            for (int i = 0; i < listArr[0].size(); i++) {
                if (InventoryItems.this.isSearch) {
                    InventoryItems.this.addDataToSearchList(listArr[0].get(i).getEpc(), listArr[0].get(i).getTimeStamp());
                } else {
                    InventoryItems.this.addDataToList(listArr[0].get(i).getEpc(), "", listArr[0].get(i).getTimeStamp(), "",false);
                }
            }
            return null;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public void onPostExecute(Boolean bool) {
            InventoryItems.this.adapter.notifyDataSetChanged();
            InventoryItems.this.mySearchAdapter.notifyDataSetChanged();
            InventoryItems.this.utils.hideProgressBar();
            super.onPostExecute(bool);
        }
    }

    public void addDataToSearchList(String str, String str2) {
        if (StringUtils.isNotEmpty(str)) {
            HashMap<String, String> hashMap = new HashMap<>();
            this.searchMap = hashMap;
            hashMap.put(TAG_EPC, str);
            this.searchMap.put(TAG_COUNT, String.valueOf(1));
            this.searchMap.put(TAG_RSSI, str2);
            this.tagSearchList.add(this.searchMap);
            this.uhfInfo.setTagSearchList(this.tagSearchList);
        }
    }

    public final class SearchViewHolder {
        public LinearLayout SllList;
        public TextView StvEPCTID;
        public TextView StvTagCount;
        public TextView StvTagRssi;
        public TextView dots;

        public SearchViewHolder() {
        }
    }

    public class MySearchAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public long getItemId(int i) {
            return (long) i;
        }

        public MySearchAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return InventoryItems.this.tagSearchList.size();
        }

        public Object getItem(int i) {
            return InventoryItems.this.tagSearchList.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            SearchViewHolder searchViewHolder;
            if (view == null) {
                searchViewHolder = new SearchViewHolder();
                view2 = this.mInflater.inflate(R.layout.listtag_items, (ViewGroup) null);
                searchViewHolder.StvEPCTID = (TextView) view2.findViewById(R.id.TvTagUii);
                searchViewHolder.StvTagCount = (TextView) view2.findViewById(R.id.TvTagCount);
                searchViewHolder.StvTagRssi = (TextView) view2.findViewById(R.id.TvTagRssi);
                searchViewHolder.SllList = (LinearLayout) view2.findViewById(R.id.ll_list);
                view2.setTag(searchViewHolder);
            } else {
                view2 = view;
                searchViewHolder = (SearchViewHolder) view.getTag();
            }
            searchViewHolder.StvEPCTID.setText((CharSequence) InventoryItems.this.tagSearchList.get(i).get(InventoryItems.TAG_EPC));
            searchViewHolder.StvTagCount.setText((CharSequence) InventoryItems.this.tagSearchList.get(i).get(InventoryItems.TAG_COUNT));
            searchViewHolder.StvTagRssi.setText((CharSequence) InventoryItems.this.tagSearchList.get(i).get(InventoryItems.TAG_RSSI));
            searchViewHolder.SllList.setOnClickListener(v -> this.showPopupp(i, v));
            if (i == InventoryItems.this.SearchSelectItem) {
                view2.setBackgroundColor(InventoryItems.this.mContext.getResources().getColor(R.color.app_color));
            } else {
                view2.setBackgroundColor(0);
            }
            return view2;
        }


        public void showPopupp(int i, View view) {
            InventoryItems inventoryItems = InventoryItems.this;
            inventoryItems.showPopup(view, (String) inventoryItems.tagSearchList.get(i).get(InventoryItems.TAG_EPC));
        }

        public void setSelectItem(int i) {
            if (InventoryItems.this.SearchSelectItem == i) {
                int unused = InventoryItems.this.SearchSelectItem = -1;
                InventoryItems.this.uhfInfo.setSelectItem("");
                InventoryItems.this.uhfInfo.setSelectIndex(InventoryItems.this.SearchSelectItem);
                return;
            }
            int unused2 = InventoryItems.this.SearchSelectItem = i;
            InventoryItems.this.uhfInfo.setSelectItem((String) InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_EPC));
            InventoryItems.this.uhfInfo.setSelectIndex(InventoryItems.this.SearchSelectItem);
        }
    }

    private void clearDialog() {
        new AlertDialog.Builder(getContext()).setIcon((int) R.drawable.delete_24).setTitle((CharSequence) "Clear Data").setMessage((CharSequence) "Do you want to clear all data?").setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                InventoryItems.this.clearDataAsyncTask = new ClearDataAsyncTask();
                InventoryItems.this.clearDataAsyncTask.execute();
                /*InventoryItems inventoryItems = new InventoryItems();
                Bundle bundle = new Bundle();
                if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                    bundle.putString("inv_type", "Rfid");
                } else {
                    bundle.putString("inv_type", "Barcode");
                }
                inventoryItems.setArguments(bundle);
                InventoryItems.this.mContext.setFragment(inventoryItems, "");*/
                    dialogInterface.dismiss();
            }
        }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) null).show();
    }

    public class ClearDataAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        public ProgressDialog progressDialog;

        private ClearDataAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(InventoryItems.this.mContext);
            progressDialog.setMessage("Clearing data...");
            progressDialog.setProgressStyle(1);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public Boolean doInBackground(Void... voidArr) {
            InvDB build = Room.databaseBuilder(InventoryItems.this.getContext().getApplicationContext(), InvDB.class, "Inventory_db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
            InvItemsDao invItemsDao = build.invItemsDao();
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                List<String> epcList = new ArrayList<>();
                for (HashMap<String, String> tag : InventoryItems.this.tagList) {
                    if (tag.containsKey(InventoryItems.TAG_EPC)) {
                        epcList.add(tag.get(InventoryItems.TAG_EPC));
                    }
                }
                if (invItemsDao.delData(epcList) > 0) {
                    build.close();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InventoryItems.this.binding.etSearch.setText("");
                            InventoryItems.this.tagList.clear();
                            InventoryItems.this.tagSearchList.clear();
                            InventoryItems.this.scannedItems = 0;
                            InventoryItems.this.binding.tvCount.setText("0");
                        }
                    });
                    new Handler(Looper.getMainLooper()).post(() -> this.doInBackgroundClearDataAsyncTask());

                }
            } else if (invItemsDao.delData(PreferenceManager.getStringValue(Constants.INV_ID_BAR)) > 0) {
                build.close();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InventoryItems.this.binding.etSearch.setText("");
                        InventoryItems.this.tagList.clear();
                        InventoryItems.this.tagSearchList.clear();
                        InventoryItems.this.scannedItems = 0;
                        InventoryItems.this.binding.tvCount.setText("0");
                    }
                });
                new Handler(Looper.getMainLooper()).post(() -> this.doInBackgroundClearDataAsyncTask());
            }
            return true;
        }

        public void doInBackgroundClearDataAsyncTask() {
            InventoryItems.this.btCancel("grey");
            InventoryItems.this.adapter.notifyDataSetChanged();
//            binding.imgScan.setVisibility(View.VISIBLE);
//            binding.LvTags.setVisibility(View.GONE);
        }

        @Override
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(numArr[0].intValue());
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (PreferenceManager.getStringValue(Constants.CUR_SC_TYPE).equals("Rfid")) {
                InventoryItems.this.mContext.setInventoryItemCount(InventoryItems.this.mContext.getItemCount());
            } else {
                InventoryItems.this.mContext.setInventoryItemBarCount(InventoryItems.this.mContext.getItemBarCount());
            }
            InventoryItems.this.mContext.setInventoryItemCount(InventoryItems.this.mContext.getItemCount());
            InventoryItems.this.clearDataAsyncTask = null;
        }
    }

    public class InitBarcodeTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        public InitBarcodeTask() {
        }

        @Override
        public Boolean doInBackground(String... strArr) {
            InventoryItems.this.open();
            return true;
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            this.mypDialog.cancel();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            ProgressDialog progressDialog = new ProgressDialog(InventoryItems.this.mContext);
            this.mypDialog = progressDialog;
            progressDialog.setProgressStyle(0);
            this.mypDialog.setMessage("init...");
            this.mypDialog.setCanceledOnTouchOutside(false);
            this.mypDialog.setCancelable(false);
            this.mypDialog.show();
        }
    }

    private void start() {
        this.binding.btStart.setText("STOP");
        this.mContext.barcodeDecoder.startScan();
    }

    public void open() {
        this.mContext.barcodeDecoder.open(this.mContext);
        this.mContext.barcodeDecoder.setDecodeCallback(barcodeEntity -> this.open(barcodeEntity));
    }


    public void open(BarcodeEntity barcodeEntity) {
        if (barcodeEntity.getResultCode() == 1) {
            this.mContext.playSound(1);
            addDataToList(barcodeEntity.getBarcodeData(), "", Util.getDateTime(),"", true);
        } else {
            this.mContext.playSound(2);
            this.mContext.highlightToast("Failed", 2);
        }
        this.isBar = false;
        this.binding.btStart.setText("Start");
        this.mContext.stop();
    }

    public static String[] permissions() {
        if (Build.VERSION.SDK_INT >= 30) {
            return storage_permissions_33;
        }
        return storage_permissions;
    }

    public void loadExportData(String str) {
        this.utils.showProgressBar("Please wait...");
        this.invItemsViewModel.getInvData(str, this.page, this.limit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<InventoryItemsEntity>>() {
            public void onSuccess(List<InventoryItemsEntity> list) {
                InventoryItems.this.utils.hideProgressBar();
                if (list.size() > 0) {
                    InventoryItems.this.mContext.highlightToast("Successfully Exported", 1);
                    InventoryItems.this.utils.showProgressBar("Excel file is creating...");
                    if ("Rfid".equals(PreferenceManager.getStringValue(Constants.CUR_SC_TYPE))) {
                        InventoryItems inventoryItems = InventoryItems.this;
                        new ExportAsyncTask(inventoryItems.mContext, list, PreferenceManager.getStringValue(Constants.INV_ITEM_RFID)).execute(new Void[0]);
                        return;
                    }
                    InventoryItems inventoryItems2 = InventoryItems.this;
                    new ExportAsyncTask(inventoryItems2.mContext, list, PreferenceManager.getStringValue(Constants.INV_ITEM_BAR)).execute(new Void[0]);
                    return;
                }
                InventoryItems.this.utils.hideProgressBar();
                InventoryItems.this.mContext.highlightToast("No data found to export", 2);
            }

            public void onError(Throwable th) {
                InventoryItems.this.utils.hideProgressBar();
                InventoryItems.this.mContext.highlightToast("Error occurred while fetching data", 2);
            }
        });
    }

    public class ExportAsyncTask extends AsyncTask<Void, Void, Void> {
        private String invCycle;
        private Context mContext;
        private List<InventoryItemsEntity> productDetails;

        public ExportAsyncTask(Context context, List<InventoryItemsEntity> list, String str) {
            this.mContext = context;
            this.productDetails = list;
            this.invCycle = str;
        }

        @Override
        public Void doInBackground(Void... voidArr) {
            InventoryItems.this.exportDB(this.productDetails);
            return null;
        }
    }

    public void exportDB(List<InventoryItemsEntity> r8) {
        /*
            r7 = this;
            java.lang.String r0 = "ItemDetails_"
            java.lang.String r1 = android.os.Environment.getExternalStorageState()
            java.lang.String r2 = "mounted"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x000f
            return
        L_0x000f:
            java.lang.String r1 = android.os.Environment.DIRECTORY_DOCUMENTS
            java.io.File r1 = android.os.Environment.getExternalStoragePublicDirectory(r1)
            java.io.File r2 = new java.io.File
            java.lang.String r3 = "RFIDScanner"
            r2.<init>(r1, r3)
            boolean r1 = r2.exists()
            if (r1 != 0) goto L_0x0025
            r2.mkdirs()
        L_0x0025:
            r1 = 0
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x00b0 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b0 }
            r4.<init>(r0)     // Catch:{ Exception -> 0x00b0 }
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00b0 }
            java.lang.StringBuilder r0 = r4.append(r5)     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r4 = ".csv"
            java.lang.StringBuilder r0 = r0.append(r4)     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00b0 }
            r3.<init>(r2, r0)     // Catch:{ Exception -> 0x00b0 }
            r3.createNewFile()     // Catch:{ Exception -> 0x00b0 }
            java.io.PrintWriter r0 = new java.io.PrintWriter     // Catch:{ Exception -> 0x00b0 }
            java.io.FileWriter r2 = new java.io.FileWriter     // Catch:{ Exception -> 0x00b0 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x00b0 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x00b0 }
            java.lang.String r1 = "EPC, Time Stamp"
            r0.println(r1)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r1 = 0
        L_0x0055:
            int r2 = r8.size()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            if (r1 >= r2) goto L_0x009f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r2.<init>()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.String r3 = "'"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.Object r3 = r8.get(r1)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            com.ruddersoft.rfidscanner.entity.InventoryItemsEntity r3 = (com.ruddersoft.rfidscanner.entity.InventoryItemsEntity) r3     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.String r3 = r3.getEpc()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.Object r3 = r8.get(r1)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            com.ruddersoft.rfidscanner.entity.InventoryItemsEntity r3 = (com.ruddersoft.rfidscanner.entity.InventoryItemsEntity) r3     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.String r3 = r3.getTimeStamp()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r4.<init>()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.StringBuilder r2 = r4.append(r2)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.String r4 = ","
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r0.println(r2)     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            int r1 = r1 + 1
            goto L_0x0055
        L_0x009f:
            com.ruddersoft.rfidscanner.utils.Util r8 = r7.utils     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r8.hideProgressBar()     // Catch:{ Exception -> 0x00ab, all -> 0x00a8 }
            r0.close()
            goto L_0x00c4
        L_0x00a8:
            r8 = move-exception
            r1 = r0
            goto L_0x00c5
        L_0x00ab:
            r8 = move-exception
            r1 = r0
            goto L_0x00b1
        L_0x00ae:
            r8 = move-exception
            goto L_0x00c5
        L_0x00b0:
            r8 = move-exception
        L_0x00b1:
            com.ruddersoft.rfidscanner.utils.Util r0 = r7.utils     // Catch:{ all -> 0x00ae }
            r0.hideProgressBar()     // Catch:{ all -> 0x00ae }
            java.lang.String r0 = "exception"
            java.lang.String r8 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x00ae }
            android.util.Log.e(r0, r8)     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x00c4
            r1.close()
        L_0x00c4:
            return
        L_0x00c5:
            if (r1 == 0) goto L_0x00ca
            r1.close()
        L_0x00ca:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ruddersoft.rfidscanner.views.fragments.InventoryItems.exportDB(java.util.List):void");
    }
}
