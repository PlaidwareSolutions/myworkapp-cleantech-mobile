package com.example.rfidapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidapp.databinding.ItemBolBinding
import com.example.rfidapp.model.network.Bol
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BolAdapter(
    val activity: Activity,
    val bolList: List<Bol>,
    private val onItemClick: (Bol) -> Unit,
) : RecyclerView.Adapter<BolAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ItemBolBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.apply {
                with(bolList[pos]){
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val outputFormat = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.US)
                    val date: Date? = createdAt?.let { inputFormat.parse(it) }
                    val formattedDate = date?.let { outputFormat.format(it) }

                    textViewTimeStamp.text = formattedDate
                    textViewDescription.text = "Lorem ipsum dolor sit amet"
                    txtName.text = ""
                    txtState.text = ""
                }

                root.setOnClickListener {
                    onItemClick.invoke(bolList[pos])
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemBolBinding.inflate(
            activity.layoutInflater,
            parent,
            false
        )
    )

    override fun getItemCount() = bolList.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(position)
    }
}