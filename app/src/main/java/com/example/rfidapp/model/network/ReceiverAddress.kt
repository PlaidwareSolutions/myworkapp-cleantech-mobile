package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiverAddress(
    @SerializedName("address") var address: Address? = null,
    @SerializedName("name") var name: String? = null
)