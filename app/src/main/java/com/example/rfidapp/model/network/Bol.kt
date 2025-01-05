package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bol(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: String? = "",
    @SerializedName("_id") var id: String? = "",
    @SerializedName("items") var items: List<Item> = listOf(),
    @SerializedName("order") var order: Order? = null,
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("tags") var tags: List<String>? = listOf(),
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("__v") var v: Int? = 0
)