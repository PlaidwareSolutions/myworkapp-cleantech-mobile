package com.example.rfidapp.activity

import android.content.Intent
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ActivityBolBinding
import com.example.rfidapp.util.ActBase

class BoLActivity : ActBase<ActivityBolBinding>() {

    override fun setViewBinding() = ActivityBolBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                toolbarTitle.text = getString(R.string.bill_of_lading)
                btnBack.setOnClickListener {
                    finish()
                }
            }
        }

        binding.createBol.setOnClickListener {

        }

        binding.lookupBol.setOnClickListener {
            startActivity(Intent(this@BoLActivity, PrepareShipmentActivity::class.java).putExtra("shipmentType", "lookup"))
        }
    }

    override fun bindMethods() {

    }

}