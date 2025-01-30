package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.rfidapp.databinding.ActivityHomeScreenBinding
import com.example.rfidapp.databinding.ActivityInspectionProcessBinding
import com.example.rfidapp.util.ActBase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreenActivity :  ActBase<ActivityHomeScreenBinding>() {

    override fun setViewBinding() = ActivityHomeScreenBinding.inflate(layoutInflater)

    override fun bindObjects() {}

    override fun bindListeners() {}

    override fun bindMethods() {
        setupUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        binding.apply {
            toolbar.apply {
                btnBack.isVisible = false
                toolbarTitle.text = "MyWorkApp"
            }

            orders.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, PrepareShipmentActivity::class.java).putExtra("shipmentType", "orders"))
            }

            billOfLading.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, CreateBoLActivity::class.java))
            }
            iconImage.setOnClickListener {
                scanningToggleVisibility()
            }

            beginScanning.setOnClickListener {
                scanningToggleVisibility()
            }

            shipping.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, PrepareShipmentActivity::class.java).putExtra("shipmentType", "shipping"))
            }

            receiving.setOnClickListener {
                startActivity(
                    Intent(
                        this@HomeScreenActivity,
                        ShipmentListActivity::class.java
                    )
                )
            }

            inventory.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, InventoryItemsActivity::class.java).putExtra("isInspection",true))
            }
            settings.setOnClickListener {
                startActivity(Intent(this@HomeScreenActivity, SettingsActivity::class.java))
            }
        }
    }

    private fun scanningToggleVisibility() {
        binding.apply {
            if (beginScanning.isVisible) {
                beginScanning.isVisible = false
                shippingReceiving.isVisible = true
            } else {
                shippingReceiving.isVisible = false
                beginScanning.isVisible = true
            }
        }
    }
}