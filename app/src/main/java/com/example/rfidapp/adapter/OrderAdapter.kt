package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ItemSearchBinding
import com.example.rfidapp.model.network.Order

class OrderAdapter(
    val activity: Activity,
    private val orderList: ArrayList<Order>,
    val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    private var selectedOrderPos: Int? = null

    inner class MyViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(orderList[pos]){
                    orderId.text = id
                    carrierName.text = carrier?.name ?: ""
                    reqBy.text = createdBy
                    if (selectedOrderPos == pos) {
                        lnrItem.backgroundTintList = ColorStateList.valueOf(
                            activity.getColor(R.color.colorPrimary)
                        )
                    } else {
                        lnrItem.backgroundTintList = ColorStateList.valueOf(
                            activity.getColor(R.color.bg_stroke_color)
                        )
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemSearchBinding.inflate(
            activity.layoutInflater,
            parent,
            false
        )
    )

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            onItemClick(orderList[position])
            val temp = selectedOrderPos
            selectedOrderPos = null
            temp?.let { it1 -> notifyItemChanged(it1) }
            selectedOrderPos = position
            notifyItemChanged(position)
        }
    }

    fun updateData(orderList: List<Order>) {
        this.orderList.clear()
        this.orderList.addAll(orderList)
        notifyDataSetChanged()
    }
}