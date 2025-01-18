package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemOrderBinding
import com.example.rfidapp.databinding.ItemShipmentCountBinding
import com.example.rfidapp.model.network.Bol
import com.example.rfidapp.model.network.Item
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.core.ShipmentUtil

class ShipmentDetailAdapter(
    val activity: Activity,
    private val orderList: List<Item>,
    private val foundQuantity:Int
) : RecyclerView.Adapter<ShipmentDetailAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemShipmentCountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(orderList[pos]){
                    itemName.text = product?.name ?: ""
                    srNo.text = pos.toString()
                    txtRequired.text = (requiredQuantity ?: 0).toString()
                    txtFound.text = foundQuantity.toString()

//                    ShipmentUtil.orderShipments.value.firstOrNull { it.orderId == orderId }?.let { orderShipmentData ->
//                        txtShipped.text = orderShipmentData.shippedQuantity.toString()
//                        txtBalance.text = orderShipmentData.getRemainingQuantity().toString()
//                    }

                    views.isVisible = position != orderList.size - 1
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemShipmentCountBinding.inflate(
            activity.layoutInflater,
            parent,
            false
        )
    )

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {

        }
    }
}