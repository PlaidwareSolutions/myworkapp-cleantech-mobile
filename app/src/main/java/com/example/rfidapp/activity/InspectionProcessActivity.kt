package com.example.rfidapp.activity

import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityInspectionProcessBinding
import com.example.rfidapp.fragment.AddInspectionFragment
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.model.Data
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.PreferenceManager
import com.example.rfidapp.util.constants.Constants
import com.example.rfidapp.util.fromJson
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InspectionProcessActivity : ActBase<ActivityInspectionProcessBinding>() {

    override fun setViewBinding() = ActivityInspectionProcessBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.inspection_process)
                btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }

    override fun bindMethods() {
        PreferenceManager.setStringValue(Constants.CUR_SC_TYPE, "Rfid")
        val inventoryItems = InventoryItems.newInstance("" ,"")
        inventoryItems.setCallback { data ->
            //Item click
            val data: Data = Gson().fromJson(json = data)
            val addInspectionFragment = AddInspectionFragment.newInstance(data.tagEpc)
            addInspectionFragment.show(
                supportFragmentManager,
                addInspectionFragment.tag
            )

        }
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, inventoryItems)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        checkBTConnect()
    }
}