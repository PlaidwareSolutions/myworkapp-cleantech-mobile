package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.adapter.ShipmentDetailAdapter
import com.example.rfidapp.databinding.ActivityShipmentDetailBinding
import com.example.rfidapp.model.network.Item
import com.example.rfidapp.model.network.ReceiveShipmentRequest
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.fromJson
import com.example.rfidapp.util.toFormattedDate
import com.example.rfidapp.viewmodel.ShipmentViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShipmentDetailActivity : ActBase<ActivityShipmentDetailBinding>() {
    private val viewModel: ShipmentViewModel by viewModels()

    val shipment by lazy { Gson().fromJson<Shipment>(intent.getStringExtra("SHIPMENT") ?: "") }
    private val tags by lazy {
        Gson().fromJson<List<String>>(intent.getStringExtra("tags") ?: "")
    }

    override fun setViewBinding() = ActivityShipmentDetailBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    private fun updateOrderDetail() {
        binding.shipmentId.text = shipment.referenceId
        binding.carrierName.text = shipment.carrier?.name
        binding.customerName.text = shipment.createdBy?.name
//      binding.pickupDate.text = shipment.?.toFormattedDate()
        binding.shipmentDate.text = shipment.shipmentDate?.toFormattedDate()
        shipment.bols?.firstOrNull()?.items?.let {
            binding.rcvOrders.isVisible = true
            binding.lnrContent.isVisible = true
            if (it.isNotEmpty()) {
                binding.tvReq.isVisible = it.first().requiredQuantity != null && it.first().requiredQuantity != 0
                initView(it)
            }
        } ?: run {
            binding.rcvOrders.isVisible = false
            binding.lnrContent.isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindListeners() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.receiveShipmentList.collectLatest {
                if (it is ScreenState.Success) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ShipmentDetailActivity,
                            "Received Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@ShipmentDetailActivity, HomeScreenActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else if (it is ScreenState.Error) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ShipmentDetailActivity,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
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
                    viewModel.receiveShipments(
                        shipmentId = shipment.id ?: "",
                        shipmentRequest = ReceiveShipmentRequest(tags)
                    )
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
                    finish()
                }
            }
        }
    }

    override fun bindMethods() {
        updateOrderDetail()
    }

    private fun initView(items: List<Item>) {
        val adapter = ShipmentDetailAdapter(
            activity = this,
            orderList = items,
            foundQuantity = tags.size
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }

}