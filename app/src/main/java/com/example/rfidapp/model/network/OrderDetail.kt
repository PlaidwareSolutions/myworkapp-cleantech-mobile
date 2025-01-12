package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderDetail(
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("createdBy") var createdBy: CreatedBy? = null,
    @SerializedName("customer") var customer: Customer? = null,
    @SerializedName("_id") var id: String = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("items") var items: List<Item> = listOf(),
    @SerializedName("requiredDate") var requiredDate: String? = null,
    @SerializedName("status") var status: String = "",
    @SerializedName("updatedAt") var updatedAt: String = "",
    @SerializedName("updatedBy") var updatedBy: UpdatedBy? = null,
    @SerializedName("__v") var v: Int = 0,
    @SerializedName("pdfKey") var pdfKey: String? = null,
){
    data class Item(
        @SerializedName("_id") var id: String? = null,
        @SerializedName("product") var product: Product? = null,
        @SerializedName("requiredQuantity") var requiredQuantity: Int? = null
    ) : Serializable

    fun getTotalCount(): Int {
        var totalCount = 0
        for (item in items) {
            totalCount += item.requiredQuantity ?: 0
        }
        return totalCount
    }
}