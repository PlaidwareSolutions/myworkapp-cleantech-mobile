package com.example.rfidapp.fragment;

import static com.example.rfidapp.ReaderClass.mReader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.rfidapp.R;
import com.example.rfidapp.activity.InventoryItemsActivity;
import com.example.rfidapp.activity.PrepareShipment1Activity;
import com.example.rfidapp.activity.ShipmentDetailActivity;
import com.example.rfidapp.dao.InvItemsDao;
import com.example.rfidapp.database.InvDB;
import com.example.rfidapp.databinding.FragmentInventoryItemsBinding;
import com.example.rfidapp.entity.InventoryItemsEntity;
import com.example.rfidapp.entity.InventoryListEntity;
import com.example.rfidapp.model.Data;
import com.example.rfidapp.model.EpcModel;
import com.example.rfidapp.model.OrderShipmentData;
import com.example.rfidapp.model.network.Asset;
import com.example.rfidapp.model.network.CreateShipmentRequest;
import com.example.rfidapp.model.network.Driver;
import com.example.rfidapp.model.network.InputBol;
import com.example.rfidapp.model.network.Shipment;
import com.example.rfidapp.model.network.orderdetail.OrderDetail;
import com.example.rfidapp.util.PreferenceManager;
import com.example.rfidapp.util.ScreenState;
import com.example.rfidapp.util.Util;
import com.example.rfidapp.util.constants.Constants;
import com.example.rfidapp.util.core.ShipmentUtil;
import com.example.rfidapp.util.tool.StringUtils;
import com.example.rfidapp.util.tool.UIHelper;
import com.example.rfidapp.viewmodel.AssetViewModel;
import com.example.rfidapp.viewmodel.InvItemsViewModel;
import com.example.rfidapp.viewmodel.InvListViewModel;
import com.example.rfidapp.views.UhfInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

@AndroidEntryPoint
public class InventoryItems extends KeyDownFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_RSSI = "tagRssi";
    public static final String TAG_RSSI_NUMBER = "tagRssiNumber";
    MyAdapter adapter;
    FragmentInventoryItemsBinding binding;
    public ClearDataAsyncTask clearDataAsyncTask = new ClearDataAsyncTask();
    InvItemsViewModel invItemsViewModel;
    InvListViewModel invListViewModel;
    List<EpcModel> inv_epc;
    boolean isAleart = false;
    int limit = 200000;
    public boolean loopFlag = false;
    InventoryItemsActivity mContext;
    public String miliSec;
    int page = 0;
    int scannedItems = 0;
    public int selectItem = -1;
    public ArrayList<HashMap<String, String>> tagList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> tagSearchList;
    private final List<String> tempData = new ArrayList<>();
    int total;
    UhfInfo uhfInfo = new UhfInfo();
    Util utils;
    OrderDetail orderDetail;
    Shipment shipment;
    int maxQuantity;
    private AssetViewModel assetViewModel;
    Boolean isCheckedStatus = false;

    public interface ClickListner {
        void onClickListener(String data);
    }

    private ClickListner callback;

    public void setCallback(ClickListner callback) {
        this.callback = callback;
    }

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

    public static InventoryItems newInstance(String orderDetailString, String shipmentString, int maxQuantity) {
        InventoryItems inventoryItems = new InventoryItems();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, orderDetailString);
        bundle.putString(ARG_PARAM2, shipmentString);
        bundle.putInt(ARG_PARAM3, maxQuantity);
        inventoryItems.setArguments(bundle);
        return inventoryItems;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
            int mParam3 = getArguments().getInt(ARG_PARAM3);
            orderDetail = new Gson().fromJson(mParam1, OrderDetail.class);
            shipment = new Gson().fromJson(mParam2, Shipment.class);
            maxQuantity = mParam3;
            Log.e("TAG243", "onCreate: " + orderDetail);
        }
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.inv_epc = new ArrayList<>();
        this.binding = FragmentInventoryItemsBinding.inflate(layoutInflater, viewGroup, false);
        this.invItemsViewModel = new ViewModelProvider(this).get(InvItemsViewModel.class);
        this.invListViewModel = new ViewModelProvider(this).get(InvListViewModel.class);
        this.assetViewModel = new ViewModelProvider(this).get(AssetViewModel.class);
        setupUI();
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

        if (orderDetail == null && shipment == null) {
            binding.save.setVisibility(View.GONE);
            if (!isCheckedStatus) {
                binding.checkStatus.setVisibility(View.VISIBLE);
            }else{
                binding.checkStatus.setVisibility(View.GONE);
            }
        }

        binding.save.setOnClickListener(view -> {
            if (InventoryItemsActivity.mReader != null && InventoryItemsActivity.mReader.isWorking()) {
                this.mContext.highlightToast("Kindly Stop Reading First..", 2);
            } else {
                if (orderDetail != null) {
                    //Create Shipment Flow
                    CreateShipmentRequest createShipmentRequest = new CreateShipmentRequest();
                    ArrayList<InputBol> bills = new ArrayList<>();
                    List<String> tagsList = tagList.stream()
                            .map(map -> map.get(InventoryItems.TAG_EPC))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    bills.add(new InputBol(orderDetail.getId(), tagsList));
                    if (tagsList.size() > maxQuantity) {
                        confirmationDialog();
                        return;
                    }

                    createShipmentRequest.setBols(bills);
                    createShipmentRequest.setCarrier(orderDetail.getCarrier().getId());
                    createShipmentRequest.setCarrierName(orderDetail.getCarrier().getName());
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
                                (ArrayList<String>) tagsList,
                                orderDetail
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
//                        if (alreadyShippedCount != 0) {
//                            intent.putExtra("alreadyShippedCount", alreadyShippedCount);
//                        }
                        startActivity(intent);
                        requireActivity().finish();
                    }
                } else if (shipment != null) {
                    List<String> tagsList = tagList.stream()
                            .map(map -> map.get(InventoryItems.TAG_EPC))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if (tagsList.size() > maxQuantity) {
                        confirmationDialog();
                        return;
                    }
                    Intent intent = new Intent(requireActivity(), ShipmentDetailActivity.class);
                    intent.putExtra("tags", new Gson().toJson(tagsList));
                    intent.putExtra("SHIPMENT", new Gson().toJson(shipment));
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                }
            }
        });

        binding.checkStatus.setOnClickListener(view -> {
            binding.txtError.setVisibility(View.GONE);
            List<String> tagsList = tagList.stream()
                    .map(map -> map.get(InventoryItems.TAG_EPC))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            setUpCheckStatus(tagsList);
        });
    }

    private void confirmationDialog() {
        if (mContext.isFinishing() || mContext.isDestroyed()) return; // Prevents leak

        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setIcon(R.drawable.ic_logo)
                .setMessage("You can ship maximum " + maxQuantity + " items.")
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(mContext, R.color.app_color_red));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(mContext, R.color.rs_green));
    }


    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        InventoryItemsActivity mainActivity = (InventoryItemsActivity) getActivity();
        this.mContext = mainActivity;
        mainActivity.currentFrag = this;
        mContext.initSound();
        init();
    }

    public void init() {
        this.tagSearchList = new ArrayList<>();
        this.adapter = new MyAdapter(this.mContext);
        this.binding.LvTags.setAdapter(this.adapter);
        this.utils = new Util(getContext());
        this.binding.btStart.setOnClickListener(this);
        this.binding.btClear.setOnClickListener(this);

        InventoryItems.this.binding.LvTags.setVisibility(android.view.View.VISIBLE);
        loadData(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
    }

    public void onClick(View view) {
        if (view.getId() == R.id.bt_clear) {
            if (this.mContext.isBTDevice) {
                if (InventoryItemsActivity.mBtReader.isWorking()) {
                    this.mContext.highlightToast("Kindly Stop Reading First..", 2);
                } else {
                    clearDialog();
                }
            } else if (!this.mContext.isC5Device) {
                clearDialog();
            } else if (mReader.isWorking()) {
                this.mContext.highlightToast("Kindly Stop Reading First..", 2);
            } else {
                clearDialog();
            }
        } else if (view.getId() == R.id.bt_start) {
            if (PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).isEmpty()) {
                insertValues();
            } else if (this.mContext.isC5Device) {
                readTag();
            } else if (!this.mContext.isBTDevice) {
                this.mContext.highlightToast("Kindly Use RFID Device", 2);
            } else if (this.mContext.isBtConnect) {
                readTag();
            } else {
                this.mContext.highlightToast("Please Connect Device First..", 2);
            }
        }
    }

    public void btCancel(String str) {
        if (str.contains("grey")) {
            this.binding.btClear.setEnabled(false);
            return;
        }
        this.binding.btClear.setEnabled(true);
    }

    private String getMiliSec() {
        String str = "RS_INV_" + Calendar.getInstance().getTimeInMillis();
        this.miliSec = str;
        return str;
    }

    public void insertValues() {
        Completable.fromAction(this::mInsertValues)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onComplete() {
                        PreferenceManager.setStringValue(Constants.INV_ITEM_RFID, InventoryItems.this.miliSec);
                        PreferenceManager.setStringValue(Constants.INV_ID_RFID, String.valueOf(InventoryItems.this.mContext.getInvId(InventoryItems.this.miliSec)));
                        InventoryItems.this.readTag();
                    }

                    @Override
                    public void onError(Throwable th) {
                        Toast.makeText(requireActivity(), th.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void mInsertValues() {
        this.invListViewModel.insert(new InventoryListEntity(getMiliSec(), Util.getDateTime(), Util.getDateTime(), "0", PreferenceManager.getStringValue(Constants.CUR_SC_TYPE)));
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
        super.onResume();
    }

    public void readTag() {
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1")) {
            if (!this.mContext.isBtConnect) {
                this.mContext.highlightToast("Kindly Connect Device First..", 2);
            } else if (InventoryItemsActivity.mBtReader.startInventoryTag()) {
                /*binding.imgScan.setVisibility(View.GONE);
                binding.LvTags.setVisibility(View.VISIBLE);*/
                this.binding.btStart.setText(this.mContext.getString(R.string.title_stop_Inventory));
                this.loopFlag = true;
                new TagThread().start();
            } else {
                stopInventory();
            }
        } else if (mReader == null) {
        } else {
            if (mReader.startInventoryTag()) {
                /*binding.imgScan.setVisibility(View.GONE);*/
                this.binding.btStart.setText(this.mContext.getString(R.string.title_stop_Inventory));
                this.loopFlag = true;
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
        inventoryItemsEntity.setEpcInv(str + "rs" + PreferenceManager.getStringValue(Constants.INV_ID_RFID));
        inventoryItemsEntity.setInventory(PreferenceManager.getStringValue(Constants.INV_ID_RFID));
        return inventoryItemsEntity;
    }

    private void insertValues(final InventoryItemsEntity inventoryItemsEntity) {
        Completable.fromAction(() -> invItemsViewModel.insert(inventoryItemsEntity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

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

    @SuppressLint("CheckResult")
    public void loadData(String str) {
        this.utils.showProgressBar("Please wait while data is loading...");
        this.invItemsViewModel.getInvData(str, this.page, this.limit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<InventoryItemsEntity>>() {
            public void onSuccess(List<InventoryItemsEntity> list) {
                if (!list.isEmpty()) {
                    new setEpc().execute(list);
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
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable th) {
                    }
                });
    }

    public void updateValuesEntity(InventoryItemsEntity inventoryItemsEntity) {
        this.invItemsViewModel.update(inventoryItemsEntity);
    }

    private void stopInventory() {
        if (this.loopFlag) {
            this.loopFlag = false;
            if (!PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") || InventoryItemsActivity.mBtReader == null || !this.mContext.isBtConnect) {
                if (mReader != null) {
                    if (mReader.stopInventory()) {
                        this.binding.btStart.setText(this.mContext.getString(R.string.btInventory));
                    } else {
                        UIHelper.ToastMessage(this.mContext, R.string.uhf_msg_inventory_stop_fail);
                    }
                }
            } else if (InventoryItemsActivity.mBtReader.stopInventory()) {
                this.binding.btStart.setText(this.mContext.getString(R.string.btInventory));
            } else {
                UIHelper.ToastMessage(this.mContext, R.string.uhf_msg_inventory_stop_fail);
            }
        }
        InventoryItemsActivity mainActivity = this.mContext;
        mainActivity.setInventoryItemCount(mainActivity.getItemCount());
    }

    public String mergeTidEpc(String str, String str2, String str3) {
        String str4 = "EPC:" + str2;
        if (!TextUtils.isEmpty(str) && !str.equals("0000000000000000") && !str.equals("000000000000000000000000")) {
            str4 = str4 + "\nTID:" + str;
        }
        return (str3 == null || str3.isEmpty()) ? str4 : str4 + "\nUSER:" + str3;
    }

    public void setTotalTime() {
        System.currentTimeMillis();
    }

    public void addDataToList(String str, String str2, String str3, String str4, boolean z) {
        if (StringUtils.isNotEmpty(str)) {
            int checkIsExist = checkIsExist(str);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(TAG_EPC, str);
            hashMap.put(TAG_EPC_TID, str2);
            hashMap.put(TAG_COUNT, String.valueOf(1));
            hashMap.put(TAG_RSSI, str3);
            if (checkIsExist != -1) {
                try {
                    hashMap.put(TAG_COUNT, String.valueOf(Integer.parseInt(this.tagList.get(checkIsExist).get(TAG_COUNT), 10) + 1));
                    hashMap.put(TAG_EPC_TID, str2);
                    hashMap.put(TAG_RSSI_NUMBER, str4);
                    this.tagList.set(checkIsExist, hashMap);
                    updateValues(setItemsDetail(str));
                } catch (Exception unused) {
                    this.tempData.remove(str);
                }
            } else if (this.tempData.size() < 200) {
                this.isAleart = false;
                this.tagList.add(hashMap);
                this.tempData.add(str);
                this.binding.tvCount.setText(String.valueOf(this.adapter.getCount()));
                if (z) {
                    insertValues(setItemsDetail(str));
                }
                if (this.binding.tvCount.getText().toString().equals("0")) {
                    binding.save.setVisibility(View.GONE);
                    isCheckedStatus = false;
                    binding.txtError.setVisibility(View.GONE);
                    if (shipment == null) {
                        binding.checkStatus.setVisibility(View.VISIBLE);
                        binding.save.setVisibility(View.GONE);
                    } else {
                        binding.checkStatus.setVisibility(View.GONE);
                        binding.save.setVisibility(View.VISIBLE);
                    }
                    btCancel("grey");
                } else {
                    if (!isCheckedStatus && shipment == null) {
                        binding.checkStatus.setVisibility(View.VISIBLE);
                        binding.save.setVisibility(View.GONE);
                    } else {
                        if (orderDetail == null && shipment == null) {
                            binding.save.setVisibility(View.GONE);
                            binding.checkStatus.setVisibility(View.VISIBLE);
                        } else {
                            binding.save.setVisibility(View.VISIBLE);
                            binding.checkStatus.setVisibility(View.GONE);
                        }
                    }
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
            this.uhfInfo.setTempDatas(this.tempData);
            this.uhfInfo.setTagList(this.tagList);
            this.uhfInfo.setCount(this.total);
            this.uhfInfo.setTagNumber(this.adapter.getCount());
        }
    }

    public int checkIsExist(String str) {
        if (StringUtils.isEmpty(str)) {
            return -1;
        }
        return binarySearch(this.tempData, str);
    }

    public static final class ViewHolder {
        public LinearLayout llList;
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;
        public TextView txtUnknown;
        public ImageView ivDelete;

        public ViewHolder() {
        }
    }

    public class MyAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public boolean isScanning = false;

        public long getItemId(int i) {
            return i;
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

        @SuppressLint({"SetTextI18n", "InflateParams"})
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view2 = this.mInflater.inflate(R.layout.list_tag_item, null);
                viewHolder.tvEPCTID = view2.findViewById(R.id.TvTagUii);
                viewHolder.tvTagCount = view2.findViewById(R.id.TvTagCount);
                viewHolder.tvTagRssi = view2.findViewById(R.id.TvTagRssi);
                viewHolder.llList = view2.findViewById(R.id.ll_list);
                viewHolder.ivDelete = view2.findViewById(R.id.btnDelete);
                viewHolder.txtUnknown = view2.findViewById(R.id.txtUnknown);
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tvEPCTID.setText("EPC [Hex]: " + InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_EPC));
            viewHolder.tvTagCount.setText("Read Count: " + InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_COUNT));
            viewHolder.tvTagRssi.setText("RSSI: " + InventoryItems.this.tagList.get(i).get(InventoryItems.TAG_RSSI_NUMBER));
            viewHolder.txtUnknown.setText(InventoryItems.this.tagList.get(i).get("status"));
            viewHolder.llList.setOnClickListener(view1 -> {
                HashMap<String, String> stringStringHashMap = InventoryItems.this.tagList.get(i);
                Data data = new Data(
                        stringStringHashMap.getOrDefault(InventoryItems.TAG_EPC, ""),
                        stringStringHashMap.getOrDefault(InventoryItems.TAG_COUNT, ""),
                        stringStringHashMap.getOrDefault(InventoryItems.TAG_RSSI_NUMBER, "")
                );
                callback.onClickListener(new Gson().toJson(data));
            });
            if (i == InventoryItems.this.selectItem) {
                view2.setBackgroundColor(InventoryItems.this.mContext.getResources().getColor(R.color.app_color));
            } else {
                view2.setBackgroundColor(0);
            }
            if (isScanning) {
                viewHolder.ivDelete.setVisibility(View.GONE);
            } else {
                viewHolder.ivDelete.setVisibility(View.VISIBLE);
            }
            viewHolder.ivDelete.setOnClickListener(view3 -> {
                InventoryItems.this.tagList.remove(i);
                binding.tvCount.setText(String.valueOf(getCount()));
                if (getCount() == 0) {
                    binding.save.setVisibility(View.GONE);
                    isCheckedStatus = false;
                    binding.txtError.setVisibility(View.GONE);
                    if (shipment == null) {
                        binding.checkStatus.setVisibility(View.VISIBLE);
                        binding.save.setVisibility(View.GONE);
                    } else {
                        binding.checkStatus.setVisibility(View.GONE);
                        binding.save.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (!isCheckedStatus && shipment == null) {
                        binding.checkStatus.setVisibility(View.VISIBLE);
                        binding.save.setVisibility(View.GONE);
                    } else {
                        if (orderDetail == null && shipment == null) {
                            binding.save.setVisibility(View.GONE);
                            binding.checkStatus.setVisibility(View.VISIBLE);
                        } else {
                            binding.save.setVisibility(View.VISIBLE);
                            binding.checkStatus.setVisibility(View.GONE);
                        }
                    }
                }
                notifyDataSetChanged();
            });
            return view2;
        }
    }

    private void clearDialog() {
        new AlertDialog.Builder(getContext()).setIcon(R.drawable.delete_24).setTitle("Clear Data").setMessage("Do you want to clear all data?").setPositiveButton("Yes", (dialogInterface, i) -> {

            InventoryItems.this.clearDataAsyncTask.execute();
            dialogInterface.dismiss();
        }).setNegativeButton("No", null).show();
    }

    class TagThread extends Thread {
        TagThread() {
        }

        public void run() {
            UHFTAGInfo uHFTAGInfo = null;
            while (InventoryItems.this.loopFlag) {
                if (PreferenceManager.getStringValue(Constants.GET_DEVICE).equals("1") && InventoryItemsActivity.mBtReader != null && InventoryItems.this.mContext.isBtConnect) {
                    uHFTAGInfo = InventoryItemsActivity.mBtReader.readTagFromBuffer();
                } else if (mReader != null) {
                    uHFTAGInfo = mReader.readTagFromBuffer();
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

    @SuppressLint("StaticFieldLeak")
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
                List<String> epcList = new ArrayList<>();
                for (HashMap<String, String> tag : InventoryItems.this.tagList) {
                    if (tag.containsKey(InventoryItems.TAG_EPC)) {
                        epcList.add(tag.get(InventoryItems.TAG_EPC));
                    }
                }
                if (invItemsDao.delData(epcList) > 0) {
                    build.close();
                    requireActivity().runOnUiThread(() -> {
                        InventoryItems.this.tagList.clear();
                        InventoryItems.this.tagSearchList.clear();
                        InventoryItems.this.scannedItems = 0;
                        isCheckedStatus = false;
                        binding.txtError.setVisibility(View.GONE);
                        InventoryItems.this.binding.tvCount.setText("0");
                        if (shipment == null) {
                            binding.checkStatus.setVisibility(View.VISIBLE);
                            binding.save.setVisibility(View.GONE);
                        } else {
                            binding.checkStatus.setVisibility(View.GONE);
                            binding.save.setVisibility(View.VISIBLE);
                        }
                    });
                    new Handler(Looper.getMainLooper()).post(this::doInBackgroundClearDataAsyncTask);
                }
            return true;
        }

        public void doInBackgroundClearDataAsyncTask() {
            InventoryItems.this.btCancel("grey");
            InventoryItems.this.adapter.notifyDataSetChanged();
            /*binding.imgScan.setVisibility(View.VISIBLE);
            binding.LvTags.setVisibility(View.GONE);*/
        }

        @Override
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgress(numArr[0]);
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            InventoryItems.this.mContext.setInventoryItemCount(InventoryItems.this.mContext.getItemCount());
            InventoryItems.this.mContext.setInventoryItemCount(InventoryItems.this.mContext.getItemCount());
            InventoryItems.this.clearDataAsyncTask = new ClearDataAsyncTask();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class setEpc extends AsyncTask<List<InventoryItemsEntity>, Boolean, Boolean> {
        public setEpc() {
        }

        @Override
        public Boolean doInBackground(List<InventoryItemsEntity>... listArr) {
            for (int i = 0; i < listArr[0].size(); i++) {
                InventoryItems.this.addDataToList(listArr[0].get(i).getEpc(), "", listArr[0].get(i).getTimeStamp(), "", false);
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
            InventoryItems.this.utils.hideProgressBar();
            super.onPostExecute(bool);
        }
    }

    private void setUpCheckStatus(List<String> tagsList) {
        JsonArray jsonArray = convertListToJsonArray(tagsList);
        assetViewModel.getAssetHistoryLiveData().observe(getViewLifecycleOwner(), it -> {
            if (it instanceof ScreenState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (it instanceof ScreenState.Success) {
                binding.progressBar.setVisibility(View.GONE);
                List<Asset> response = ((ScreenState.Success<List<Asset>>) it).getResponse();
                if (response != null && !response.isEmpty()) {
                    if (tagsList.size() == response.size()) {
                        isCheckedStatus = true;
                        for (Asset asset : response) {
                            String tagId = asset.getTag();
                            String status = asset.getLastState();

                            boolean found = false;

                            // Find the matching tagId in tagList and update its status
                            for (HashMap<String, String> tag : tagList) {
                                if (tagId.equals(tag.get(InventoryItems.TAG_EPC))) {
                                    tag.put("status", status);
                                    found = true;
                                    break;
                                }
                            }

                            // If tagId not found in tagList, add a new entry
                            if (!found) {
                                HashMap<String, String> newTag = new HashMap<>();
                                newTag.put(InventoryItems.TAG_EPC, tagId);
                                newTag.put("status", status);
                                tagList.add(newTag);
                            }

                            // If any status is not "Cleaned", set isChecked to false
                            if (!"CLEANED".equalsIgnoreCase(status)) {
                                isCheckedStatus = false;
                            }
                        }
                        InventoryItems.this.adapter.notifyDataSetChanged();
                        if (isCheckedStatus) {
                            binding.txtError.setVisibility(View.GONE);
                            if (orderDetail == null && shipment == null) {
                                binding.save.setVisibility(View.GONE);
                                binding.checkStatus.setVisibility(View.VISIBLE);
                            } else {
                                binding.save.setVisibility(View.VISIBLE);
                                binding.checkStatus.setVisibility(View.GONE);
                            }
                        } else {
                            binding.txtError.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else if (it instanceof ScreenState.Error) {
                binding.progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    Toast.makeText(requireActivity(), ((ScreenState.Error) it).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        assetViewModel.getAssetByTagID(jsonArray);
    }

    public JsonArray convertListToJsonArray(List<String> tagsList) {
        JsonArray jsonArray = new JsonArray();
        for (String tag : tagsList) {
            jsonArray.add(new JsonPrimitive(tag));
        }
        return jsonArray;
    }


}
