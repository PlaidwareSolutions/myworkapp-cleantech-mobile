package com.example.rfidapp.model.network.orderdetail

import com.example.rfidapp.model.network.Bol
import com.example.rfidapp.model.network.Carrier
import com.example.rfidapp.model.network.CreatedBy
import com.example.rfidapp.model.network.Customer
import com.example.rfidapp.model.network.Driver
import com.example.rfidapp.model.network.Item
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
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("requiredDate") var requiredDate: String? = "",
    @SerializedName("shippedItems") var shippedItems: List<ShippedItem>? = listOf(),
    @SerializedName("shippingDetails") var shippingDetails: List<ShippingDetail>? = listOf(),
    @SerializedName("status") var status: String? = "",
    @SerializedName("type") var type: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: UpdatedBy? = UpdatedBy(),
    @SerializedName("__v") var v: Int? = 0
){
    @Serializable
    data class ShippedItem(
        @SerializedName("bol_ids") var bolIds: List<String?>? = null,
        @SerializedName("product_id") var productId: String? = null,
        @SerializedName("shippedQuantity") var shippedQuantity: Int? = null
    )

    @Serializable
    data class ShippingDetail(
        @SerializedName("bols") var bols: List<Bol>? = listOf(),
        @SerializedName("carrier") var carrier: Carrier? = Carrier(),
        @SerializedName("createdAt") var createdAt: String? = "",
        @SerializedName("createdBy") var createdBy: CreatedBy? = null,
        @SerializedName("driver") var driver: Driver? = Driver(),
        @SerializedName("_id") var id: String? = "",
        @SerializedName("orderType") var orderType: String? = "",
        @SerializedName("referenceId") var referenceId: String? = "",
        @SerializedName("shipmentDate") var shipmentDate: String? = "",
        @SerializedName("updatedAt") var updatedAt: String? = "",
        @SerializedName("__v") var v: Int? = 0
    )

    fun getTotalCount(): Int {
        var totalCount = 0
        items?.forEach { item ->
            totalCount += item.requiredQuantity ?: 0
        }
        return totalCount
    }
}