package com.example.rfidapp.activity

import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityInventoryBinding
import com.example.rfidapp.databinding.ActivityTagFinderBinding
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.fragment.SingleSearch
import com.example.rfidapp.util.ActBase

class TagFinderActivity : ActBase<ActivityTagFinderBinding>() {

    override fun setViewBinding() = ActivityTagFinderBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.tag_finder)
                btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }

    override fun bindMethods() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, SingleSearch.newInstance("", ""))
            .commit()
    }

}