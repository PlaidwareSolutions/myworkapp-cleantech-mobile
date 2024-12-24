package com.example.rfidapp.activity

import android.content.Intent
import com.example.rfidapp.databinding.ActivityInventoryBinding
import com.example.rfidapp.fragment.InventoryItems
import com.example.rfidapp.util.ActBase

class InventoryActivity : ActBase<ActivityInventoryBinding>() {

    override fun setViewBinding() = ActivityInventoryBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.imageButtonBack.setOnClickListener {
            finish()
        }

        binding.tagFinder.setOnClickListener {
            startActivity(Intent(this, TagFinderActivity::class.java))
        }
    }

    override fun bindMethods() {

    }

}