package com.example.rfidapp.activity

import androidx.room.Room.databaseBuilder
import com.example.rfidapp.database.InvDB
import com.example.rfidapp.databinding.ActivityInventoryItemsBinding
import com.example.rfidapp.fragment.AddInspectionFragment
import com.example.rfidapp.fragment.InspectionFragment
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.model.Data
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.model.network.orderdetail.OrderDetail
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.PreferenceManager
import com.example.rfidapp.util.constants.Constants
import com.example.rfidapp.util.fromJson
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryItemsActivity : ActBase<ActivityInventoryItemsBinding>() {

    override fun setViewBinding() = ActivityInventoryItemsBinding.inflate(layoutInflater)

    private var orderDetail: OrderDetail? = null
    private var shipment: Shipment? = null
    private var isInspection:Boolean = false
    private var maxQuantity = 10000

    override fun bindObjects() {
        intent.getStringExtra("orderDetail")?.let {
            orderDetail = Gson().fromJson<OrderDetail>(it)
        }
        intent.getStringExtra("SHIPMENT")?.let {
            shipment = Gson().fromJson<Shipment>(it)
        }
        maxQuantity = intent.getIntExtra("maxQuantity",10000)

        intent.getBooleanExtra("isInspection",false).let {
            isInspection = it
        }

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = "Scan Item"
                btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }


    override fun bindMethods() {
        PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Rfid")
        val inventoryItems = InventoryItems.newInstance(
            intent.getStringExtra("orderDetail"),
            intent.getStringExtra("SHIPMENT"),
            maxQuantity
        )
        inventoryItems.setCallback { data ->
            //Item click
            if (isInspection) {
                val data: Data? = Gson().fromJson(json = data)
                val tagId = data?.tagEpc ?: ""
                val inspectionFragment: AddInspectionFragment =
                    AddInspectionFragment.newInstance(tagId, {
                        if (it) {
                            inventoryItems.clearDataAsyncTask.execute()
                        }
                    })
                inspectionFragment.show(
                    supportFragmentManager,
                    inspectionFragment.tag
                )
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainerView.id,
                inventoryItems
            )
            .commit()
    }

    fun getInvId(str: String?): Int {
        return databaseBuilder(
            applicationContext,
            InvDB::class.java,
            "Inventory_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao()
            .getInvId(str)
    }

    fun setInventoryName(str: String?, str2: String?): Int {
        return databaseBuilder(
            applicationContext,
            InvDB::class.java,
            "Inventory_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao()
            .updateName(str, str2)
    }

    fun getItemCount(): String {
        return if ((PreferenceManager.getStringValue(Constants.INV_ID_RFID) == "null" || PreferenceManager.getStringValue(
                Constants.INV_ID_RFID
            ) == "")
        ) "0" else databaseBuilder(
            applicationContext,
            InvDB::class.java, "Inventory_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build().invItemsDao()
            .getItemCount(
                PreferenceManager.getStringValue(Constants.INV_ID_RFID)
            ).toString()
    }

    fun getItemBarCount(): String {
        return if ((PreferenceManager.getStringValue(Constants.INV_ID_BAR) == "null" || PreferenceManager.getStringValue(
                Constants.INV_ID_BAR
            ) == "")
        ) "0" else databaseBuilder(
            applicationContext,
            InvDB::class.java, "Inventory_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build().invItemsDao()
            .getItemCount(
                PreferenceManager.getStringValue(Constants.INV_ID_BAR)
            ).toString()
    }

    fun setInventoryItemCount(str: String?) {
        databaseBuilder(
            applicationContext,
            InvDB::class.java,
            "Inventory_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao().update(
            PreferenceManager.getStringValue(Constants.INV_ID_RFID), str
        )
    }

    fun setInventoryItemBarCount(str: String?) {
        databaseBuilder(
            applicationContext,
            InvDB::class.java,
            "Inventory_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build().invListDao().update(
            PreferenceManager.getStringValue(Constants.INV_ID_BAR), str
        )
    }

    override fun onResume() {
        super.onResume()
        checkBTConnect()
    }
}