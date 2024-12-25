package com.example.rfidapp.activity

import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityInspectionProcessBinding
import com.example.rfidapp.databinding.ActivitySettingsBinding
import com.example.rfidapp.fragment.AppSettings
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.util.ActBase

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

    }
}