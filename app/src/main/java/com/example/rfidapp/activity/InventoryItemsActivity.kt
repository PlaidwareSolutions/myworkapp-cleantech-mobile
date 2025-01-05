package com.example.rfidapp.activity

import androidx.room.Room.databaseBuilder
import com.example.rfidapp.database.InvDB
import com.example.rfidapp.databinding.ActivityInventoryItemsBinding
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.PreferenceManager
import com.example.rfidapp.util.constants.Constants
import com.example.rfidapp.util.fromJson
import com.google.gson.Gson

class InventoryItemsActivity : ActBase<ActivityInventoryItemsBinding>() {

    override fun setViewBinding() = ActivityInventoryItemsBinding.inflate(layoutInflater)

    var orderDetail: OrderDetail?= null

    override fun bindObjects() {
        orderDetail = Gson().fromJson(intent.getStringExtra("orderDetail") ?: "")
    }

    override fun bindListeners() {
        binding.imageButtonBack.setOnClickListener {
            finish()
        }
    }

    override fun bindMethods() {
        PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Rfid")
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, InventoryItems.newInstance(intent.getStringExtra("orderDetail") ,""))
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