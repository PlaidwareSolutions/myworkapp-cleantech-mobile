package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    @SerializedName("items") var items: List<Item>? = null,
    @SerializedName("requiredDate") var requiredDate: String? = null
)