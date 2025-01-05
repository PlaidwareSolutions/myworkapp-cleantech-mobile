package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bol(
    @SerialName("carrier") var carrier: Carrier? = null,
    @SerialName("createdAt") var createdAt: String? = "",
    @SerialName("createdBy") var createdBy: String? = "",
    @SerialName("_id") var id: String? = "",
    @SerialName("items") var items: List<Item> = listOf(),
    @SerialName("order") var order: Order? = null,
    @SerialName("referenceId") var referenceId: String? = "",
    @SerialName("tags") var tags: List<String>? = listOf(),
    @SerialName("updatedAt") var updatedAt: String? = "",
    @SerialName("__v") var v: Int? = 0
)