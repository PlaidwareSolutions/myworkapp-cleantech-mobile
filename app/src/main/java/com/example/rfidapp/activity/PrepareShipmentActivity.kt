package com.example.rfidapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidapp.R
import com.example.rfidapp.adapter.OrderAdapter
import com.example.rfidapp.databinding.ActivityPrepareShipmentBinding
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.util.ActBase
import com.example.rfidapp.util.ItemMarginDecoration
import com.example.rfidapp.viewmodel.OrderViewModel
import com.example.rfidapp.viewmodel.PrepareShipmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrepareShipmentActivity : ActBase<ActivityPrepareShipmentBinding>() {

    private val orderViewModel: OrderViewModel by viewModels()

    private val viewModel: PrepareShipmentViewModel by viewModels()
    private var shipmentType: String = ""

    private var orderAdapter: OrderAdapter? = null

    override fun setViewBinding() = ActivityPrepareShipmentBinding.inflate(layoutInflater)

    override fun bindObjects() {
        shipmentType = intent.getStringExtra("shipmentType") ?: ""
    }

    override fun bindListeners() {
        setSearchViewListener()

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.selectedOrder.collectLatest { order ->
                runOnUiThread {
                    binding.orderDetailsButton.isEnabled = order != null
                }
            }
        }

        binding.orderDetailsButton.setOnClickListener {
            viewModel.selectedOrder.value?.id?.let {
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", it)
                startActivity(intent)
            }
        }

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            orderViewModel.orderList.collectLatest {
                runOnUiThread {
                    orderAdapter?.updateData(it) ?: run {
                        initAdapter(it as ArrayList<Order>)
                    }
                }
            }
        }
    }

    override fun bindMethods() {
        initTabs()
        initAdapter()
    }

    private fun initAdapter(orderList: ArrayList<Order> = arrayListOf()) {
        val marginInPixels = resources.getDimensionPixelSize(R.dimen.item_margin)
        binding.rcvOrders.addItemDecoration(ItemMarginDecoration(marginInPixels))
        orderAdapter = OrderAdapter(
            activity = this,
            orderList = orderList,
            onItemClick = {
                viewModel.selectedOrder.value = it
            }
        )
        binding.rcvOrders.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvOrders.adapter = orderAdapter
    }

    private fun initTabs() {
        binding.apply {
            when (shipmentType) {
                "shipping" -> {
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.order)))
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.customer))
                    )
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.carrier))
                    )
                    toolbar.toolbarTitle.text = getString(R.string.prepare_shipment)

                }

                "receiving" -> {
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.bill_of_lading_))
                    )
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.carrier))
                    )
                    toolbar.toolbarTitle.text = getString(R.string.receive_shipment)

                }

                "orders" -> {
                    tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.order)))
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.customer))
                    )
                    toolbar.toolbarTitle.text = getString(R.string.order_search)
                }

                "lookup" -> {
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.bill_of_lading_))
                    )
                    tabLayout.addTab(
                        binding.tabLayout.newTab().setText(getString(R.string.carrier))
                    )
                    toolbar.toolbarTitle.text = getString(R.string.receive_shipment)
                }

                else -> {
                }
            }
        }


    }

    private fun setSearchViewListener() {
        binding.apply {
            search.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val closeIcon: Drawable? =
                        if (!s.isNullOrEmpty()) getDrawable(R.drawable.ic_close) else null
                    closeIcon?.setBounds(0, 0, closeIcon.intrinsicWidth, closeIcon.intrinsicHeight)

                    val searchIcon: Drawable? = getDrawable(R.drawable.ic_search)
                    searchIcon?.setBounds(
                        0,
                        0,
                        searchIcon.intrinsicWidth,
                        searchIcon.intrinsicHeight
                    )

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