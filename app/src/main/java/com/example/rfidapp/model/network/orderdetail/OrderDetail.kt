package com.example.rfidapp.model.network.orderdetail

import com.example.rfidapp.model.network.Carrier
import com.example.rfidapp.model.network.CreatedBy
import com.example.rfidapp.model.network.Customer
import com.example.rfidapp.model.network.Item
import com.example.rfidapp.model.network.ShippedItem
import com.example.rfidapp.model.network.ShippingDetail
import com.example.rfidapp.model.network.UpdatedBy
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetail(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: CreatedBy? = null,
    @SerializedName("customer") var customer: Customer? = null,
    @SerializedName("_id") var id: String? = "",
    @SerializedName("items") var items: List<Item>? = listOf(),
    @SerializedName("poNumber") var poNumber: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("requiredDate") var requiredDate: String? = "",
    @SerializedName("shippedItems") var shippedItems: List<ShippedItem>? = listOf(),
    @SerializedName("shippingDetails") var shippingDetails: List<ShippingDetail>? = listOf(),
    @SerializedName("status") var status: String? = "",
    @SerializedName("totalProcessorPingQuantity") var totalProcessorPingQuantity: Int? = 0,
    @SerializedName("totalRequiredQuantity") var totalRequiredQuantity: Int? = 0,
    @SerializedName("totalReturnedQuantity") var totalReturnedQuantity: Int? = 0,
    @SerializedName("totalShippedQuantity") var totalShippedQuantity: Int? = 0,
    @SerializedName("type") var type: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: UpdatedBy? = null,
    @SerializedName("__v") var v: Int? = 0
){

    fun getTotalCount(): Int {
        var totalCount = 0
        items?.forEach { item ->
            totalCount += item.requiredQuantity ?: 0
        }
        return totalCount
    }
}