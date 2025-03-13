package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Shipment(
    @SerializedName("bols") var bols: List<Bol>? = listOf(),
    @SerializedName("carrier") var carrier: Carrier? = null,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: CreatedBy? = CreatedBy(),
    @SerializedName("driver") var driver: Driver? = null,
    @SerializedName("_id") var id: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("shipmentDate") var shipmentDate: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("updatedBy") var updatedBy: UpdatedBy? = UpdatedBy(),
    @SerializedName("orderType") var orderType: String = "",
    @SerializedName("__v") var v: Int? = 0
){
    fun isInbound(): Boolean = orderType == "INBOUND"
}