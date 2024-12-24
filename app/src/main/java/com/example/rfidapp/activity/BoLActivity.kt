package com.example.rfidapp.activity

import android.content.Intent
import com.example.rfidapp.databinding.ActivityBolBinding
import com.example.rfidapp.util.ActBase

class BoLActivity : ActBase<ActivityBolBinding>() {

    override fun setViewBinding() = ActivityBolBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.imageButtonBack.setOnClickListener {
            finish()
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