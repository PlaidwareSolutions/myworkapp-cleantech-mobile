package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("_id") var id: String? = null,
    @SerialName("product") var product: String? = null,
    @SerialName("requiredQuantity") var requiredQuantity: Int? = null
)