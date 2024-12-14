package com.example.rfidapp.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ItemOrderBinding

class OrderAdapter(
    val activity: Activity,
    private val orderList: List<Int>,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    private var selectedOrderPos: Int? = null

    inner class MyViewHolder(val item: ItemOrderBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(pos: Int) {
            item.orderId.text = "$pos$pos$pos$pos$pos$pos"
            item.carrierName.text = "abababanjhgj"
            item.reqBy.text = "abababa"
            if (selectedOrderPos == pos) {
                item.root.background =
                    ContextCompat.getDrawable(activity, R.drawable.bg_border_selected)
                item.verticalDivider1.select()
                item.verticalDivider2.select()
                item.verticalDivider3.select()
                item.verticalDivider4.select()
                item.verticalDivider5.select()
                item.verticalDivider6.select()
                item.horizontalDivider1.select()
                item.horizontalDivider2.select()
            } else {
                item.root.background = ContextCompat.getDrawable(activity, R.drawable.bg_border)
                item.verticalDivider1.unselect()
                item.verticalDivider2.unselect()
                item.verticalDivider3.unselect()
                item.verticalDivider4.unselect()
                item.verticalDivider5.unselect()
                item.verticalDivider6.unselect()
                item.horizontalDivider1.unselect()
                item.horizontalDivider2.unselect()
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
}

fun View.select() {
    setBackgroundColor(ContextCompat.getColor(context, R.color.black))
}

fun View.unselect() {
    setBackgroundColor(ContextCompat.getColor(context, R.color.divider_color))
}