package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("carrier") var carrier: String? = "",
    @SerialName("createdAt") var createdAt: String? = "",
    @SerialName("createdBy") var createdBy: String? = "",
    @SerialName("customer") var customer: String? = "",
    @SerialName("_id") var id: String? = "",
    @SerialName("items") var items: List<Item> = listOf(),
    @SerialName("requiredDate") var requiredDate: String? = "",
    @SerialName("status") var status: String? = "",
    @SerialName("updatedAt") var updatedAt: String? = "",
    @SerialName("updatedBy") var updatedBy: String? = "",
    @SerialName("__v") var v: Int? = 0
)