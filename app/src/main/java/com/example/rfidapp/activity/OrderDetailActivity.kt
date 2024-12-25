package com.example.rfidapp.activity

import android.content.Intent
import com.example.rfidapp.databinding.ActivityOrderDetailBinding
import com.example.rfidapp.util.ActBase

class OrderDetailActivity : ActBase<ActivityOrderDetailBinding>() {

    override fun setViewBinding() = ActivityOrderDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                btnBack.setOnClickListener {
                    finish()
                }
            }

            footer.apply {
                filledButton.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OrderDetailActivity,
                            InventoryItemsActivity::class.java
                        )
                    )
                }

                outlinedOutlined.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OrderDetailActivity,
                            InventoryItemsActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun bindMethods() {

    }
}