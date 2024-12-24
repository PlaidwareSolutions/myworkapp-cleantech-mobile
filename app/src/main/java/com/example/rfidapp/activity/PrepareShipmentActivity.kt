package com.example.rfidapp.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.OrderAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipmentBinding
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ItemMarginDecoration
import com.example.rfidapp.viewmodel.PrepareShipmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrepareShipmentActivity : ActBase<ActivityPrepareShipmentBinding>() {

    private val viewModel: PrepareShipmentViewModel by viewModels()

    override fun setViewBinding() = ActivityPrepareShipmentBinding.inflate(layoutInflater)

    override fun bindObjects() {
        val shipmentType = intent.getStringExtra("shipmentType")
        binding.apply {
            if (shipmentType == "receiving" || shipmentType == "lookup") {
                customerButton.text = getString(R.string.bill_of_lading_)
            } else {
                customerButton.text = getString(R.string.customer)
            }
            when (shipmentType) {
                "shipping" -> {
                    orderButton.visibility = View.VISIBLE
                    customerButton.visibility = View.VISIBLE
                    carrierButton.visibility = View.VISIBLE
                    textViewTitle.text = getString(R.string.prepare_shipment)
                }
                "receiving" -> {
                    orderButton.visibility = View.GONE
                    customerButton.visibility = View.VISIBLE
                    carrierButton.visibility = View.VISIBLE
                    textViewTitle.text = getString(R.string.receive_shipment)

                }
                "orders" -> {
                    orderButton.visibility = View.VISIBLE
                    customerButton.visibility = View.VISIBLE
                    carrierButton.visibility = View.GONE
                    textViewTitle.text = getString(R.string.order_search)

                }
                "lookup" -> {
                    orderButton.visibility = View.GONE
                    customerButton.visibility = View.VISIBLE
                    carrierButton.visibility = View.VISIBLE
                    textViewTitle.text = getString(R.string.receive_shipment)
                }
                else -> {
                    orderButton.visibility = View.GONE
                    customerButton.visibility = View.GONE
                    carrierButton.visibility = View.GONE
                }
            }
        }
    }

    override fun bindListeners() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.selectedOrder.collectLatest { order ->
                runOnUiThread {
                    binding.orderDetailsButton.isEnabled = order != null
                }
            }
        }

        binding.orderDetailsButton.setOnClickListener {
            startActivity(Intent(this, OrderDetailActivity::class.java))
        }

        binding.imageButtonBack.setOnClickListener {
            finish()
        }
    }

    override fun bindMethods() {
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_margin)
        binding.rcvOrders.addItemDecoration(ItemMarginDecoration(marginInPixels))
        val adapter = OrderAdapter(
            activity = this,
            orderList = listOf(1, 2, 3, 4),
            onItemClick = {
                Log.e("TAG111", "bindMethods: $it")
                viewModel.selectedOrder.value = it
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }
}