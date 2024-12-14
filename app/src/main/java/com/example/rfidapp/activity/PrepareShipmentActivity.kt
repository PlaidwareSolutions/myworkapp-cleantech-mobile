package com.example.rfidapp.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.OrderAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipmentBinding
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ItemMarginDecoration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class PrepareShipmentActivity : ActBase<ActivityPrepareShipmentBinding>() {

    override fun setViewBinding() = ActivityPrepareShipmentBinding.inflate(layoutInflater)

    override fun bindObjects() {

    }

    override fun bindListeners() {

    }

    override fun bindMethods() {
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_margin)
        binding.rcvOrders.addItemDecoration(ItemMarginDecoration(marginInPixels))
        val adapter = OrderAdapter(
            activity = this,
            orderList = listOf(1, 2, 3, 4),
            onItemClick = {
                Log.e("TAG111", "bindMethods: $it")
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = adapter
    }
}