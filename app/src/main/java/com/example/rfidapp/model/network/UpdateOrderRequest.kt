package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    @SerialName("items") var items: List<Item>? = null,
    @SerialName("requiredDate") var requiredDate: String? = null
)