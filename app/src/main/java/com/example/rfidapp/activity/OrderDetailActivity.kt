package com.example.rfidapp.activity

import android.content.Intent
import com.example.rfidapp.databinding.ActivityOrderDetailBinding
import com.example.rfidapp.util.ActBase

class OrderDetailActivity : ActBase<ActivityOrderDetailBinding>() {

    override fun setViewBinding() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.scanButton.setOnClickListener {
            startActivity(Intent(this, InventoryItemsActivity::class.java))
        }
    }

    override fun bindMethods() {

    }
}