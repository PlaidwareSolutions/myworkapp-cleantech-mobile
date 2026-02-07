package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemTagHistoryBinding
import com.example.rfidapp.model.OrderShipmentData
import com.example.rfidapp.model.network.HistoryAsset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TagHistoryAdapter(
    val activity: Activity,
    private val historyList: List<HistoryAsset>,
    private val onItemClick: (HistoryAsset) -> Unit,
    private val name:String
) : RecyclerView.Adapter<TagHistoryAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemTagHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(historyList[pos]){
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val outputFormat = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.US)
                    val date: Date? = createdAt?.let { inputFormat.parse(it) }
                    val formattedDate = date?.let { outputFormat.format(it) }

                    textViewTimeStamp.text = formattedDate
                    textViewDescription.text = comment
                    txtName.text = name
                    txtState.text = state
                    txtState.isVisible = state.isNullOrEmpty().not()
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