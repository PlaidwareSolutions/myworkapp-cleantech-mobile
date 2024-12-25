package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrepareShipmentActivity : ActBase<ActivityPrepareShipmentBinding>() {

    private val viewModel: PrepareShipmentViewModel by viewModels()
    private var shipmentType : String = ""

    override fun setViewBinding() = ActivityPrepareShipmentBinding.inflate(layoutInflater)

    override fun bindObjects() {
        shipmentType = intent.getStringExtra("shipmentType")?:""
    }

    override fun bindListeners() {
        setSearchViewListner()

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

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun bindMethods() {
        initTabs()

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

    private fun initTabs() {
        binding.apply {
            when (shipmentType) {
                "shipping" -> {
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.order)))
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.customer)))
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.carrier)))
                    toolbar.toolbarTitle.text = getString(R.string.prepare_shipment)

                }
                "receiving" -> {
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.bill_of_lading_)))
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.carrier)))
                    toolbar.toolbarTitle.text = getString(R.string.receive_shipment)

                }
                "orders" -> {
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.order)))
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.customer)))
                    toolbar.toolbarTitle.text = getString(R.string.order_search)
                }
                "lookup" -> {
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.bill_of_lading_)))
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.carrier)))
                    toolbar.toolbarTitle.text = getString(R.string.receive_shipment)
                }
                else -> {
                }
            }
        }



    }

    private fun setSearchViewListner(){
        binding.apply {
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val closeIcon: Drawable? = if (!s.isNullOrEmpty()) getDrawable(R.drawable.ic_close) else null
                    closeIcon?.setBounds(0, 0, closeIcon.intrinsicWidth, closeIcon.intrinsicHeight)

                    val searchIcon: Drawable? = getDrawable(R.drawable.ic_search)
                    searchIcon?.setBounds(0, 0, searchIcon.intrinsicWidth, searchIcon.intrinsicHeight)

                    search.setCompoundDrawables(
                        searchIcon,
                        null,
                        closeIcon,
                        null
                    )
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            search.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableEnd = search.compoundDrawables[2]
                    if (drawableEnd != null) {
                        val drawableWidth = drawableEnd.bounds.width()
                        val touchAreaStart = search.width - search.paddingEnd - drawableWidth
                        if (event.rawX >= touchAreaStart) {
                            search.text.clear()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
    }
}