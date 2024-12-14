package com.example.rfidapp.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemOrderBinding

class OrderAdapter(
    val activity: Activity,
    private val orderList: List<Int>,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {
    inner class MyViewHolder(val item: ItemOrderBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(pos: Int) {
            item.orderId.text = "$pos$pos$pos$pos$pos$pos"
            item.carrierName.text = "abababanjhgj"
            item.reqBy.text = "abababa"
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(orderList[position])
        holder.itemView.setOnClickListener {
            onItemClick(orderList[position])
        }
    }
}