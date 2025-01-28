package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: String? = "",
    @SerializedName("customer") var customer: Customer? = null,
    @SerializedName("_id") var id: String? = "",
    @SerializedName("items") var items: List<Item>? = listOf(),
    @SerializedName("poNumber") var poNumber: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("requiredDate") var requiredDate: String? = "",
    @SerializedName("shippedItems") var shippedItems: List<ShippedItem>? = listOf(),
    @SerializedName("status") var status: String? = "",
    @SerializedName("totalRequiredQuantity") var totalRequiredQuantity: Int? = 0,
    @SerializedName("totalShippedQuantity") var totalShippedQuantity: Int? = 0,
    @SerializedName("type") var type: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: String? = "",
    @SerializedName("__v") var v: Int? = 0
): Serializable