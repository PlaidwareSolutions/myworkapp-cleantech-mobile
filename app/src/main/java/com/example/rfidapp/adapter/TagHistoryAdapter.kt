package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemTagHistoryBinding
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.HistoryAsset

class TagHistoryAdapter(
    val activity: Activity,
    private val historyList: List<HistoryAsset>,
    private val onItemClick: (HistoryAsset) -> Unit
) : RecyclerView.Adapter<TagHistoryAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemTagHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(historyList[pos]){

                }

                root.setOnClickListener {
                    onItemClick.invoke(historyList[pos])
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemTagHistoryBinding.inflate(
            activity.layoutInflater,
            parent,
            false
        )
    )

    override fun getItemCount() = historyList.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(position)
    }
}