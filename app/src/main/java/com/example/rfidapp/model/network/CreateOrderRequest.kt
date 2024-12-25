package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("carrier") var carrier: String? = null,
    @SerialName("customer") var customer: String? = null,
    @SerialName("items") var items: List<Item>? = null,
    @SerialName("requiredDate") var requiredDate: String? = null
)