package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    @SerializedName("binWeight") var binWeight: Int? = null,
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: CreatedBy? = null,
    @SerializedName("customer") var customer: Customer? = null,
    @SerializedName("_id") var id: String? = "",
    @SerializedName("items") var items: List<Item>? = listOf(),
    @SerializedName("orderWeight") var orderWeight: Int? = null,
    @SerializedName("palletCount") var palletCount: Int? = null,
    @SerializedName("palletWeight") var palletWeight: Int? = null,
    @SerializedName("pdfKey") var pdfKey: String? = null,
    @SerializedName("poNumber") var poNumber: String? = "",
    @SerializedName("receiverAddress") var receiverAddress: ReceiverAddress? = null,
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("requiredDate") var requiredDate: String? = "",
    @SerializedName("shippedItems") var shippedItems: List<ShippedItem>? = listOf(),
    @SerializedName("status") var status: String? = "",
    @SerializedName("totalRequiredQuantity") var totalRequiredQuantity: Int? = 0,
    @SerializedName("totalShippedQuantity") var totalShippedQuantity: Int? = 0,
    @SerializedName("type") var type: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: UpdatedBy? = null,
    @SerializedName("__v") var v: Int? = 0
): Serializable