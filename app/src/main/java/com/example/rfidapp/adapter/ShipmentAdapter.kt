package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.R
import com.example.rfidapp.databinding.ItemShipmentBinding
import com.example.rfidapp.model.network.Shipment
import com.example.rfidapp.util.toFormattedDate

class ShipmentAdapter(
    val activity: Activity,
    private val orderList: ArrayList<Shipment>,
    val onItemClick: (Shipment) -> Unit
) : RecyclerView.Adapter<ShipmentAdapter.MyViewHolder>() {

    private var selectedOrderPos: Int? = null

    inner class MyViewHolder(val binding: ItemShipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(pos: Int) {
            binding.apply {
                with(orderList[pos]) {
                    shippedBy.text = referenceId
                    orderID.text = ""
                    orderStatus.text = orderType
                    customerName.text = ""
                    txtTrlNumber.text = driver?.dl ?: ""
                    carrierName.text = carrier?.name ?: ""
                    driverName.text = driver?.name ?: ""
                    shippedDate.text = shipmentDate?.toFormattedDate()
                    this@apply.createdBy.text = createdBy?.name ?: ""
                    this@apply.txtRequiredQuantity.text =
                        (bols?.firstOrNull()?.items?.firstOrNull()?.quantity
                            ?: 0).toString()
                    txtStatus.text = shipmentStatus
//                    val totalRequiredQuantity = items.sumOf { it.requiredQuantity ?: 0 }
//                    txtRequiredQuantity.text = "" + totalRequiredQuantity

                    if (shipmentStatus.equals("INITIATED", ignoreCase = true)) {
                        binding.txtStatus.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    txtStatus.context,
                                    R.color.color_bg_status_initiated
                                )
                            )
                        binding.txtStatus.setTextColor(
                            ContextCompat.getColor(
                                txtStatus.context,
                                R.color.color_text_status_initiated
                            )
                        )
                    } else {
                        binding.txtStatus.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    txtStatus.context,
                                    R.color.bg_stroke_color
                                )
                            )
                        binding.txtStatus.setTextColor(
                            ContextCompat.getColor(
                                txtStatus.context,
                                R.color.black
                            )
                        )
                    }


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
        ItemShipmentBinding.inflate(
            activity.layoutInflater,
            parent,
            false
        )
    )

    override fun getItemCount() = orderList.size

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
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

    fun updateData(orderList: List<Shipment>) {
        this.orderList.clear()
        this.orderList.addAll(orderList)
        notifyDataSetChanged()
    }
}