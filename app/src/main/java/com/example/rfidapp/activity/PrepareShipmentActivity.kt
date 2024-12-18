package com.example.rfidapp.activity

import android.content.Intent
import android.util.Log
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