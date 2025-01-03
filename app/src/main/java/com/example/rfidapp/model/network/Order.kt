package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("createdBy") var createdBy: String? = "",
    @SerializedName("customer") var customer: Customer? = null,
    @SerializedName("_id") var id: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("items") var items: List<Item> = listOf(),
    @SerializedName("requiredDate") var requiredDate: String? = "",
    @SerializedName("status") var status: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("updatedBy") var updatedBy: String? = "",
    @SerializedName("__v") var v: Int? = 0
): Serializable