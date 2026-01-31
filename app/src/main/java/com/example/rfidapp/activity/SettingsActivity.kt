package com.example.rfidapp.activity

import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivitySettingsBinding
import com.example.rfidapp.fragment.AppSettings
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.util.ActBase

class SettingsActivity : ActBase<ActivitySettingsBinding>() {

    override fun setViewBinding() = ActivitySettingsBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.settings)
                btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }

    override fun bindMethods() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, AppSettings.newInstance("",""))
            .commit()
    }
}