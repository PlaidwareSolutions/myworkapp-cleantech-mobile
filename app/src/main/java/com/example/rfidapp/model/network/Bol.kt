package com.example.rfidapp.model.network

import com.example.rfidapp.util.toFormattedDate
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Bol(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: String? = "",
    @SerializedName("_id") var id: String? = "",
    @SerializedName("items") var items: List<Item> = listOf(),
    @SerializedName("order") var order: Order? = null,
    @SerializedName("orderType") var orderType: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("tags") var tags: List<String> = listOf(),
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: String? = "",
    @SerializedName("__v") var v: Int? = 0
) {
    fun getCarrierName() = carrier?.name ?: "N/A"
    fun getCustomerName() = order?.customer?.name ?: "N/A"
    fun getCreatedDate() = createdAt?.toFormattedDate() ?: ""
}

@Serializable
data class BolX(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: String? = "",
    @SerializedName("_id") var id: String? = "",
    @SerializedName("items") var items: List<Item> = listOf(),
    @SerializedName("order") var order: Order? = null,
    @SerializedName("orderType") var orderType: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("tags") var tags: List<String> = listOf(),
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: String? = "",
    @SerializedName("__v") var v: Int? = 0
) {
    data class Item(
        @SerializedName("_id") var id: String? = null,
        @SerializedName("product") var product: String? = null,
        @SerializedName("quantity") var quantity: Int? = null
    ) : java.io.Serializable

    fun getCarrierName() = carrier?.name ?: "N/A"
    fun getCustomerName() = order?.customer?.name ?: "N/A"
    fun getCreatedDate() = createdAt?.toFormattedDate() ?: ""
}