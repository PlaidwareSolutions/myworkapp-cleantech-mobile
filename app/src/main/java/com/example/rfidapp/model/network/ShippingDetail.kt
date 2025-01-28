package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName

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