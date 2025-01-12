package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemOrderBinding
import com.example.rfidapp.databinding.ItemOrderShipmentBinding
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.InputBol
import com.example.rfidapp.model.network.Item
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.model.network.OrderDetail

class ShipmentOrderAdapter(
    val activity: Activity,
    private val orderList: List<OrderShipmentData>
) : RecyclerView.Adapter<ShipmentOrderAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemOrderShipmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(orderList[pos]){
                    itemName.text = orderRefId
                    srNo.text = (pos + 1).toString()
                    txtTotal.text = totalQuantity.toString()
                    txtShipped.text = shippedQuantity.toString()
                    txtAdd.text = getRemainingQuantity().toString()

                    views.isVisible = position != orderList.size - 1
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemOrderShipmentBinding.inflate(
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