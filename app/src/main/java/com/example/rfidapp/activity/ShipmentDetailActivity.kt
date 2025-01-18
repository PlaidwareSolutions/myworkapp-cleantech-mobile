package com.example.rfidapp.activity

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.ShipmentDetailAdapter
import com.example.rfidapp.databinding.ActivityShipmentDetailBinding
import com.example.rfidapp.model.network.Item
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.fromJson
import com.example.rfidapp.util.toFormattedDate
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShipmentDetailActivity : ActBase<ActivityShipmentDetailBinding>() {

    val shipment by lazy { Gson().fromJson<Shipment>(intent.getStringExtra("SHIPMENT") ?: "") }

    override fun setViewBinding() = ActivityShipmentDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    private fun updateOrderDetail() {
        binding.shipmentId.text = shipment.referenceId
        binding.carrierName.text = shipment.carrier?.name
        binding.customerName.text = shipment.createdBy?.name
//        binding.pickupDate.text = shipment.?.toFormattedDate()
        binding.shipmentDate.text = shipment.shipmentDate?.toFormattedDate()
        shipment.bols?.first()?.items?.let {
            binding.rcvOrders.isVisible = true
            binding.lnrContent.isVisible = true
            initView(it)
        } ?: run {
            binding.rcvOrders.isVisible = false
            binding.lnrContent.isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindListeners() {
        binding.apply {
            toolbar.apply {
                btnBack.setOnClickListener {
                    finish()
                }

                toolbarTitle.text = "Shipment Details"
            }

            footer.apply {
                outlinedOutlined.text = "Scan for receiving"
                filledButton.text = "Complete Receiving"

                filledButton.setOnClickListener {
//                    startActivity(
//                        Intent(
//                            this@ShipmentDetailActivity,
//                            InventoryItemsActivity::class.java
//                        ).putExtra("orderDetail", Gson().toJson(orderDetail))
//                    )
//                    finish()
                }
                outlinedOutlined.setOnClickListener {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        runOnUiThread {
//                            progressBar.isVisible = true
//                        }
//                        viewModel.fetchOrderPdf().collectLatest {
//                            runOnUiThread {
//                                progressBar.isVisible = false
//                                it.data?.url?.let { it1 -> openPdf(it1) }
//                            }
//                        }
//                    }
                }
            }
        }
    }

    override fun bindMethods() {
    }

    private fun initView(items: List<Item>) {
        val adapter = ShipmentDetailAdapter(
            activity = this,
            orderList = items,
            foundQuantity = 0
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }

}