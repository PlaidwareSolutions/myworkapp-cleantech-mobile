package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveShipmentRequest(
    @SerializedName("assets") var assets: List<String>? = listOf()
)