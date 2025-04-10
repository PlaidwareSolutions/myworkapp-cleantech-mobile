package com.example.rfidapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room.databaseBuilder
import com.example.rfidapp.R
import com.example.rfidapp.ReaderClass.mBtReader
import com.example.rfidapp.ReaderClass.mReader
import com.example.rfidapp.activity.InventoryItemsActivity
import com.example.rfidapp.activity.PrepareShipment1Activity
import com.example.rfidapp.activity.ShipmentDetailActivity
import com.example.rfidapp.database.InvDB
import com.example.rfidapp.databinding.FragmentInventoryItemsBinding
import com.example.rfidapp.entity.InventoryItemsEntity
import com.example.rfidapp.entity.InventoryListEntity
import com.example.rfidapp.model.Data
import com.example.rfidapp.model.EpcModel
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.Asset
import com.example.rfidapp.model.network.CreateShipmentRequest
import com.example.rfidapp.model.network.Driver
import com.example.rfidapp.model.network.InputBol
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.model.network.orderdetail.OrderDetail
import com.example.rfidapp.util.PreferenceManager
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.Util
import com.example.rfidapp.util.constants.Constants
import com.example.rfidapp.util.core.ShipmentUtil.addOrUpdateOrderToShipment
import com.example.rfidapp.util.core.ShipmentUtil.getOrderToShipmentById
import com.example.rfidapp.util.core.ShipmentUtil.setCreateShipment
import com.example.rfidapp.util.tool.StringUtils
import com.example.rfidapp.util.tool.UIHelper
import com.example.rfidapp.viewmodel.AssetViewModel
import com.example.rfidapp.viewmodel.InvItemsViewModel
import com.example.rfidapp.viewmodel.InvListViewModel
import com.example.rfidapp.views.UhfInfo
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import com.rscja.deviceapi.entity.UHFTAGInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Objects
import java.util.stream.Collectors

@AndroidEntryPoint
class InventoryItems : KeyDownFragment(), View.OnClickListener {

    lateinit var binding: FragmentInventoryItemsBinding
    private var invItemsViewModel: InvItemsViewModel? = null
    private var invListViewModel: InvListViewModel? = null
    private var assetViewModel: AssetViewModel? = null
    lateinit var mContext: InventoryItemsActivity

    var adapter: MyAdapter? = null
    private var invEpc: List<EpcModel>? = null
    private var isAleart: Boolean = false
    private var limit: Int = 200000
    var loopFlag: Boolean = false
    var miliSecString: String? = null
    private var page: Int = 0
    var scannedItems: Int = 0
    var selectItem: Int = -1
    private var total: Int = 0
    private var uhfInfo: UhfInfo = UhfInfo()
    var utils: Util? = null
    var orderDetail: OrderDetail? = null
    var shipment: Shipment? = null
    private var maxQuantity: Int = 0
    var isCheckedStatus: Boolean = false
    var isInbound: Boolean = false
    var tagList: ArrayList<HashMap<String, String?>> = ArrayList()
    private val tempData: MutableList<String> = ArrayList()

    interface ClickListner {
        fun onClickListener(data: String?, status: String?)
    }

    private var callback: ClickListner? = null

    fun setCallback(callback: ClickListner) {
        this.callback = callback
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            val uHFTAGInfo = message.obj as UHFTAGInfo
            val dateTime = Util.getDateTime()
            this@InventoryItems.addDataToList(
                uHFTAGInfo.epc,
                this@InventoryItems.mergeTidEpc(
                    uHFTAGInfo.tid,
                    uHFTAGInfo.epc,
                    uHFTAGInfo.user
                ), dateTime,
                uHFTAGInfo.rssi,
                true
            )
            this@InventoryItems.setTotalTime()
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        arguments?.let {
            val mParam1 = it.getString(ARG_PARAM1) ?: ""
            val mParam2 = it.getString(ARG_PARAM2) ?: ""
            maxQuantity = it.getInt(ARG_PARAM3)
            orderDetail = Gson().fromJson(mParam1, OrderDetail::class.java)
            shipment = Gson().fromJson(mParam2, Shipment::class.java)
            isInbound = (orderDetail?.isInbound() == true) || (shipment?.isInbound() == true)
        }
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        this.invEpc = ArrayList()
        this.binding = FragmentInventoryItemsBinding.inflate(layoutInflater, viewGroup, false)
        this.invItemsViewModel = ViewModelProvider(this)[InvItemsViewModel::class.java]
        this.invListViewModel = ViewModelProvider(this)[InvListViewModel::class.java]
        this.assetViewModel = ViewModelProvider(this)[AssetViewModel::class.java]
        setupObserver()
        setupUI()
        return binding.root
    }

    private fun setupObserver() {
        assetViewModel?.assetHistoryLiveData?.observe(
            viewLifecycleOwner
        ) { it: ScreenState<List<Asset>?>? ->
            if (it is ScreenState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else if (it is ScreenState.Success<*>) {
                binding.progressBar.visibility = View.GONE
                val response = (it as ScreenState.Success<List<Asset>?>).response
                if (!response.isNullOrEmpty()) {
                    isCheckedStatus = true
                    for ((_, _, _, tagId, _, status) in response) {
                        var found = false
                        // Find the matching tagId in tagList and update its status
                        for (tag in tagList) {
                            if (tagId == tag[TAG_EPC]) {
                                Log.e("TAG111", "setupObserver: $status")
                                tag["status"] = status?.ifEmpty { "UNKNOWN" } ?: "UNKNOWN"
                                if (tag["status"] == "null" || tag["status"] == null) {
                                    tag["status"] = "UNKNOWN"
                                }
                                found = true
                                break
                            }
                        }

                        // If tagId not found in tagList, add a new entry
                        if (!found) {
                            val newTag = HashMap<String, String?>()
                            newTag[TAG_EPC] = tagId
                            newTag["status"] = status?.ifEmpty { "UNKNOWN" } ?: "UNKNOWN"
                            if (newTag["status"] == "null" || newTag["status"] == null) {
                                newTag["status"] = "UNKNOWN"
                            }
                            tagList.add(newTag)
                            updateValidation()
                        }

                        // If any status is not "Cleaned", set isChecked to false
//                        if (!"CLEANED".equals(status, ignoreCase = true)) {
//                            if (!isInbound) {
//                                isCheckedStatus = false
//                            }
//                        }
                        if (isInbound) {
                            if (!"ASSIGNED".equals(status, ignoreCase = true)
                                && !"PROCESSING".equals(status, ignoreCase = true)
                            ) {
                                isCheckedStatus = false
                            }
                        } else {
                            if (!"CLEANED".equals(status, ignoreCase = true)) {
                                isCheckedStatus = false
                            }
                        }
                    }
                    tagList = ArrayList(
                        tagList.sortedWith(
                            compareBy { tag ->
                                if (isInbound) {
                                    if (tag["status"].equals("ASSIGNED", ignoreCase = true) || tag["status"].equals("PROCESSING", ignoreCase = true)) 1 else 0
                                } else {
                                    if (tag["status"].equals("CLEANED", ignoreCase = true)) 1 else 0
                                }
                            }
                        )
                    )
                    adapter?.notifyDataSetChanged()
                    if (isCheckedStatus) {
                        binding.txtError.visibility = View.GONE
                        if (orderDetail == null && shipment == null) {
                            binding.save.visibility = View.GONE
                            binding.checkStatus.visibility = View.VISIBLE
                        } else {
                            binding.save.visibility = View.VISIBLE
                            binding.checkStatus.visibility = View.GONE
                        }
                    } else {
                        if (shipment == null && orderDetail == null) {
                            binding.txtError.visibility = View.GONE
                        } else {
                            binding.txtError.visibility = View.VISIBLE
                            if (isInboundShipment() || shipment != null) {
                                binding.errorText.text =
                                    "Non-Receivable BIN. Place for inspection before proceeding"
                            } else {
                                binding.errorText.text = "Please remove non-shippable items"
                            }
                        }
                    }
                }
            } else if (it is ScreenState.Error) {
                binding.progressBar.visibility = View.GONE
                if (isAdded) {
                    Toast.makeText(
                        requireActivity(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isInboundShipment() = shipment != null && shipment?.isInbound() == true

    private fun setupUI() {
        if (orderDetail != null) {
            binding.orderDate.text = orderDetail?.createdAt
            binding.orderId.text = orderDetail?.referenceId
            binding.customerName.text = orderDetail?.customer?.name
            binding.carrierName.text = orderDetail?.carrier?.name
        } else if (shipment != null) {
            binding.orderDate.text = shipment?.createdAt
            binding.orderId.text = shipment?.referenceId
            binding.customerName.text = shipment?.createdBy?.name
            binding.carrierName.text = shipment?.carrier?.name
        } else {
            binding.lnrItem.visibility = View.GONE
        }

        if (isInbound) {
            binding.save.visibility = View.VISIBLE
        } else {
            binding.checkStatus.visibility = View.VISIBLE
        }

        if (orderDetail == null && shipment == null) {
            binding.save.visibility = View.GONE
            if (!isCheckedStatus) {
                binding.checkStatus.visibility = View.VISIBLE
            } else {
                binding.checkStatus.visibility = View.GONE
            }
        }

        binding.save.setOnClickListener {
            if (mReader != null && mReader.isWorking) {
                mContext.highlightToast("Kindly Stop Reading First..", 2)
            } else {
                if (orderDetail != null) {
                    //Create Shipment Flow
                    val createShipmentRequest = CreateShipmentRequest()
                    val bills = ArrayList<InputBol>()
                    val tagsList = tagList.stream()
                        .map { map: HashMap<String, String?> -> map[TAG_EPC] }
                        .filter { obj: String? -> Objects.nonNull(obj) }
                        .collect(Collectors.toList())
                    bills.add(InputBol(orderDetail?.id, tagsList))
                    if (tagsList.size > maxQuantity) {
                        confirmationDialog()
                        return@setOnClickListener
                    }

                    createShipmentRequest.bols = bills
                    createShipmentRequest.carrier = orderDetail?.carrier?.id
                    createShipmentRequest.carrierName = orderDetail?.carrier?.name
                    val date = Date()
                    @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                    val formattedDate = formatter.format(date)
                    createShipmentRequest.shipmentDate = formattedDate
                    createShipmentRequest.driver = Driver("", "")
                    setCreateShipment(createShipmentRequest)
                    var orderShipmentData = getOrderToShipmentById(orderDetail?.id?:"")
                    if (orderShipmentData == null) {
                        orderShipmentData = OrderShipmentData(
                            orderDetail?.id?:"",
                            orderDetail?.referenceId?:"",
                            orderDetail?.getTotalCount()?:0,
                            tagsList.size,
                            tagsList as ArrayList<String>,
                            orderDetail
                        )
                    } else {
                        //todo:update logic here
                        val tags: ArrayList<String> = orderShipmentData.tags
                        tags.addAll(tagsList as ArrayList<String>)
                        tags.stream().distinct()
                        orderShipmentData.tags = tags
                    }
                    addOrUpdateOrderToShipment(orderShipmentData)
                    if (orderDetail != null) {
                        val intent = Intent(
                            requireActivity(),
                            PrepareShipment1Activity::class.java
                        )
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } else if (shipment != null) {
                    if (binding.tvCount.text.toString() == "0") {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireActivity(),
                                "Please scan the item",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val tagsList = tagList.stream()
                            .map { map: HashMap<String, String?> -> map[TAG_EPC] }
                            .filter { obj: String? -> Objects.nonNull(obj) }
                            .collect(Collectors.toList())
                        if (tagsList.size > maxQuantity) {
                            confirmationDialog()
                            return@setOnClickListener
                        }
                        val intent = Intent(
                            requireActivity(),
                            ShipmentDetailActivity::class.java
                        )
                        intent.putExtra("tags", Gson().toJson(tagsList))
                        intent.putExtra("SHIPMENT", Gson().toJson(shipment))
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }

        binding.checkStatus.setOnClickListener {
            binding.txtError.visibility = View.GONE
            val tagsList = tagList.stream()
                .map { map: HashMap<String, String?> -> map[TAG_EPC] }
                .filter { obj: String? -> Objects.nonNull(obj) }
                .collect(Collectors.toList())
            setUpCheckStatus(tagsList as ArrayList<String>)
        }
    }

    private fun confirmationDialog() {
        if (mContext.isFinishing || mContext.isDestroyed) return
        val alertDialog = AlertDialog.Builder(
            mContext
        ).setIcon(R.drawable.ic_logo).setMessage(
            if (shipment == null) {
                "Max Allowed Scans : $maxQuantity Qty"
            } else {
                "Max Allowed Scans : $maxQuantity Qty"
            }
        ).setPositiveButton(
                "Ok") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(mContext, R.color.app_color_red))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(mContext, R.color.rs_green))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as InventoryItemsActivity
        mContext = mainActivity
        mainActivity.currentFrag = this
        mContext.initSound()
        init()
    }

    fun init() {
        this.adapter = MyAdapter(this.mContext)
        binding.LvTags.adapter = this.adapter
        this.utils = Util(context)
        binding.btStart.setOnClickListener(this)
        binding.btClear.setOnClickListener(this)
        binding.LvTags.visibility = View.VISIBLE
        loadData(PreferenceManager.getStringValue(Constants.INV_ID_RFID))
    }

    override fun onClick(view: View) {
        if (view.id == R.id.bt_clear) {
            if (mContext.isBTDevice) {
                if (mBtReader.isWorking) {
                    mContext.highlightToast("Kindly Stop Reading First..", 2)
                } else {
                    clearDialog()
                }
            } else if (!mContext.isC5Device) {
                clearDialog()
            } else if (mReader.isWorking) {
                mContext.highlightToast("Kindly Stop Reading First..", 2)
            } else {
                clearDialog()
            }
        } else if (view.id == R.id.bt_start) {
            if (PreferenceManager.getStringValue(Constants.INV_ITEM_RFID).isEmpty()) {
                insertValues()
            } else if (mContext.isC5Device) {
                readTag()
            } else if (!mContext.isBTDevice) {
                mContext.highlightToast("Kindly Use RFID Device", 2)
            } else if (mContext.isBtConnect) {
                readTag()
            } else {
                mContext.highlightToast("Please Connect Device First..", 2)
            }
        }
    }

    fun btCancel(str: String) {
        if (str.contains("grey")) {
            binding.btClear.isEnabled = false
            return
        }
        binding.btClear.isEnabled = true
    }

    private fun getMiliSec(): String {
        val str = "RS_INV_" + Calendar.getInstance().timeInMillis
        this.miliSecString = str
        return str
    }

    private fun insertValues() {
        Completable.fromAction { this.mInsertValues() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                }
                override fun onComplete() {
                    PreferenceManager.setStringValue(
                        Constants.INV_ITEM_RFID,
                        this@InventoryItems.miliSecString
                    )
                    PreferenceManager.setStringValue(
                        Constants.INV_ID_RFID,
                        mContext.getInvId(this@InventoryItems.miliSecString).toString()
                    )
                    this@InventoryItems.readTag()
                }
                override fun onError(th: Throwable) {
                    Toast.makeText(requireActivity(), th.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun mInsertValues() {
        invListViewModel?.insert(
            InventoryListEntity(
                getMiliSec(),
                Util.getDateTime(),
                Util.getDateTime(),
                "0",
                PreferenceManager.getStringValue(
                    Constants.CUR_SC_TYPE
                )
            )
        )
    }

    override fun onPause() {
        if (mContext.isScanning) {
            stopInventory()
        }
        mContext.close()
        super.onPause()
    }

    override fun onResume() {
        mContext.checkBTConnect()
        super.onResume()
    }

    fun readTag() {
        if (PreferenceManager.getStringValue(Constants.GET_DEVICE) == "1") {
            if (!mContext.isBtConnect) {
                mContext.highlightToast("Kindly Connect Device First..", 2)
            } else if (mBtReader.startInventoryTag()) {
                clearData()
                binding.btStart.text =
                    mContext.getString(R.string.title_stop_Inventory)
                this.loopFlag = true
                TagThread().start()
            } else {
                stopInventory()
            }
        } else if (mReader == null) {
        } else {
            if (mReader.startInventoryTag()) {
                clearData()
                /*binding.imgScan.setVisibility(View.GONE);*/
                binding.btStart.text =
                    mContext.getString(R.string.title_stop_Inventory)
                this.loopFlag = true
                TagThread().start()
            } else {
                stopInventory()
            }
        }
    }

    private fun setItemsDetail(str: String): InventoryItemsEntity {
        val inventoryItemsEntity = InventoryItemsEntity()
        inventoryItemsEntity.epc = str
        inventoryItemsEntity.timeStamp = Util.getDateTime()
        inventoryItemsEntity.epcInv =
            str + "rs" + PreferenceManager.getStringValue(Constants.INV_ID_RFID)
        inventoryItemsEntity.inventory =
            PreferenceManager.getStringValue(Constants.INV_ID_RFID)
        return inventoryItemsEntity
    }

    private fun insertValues(inventoryItemsEntity: InventoryItemsEntity) {
        Completable.fromAction { invItemsViewModel?.insert(inventoryItemsEntity) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    scannedItems++
                }

                override fun onError(th: Throwable) {
                    this@InventoryItems.updateValues(inventoryItemsEntity)
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun loadData(str: String?) {
        utils?.showProgressBar("Please wait while data is loading...")
            invItemsViewModel?.getInvData(str, this.page, this.limit)?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(object : DisposableSingleObserver<List<InventoryItemsEntity?>?>() {
                    override fun onSuccess(list: List<InventoryItemsEntity?>) {
                        if (list.isNotEmpty()) {
                            setEpcData(list)
                            this@InventoryItems.btCancel("appColor")
                            return
                        }
                        this@InventoryItems.btCancel("grey")
                        utils?.hideProgressBar()
                    }

                    override fun onError(th: Throwable) {
                        utils?.hideProgressBar()
                    }
                })
    }

    fun updateValues(inventoryItemsEntity: InventoryItemsEntity?) {
        Completable.fromAction {
            this.updateValuesEntity(
                inventoryItemsEntity
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                }

                override fun onComplete() {
                }

                override fun onError(th: Throwable) {
                }
            })
    }

    private fun updateValuesEntity(inventoryItemsEntity: InventoryItemsEntity?) {
        invItemsViewModel?.update(inventoryItemsEntity)
    }

    private fun stopInventory() {
        if (this.loopFlag) {
            this.loopFlag = false
            if (PreferenceManager.getStringValue(Constants.GET_DEVICE) != "1" || mBtReader == null || !mContext.isBtConnect) {
                if (mReader != null) {
                    if (mReader.stopInventory()) {
                        binding.btStart.text =
                            mContext.getString(R.string.btInventory)
                    } else {
                        UIHelper.ToastMessage(this.mContext, R.string.uhf_msg_inventory_stop_fail)
                    }
                }
            } else if (mBtReader.stopInventory()) {
                binding.btStart.text =
                    mContext.getString(R.string.btInventory)
            } else {
                UIHelper.ToastMessage(this.mContext, R.string.uhf_msg_inventory_stop_fail)
            }
        }
        val mainActivity = this.mContext
        mainActivity.setInventoryItemCount(mainActivity.getItemCount())
    }

    fun mergeTidEpc(str: String, str2: String, str3: String?): String {
        var str4 = "EPC:$str2"
        if (!TextUtils.isEmpty(str) && str != "0000000000000000" && str != "000000000000000000000000") {
            str4 = "$str4\nTID:$str"
        }
        return if (str3.isNullOrEmpty()) str4 else "$str4\nUSER:$str3"
    }

    fun setTotalTime() {
        System.currentTimeMillis()
    }

    fun addDataToList(str: String, str2: String?, str3: String?, str4: String?, z: Boolean) {
        if (StringUtils.isNotEmpty(str)) {
            val checkIsExist = checkIsExist(str)
            val hashMap = HashMap<String, String?>()
            hashMap[TAG_EPC] = str
            hashMap[TAG_EPC_TID] = str2
            hashMap[TAG_COUNT] = 1.toString()
            hashMap[TAG_RSSI] = str3
            if (checkIsExist != -1) {
                try {
                    hashMap[TAG_COUNT] = (tagList[checkIsExist][TAG_COUNT]?.toInt(10)?.plus(1)).toString()
                    hashMap[TAG_EPC_TID] = str2
                    hashMap[TAG_RSSI_NUMBER] = str4
                    tagList[checkIsExist] = hashMap
                    updateValues(setItemsDetail(str))
                } catch (unused: Exception) {
                    tempData.remove(str)
                }
            } else if (tempData.size < 200) {
                this.isAleart = false
                tagList.add(hashMap)
                updateValidation()
                tempData.add(str)
                binding.tvCount.text = adapter?.count.toString()
                if (z) {
                    insertValues(setItemsDetail(str))
                }
                if (binding.tvCount.text.toString() == "0") {
                    binding.save.visibility = View.GONE
//                    if (!isInbound) {
                        isCheckedStatus = false
//                    }
                    binding.txtError.visibility = View.GONE
                    if (shipment == null) {
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    } else {
//                        binding.checkStatus.visibility = View.GONE
//                        binding.save.visibility = View.VISIBLE
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    }
                    btCancel("grey")
                } else {
                    if (!isCheckedStatus && shipment == null) {
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    } else {
                        if (orderDetail == null && shipment == null) {
                            binding.save.visibility = View.GONE
                            binding.checkStatus.visibility = View.VISIBLE
                        } else {
//                            binding.save.visibility = View.VISIBLE
//                            binding.checkStatus.visibility = View.GONE
                            binding.checkStatus.visibility = View.VISIBLE
                            binding.save.visibility = View.GONE
                        }
                    }
                    btCancel("appColor")
                }
            } else if (!this.isAleart) {
                this.isAleart = true
                stopInventory()
                Util.showfreeDialog(this.mContext)
            }
            if (z) {
                adapter?.notifyDataSetChanged()
            }
            uhfInfo.tempDatas = this.tempData
            uhfInfo.tagList = this.tagList
            uhfInfo.count = this.total
            uhfInfo.tagNumber = adapter?.count?:0
        }
    }

    private fun updateValidation() {
        if (tagList.size == 0) {
            binding.save.isEnabled = false
            binding.checkStatus.isEnabled = false
        } else {
            binding.save.isEnabled = true
            binding.checkStatus.isEnabled = true
        }
    }

    private fun checkIsExist(str: String): Int {
        if (StringUtils.isEmpty(str)) {
            return -1
        }
        return binarySearch(this.tempData, str)
    }

    class ViewHolder {
        var llList: LinearLayout? = null
        var tvEPCTID: TextView? = null
        var tvTagCount: TextView? = null
        var tvTagRssi: TextView? = null
        var txtUnknown: TextView? = null
        var ivDelete: ImageView? = null
    }

    inner class MyAdapter(context: Context?) : BaseAdapter() {
        private val mInflater: LayoutInflater = LayoutInflater.from(context)

        private var isScanning: Boolean = false

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getCount(): Int {
            return tagList.size
        }

        override fun getItem(i: Int): Any {
            return tagList[i]
        }

        @SuppressLint("SetTextI18n", "InflateParams")
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val view2: View
            val viewHolder: ViewHolder
            if (view == null) {
                viewHolder = ViewHolder()
                view2 = mInflater.inflate(R.layout.list_tag_item, null)
                viewHolder.tvEPCTID = view2.findViewById(R.id.TvTagUii)
                viewHolder.tvTagCount = view2.findViewById(R.id.TvTagCount)
                viewHolder.tvTagRssi = view2.findViewById(R.id.TvTagRssi)
                viewHolder.llList = view2.findViewById(R.id.ll_list)
                viewHolder.ivDelete = view2.findViewById(R.id.btnDelete)
                viewHolder.txtUnknown = view2.findViewById(R.id.txtUnknown)
                view2.tag = viewHolder
            } else {
                view2 = view
                viewHolder = view.tag as ViewHolder
            }
            viewHolder.tvEPCTID?.text =
                "EPC [Hex]: " + tagList[i][TAG_EPC]
            viewHolder.tvTagCount?.text =
                "Read Count: " + tagList[i][TAG_COUNT]
            viewHolder.tvTagRssi?.text =
                "RSSI: " + tagList[i][TAG_RSSI_NUMBER]

            Log.e("TAG243", "status: "+tagList[i]["status"])
            Log.e("TAG243", "isCheckedStatus: $isCheckedStatus")
            val status =
                if (tagList[i]["status"] == null && tagList.any { it["status"] != null }) "UNKNOWN" else tagList[i]["status"]?.ifBlank { "UNKNOWN" }
            status.getStatusColor().let { viewHolder.txtUnknown?.setTextColor(it) }
            viewHolder.txtUnknown?.text = status
            viewHolder.llList?.setOnClickListener {
                val stringStringHashMap =
                    tagList[i]
                val data = Data(
                    stringStringHashMap.getOrDefault(TAG_EPC, ""),
                    stringStringHashMap.getOrDefault(TAG_COUNT, ""),
                    stringStringHashMap.getOrDefault(TAG_RSSI_NUMBER, "")
                )
                var status: String? = ""
                if (tagList[i].containsKey("status")) {
                    status = tagList[i]["status"]
                }
                callback?.onClickListener(
                    Gson().toJson(data),
                    status
                )
            }
            if (i == this@InventoryItems.selectItem) {
                view2.setBackgroundColor(ContextCompat.getColor(mContext,R.color.app_color))
            } else {
                view2.setBackgroundColor(0)
            }
            if (isScanning) {
                viewHolder.ivDelete?.visibility = View.GONE
            } else {
                viewHolder.ivDelete?.visibility = View.VISIBLE
            }
            viewHolder.ivDelete?.setOnClickListener {
                tagList.removeAt(
                    i
                )
                updateValidation()
                binding.tvCount.text = count.toString()
                if (count == 0) {
                    binding.save.visibility = View.GONE
//                    if (!isInbound) {
                        isCheckedStatus = false
//                    }
                    binding.txtError.visibility = View.GONE
                    if (shipment == null) {
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    } else {
//                        binding.checkStatus.visibility = View.GONE
//                        binding.save.visibility = View.VISIBLE
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    }
                } else {
                    if (!isCheckedStatus && shipment == null) {
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    } else {
                        if (orderDetail == null && shipment == null) {
                            binding.save.visibility = View.GONE
                            binding.checkStatus.visibility = View.VISIBLE
                        } else {
//                            binding.save.visibility = View.VISIBLE
//                            binding.checkStatus.visibility = View.GONE
                            binding.checkStatus.visibility = View.VISIBLE
                            binding.save.visibility = View.GONE
                        }
                    }
                }
                notifyDataSetChanged()
            }
            return view2
        }

        private fun String?.getStatusColor(): Int {
            val isColored: Boolean
            val shouldBeGreen = if (shipment != null) {
                isColored = true
                this.equals("ASSIGNED", ignoreCase = true) || this.equals(
                    "PROCESSING",
                    ignoreCase = true
                )
            } else if (orderDetail != null) {
                isColored = true
                this.equals("CLEANED", ignoreCase = true)
            } else {
                isColored = false
                false
            }
            return if (shouldBeGreen) {
                ContextCompat.getColor(requireActivity(), R.color.rs_green)  // Your green color
            } else {
                if (isColored) {
                    ContextCompat.getColor(requireActivity(), R.color.red)    // Your red color
                } else {
                    ContextCompat.getColor(requireActivity(), R.color.color_0F0F0F)
                }
            }
        }
    }

    private fun clearDialog() {
        AlertDialog.Builder(requireActivity()).setIcon(R.drawable.delete_24).setTitle("Clear Data")
            .setMessage("Do you want to clear all data?").setPositiveButton(
                "Yes"
            ) { dialogInterface: DialogInterface, _: Int ->
                clearData()
                dialogInterface.dismiss()
            }.setNegativeButton("No", null).show()
    }

    internal inner class TagThread : Thread() {
        override fun run() {
            var uHFTAGInfo: UHFTAGInfo? = null
            while (this@InventoryItems.loopFlag) {
                if (PreferenceManager.getStringValue(Constants.GET_DEVICE) == "1" && mBtReader != null && mContext.isBtConnect) {
                    uHFTAGInfo = mBtReader.readTagFromBuffer()
                } else if (mReader != null) {
                    uHFTAGInfo = mReader.readTagFromBuffer()
                }
                if (uHFTAGInfo != null) {
                    val obtainMessage = handler.obtainMessage()
                    obtainMessage.obj = uHFTAGInfo
                    handler.sendMessage(obtainMessage)
                    mContext.playSound(1)
                }
            }
        }
    }

    fun clearData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val db = databaseBuilder(
                requireContext(),
                InvDB::class.java,
                "Inventory_db"
            ).fallbackToDestructiveMigration()
                .build()

            val invItemsDao = db.invItemsDao()

            val epcList = tagList.mapNotNull { it[TAG_EPC] }

            val result = invItemsDao.delData(epcList) > 0
            invItemsDao.deleteAllData()
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE

                if (result) {
                    tagList.clear()
                    updateValidation()
                    scannedItems = 0
                    /*if (!isInbound)*/ isCheckedStatus = false

                    binding.txtError.visibility = View.GONE
                    binding.tvCount.text = "0"

                    if (shipment == null) {
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    } else {
//                        binding.checkStatus.visibility = View.GONE
//                        binding.save.visibility = View.VISIBLE
                        binding.checkStatus.visibility = View.VISIBLE
                        binding.save.visibility = View.GONE
                    }

                    btCancel("grey")
                    adapter?.notifyDataSetChanged()
                    mContext.setInventoryItemCount(mContext.getItemCount())
                }
            }
        }
    }

    private fun setEpcData(epcList: List<InventoryItemsEntity?>) {
        lifecycleScope.launch(Dispatchers.IO) {
            for (item in epcList) {
                this@InventoryItems.addDataToList(
                    item?.epc ?: "",
                    "",
                    item?.timeStamp ?: "",
                    "",
                    false
                )
            }

            withContext(Dispatchers.Main) {
                adapter?.notifyDataSetChanged()
                utils?.hideProgressBar()
            }
        }
    }

    private fun setUpCheckStatus(tagsList: List<String>) {
        val jsonArray = convertListToJsonArray(tagsList)
        assetViewModel?.getAssetByTagID(jsonArray)
    }

    private fun convertListToJsonArray(tagsList: List<String>): JsonArray {
        val jsonArray = JsonArray()
        for (tag in tagsList) {
            jsonArray.add(JsonPrimitive(tag))
        }
        return jsonArray
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        const val TAG_COUNT: String = "tagCount"
        const val TAG_EPC: String = "tagEPC"
        const val TAG_EPC_TID: String = "tagEpcTID"
        const val TAG_RSSI: String = "tagRssi"
        const val TAG_RSSI_NUMBER: String = "tagRssiNumber"

        fun binarySearch(list: List<String>, str: String): Int {
            var i = 0
            var size2 = list.size - 1
            while (i <= size2) {
                if (compareString(list[i], str)) {
                    return i
                }
                if (i != size2 && compareString(list[size2], str)) {
                    return size2
                }
                i++
                size2--
            }
            return -1
        }

        private fun compareString(str: String, str2: String): Boolean {
            if (str.length != str2.length || str.hashCode() != str2.hashCode()) {
                return false
            }
            val charArray = str.toCharArray()
            val charArray2 = str2.toCharArray()
            val length = charArray.size
            for (i in 0 until length) {
                if (charArray[i] != charArray2[i]) {
                    return false
                }
            }
            return true
        }

        fun newInstance(
            orderDetailString: String?,
            shipmentString: String?,
            maxQuantity: Int
        ): InventoryItems {
            val inventoryItems = InventoryItems()
            val bundle = Bundle()
            bundle.putString(ARG_PARAM1, orderDetailString)
            bundle.putString(ARG_PARAM2, shipmentString)
            bundle.putInt(ARG_PARAM3, maxQuantity)
            inventoryItems.arguments = bundle
            return inventoryItems
        }
    }
}