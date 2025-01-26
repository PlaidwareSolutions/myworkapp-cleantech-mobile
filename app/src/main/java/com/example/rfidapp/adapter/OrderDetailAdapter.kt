package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemOrderBinding
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.core.ShipmentUtil

class OrderDetailAdapter(
    val orderId: String,
    val orderType:String,
    val activity: Activity,
    private val orderList: List<OrderDetail.Item>,
    private val onItemClick:(OrderDetail.Item)->Unit
) : RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(orderList[pos]) {
                    itemName.text = product?.name ?: ""
                    srNo.text = pos.toString()
                    txtQty.text = (requiredQuantity ?: 0).toString()
                    ShipmentUtil.orderShipments.value.firstOrNull { it.orderId == orderId }
                        ?.let { orderShipmentData ->
                            txtShipped.text = orderShipmentData.shippedQuantity.toString()
                            txtBalance.text = orderShipmentData.getRemainingQuantity().toString()
                        }

                    views.isVisible = position != orderList.size - 1
                }
                root.setOnClickListener {
                    if(orderType == "INBOUND"){
                        onItemClick.invoke(orderList[pos])
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemOrderBinding.inflate(
            activity.layoutInflater,
            parent,
            false
        )
    )

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(position)
    }
}