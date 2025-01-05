package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateShipmentResponse(
    @SerializedName("bols") var bols: List<String>? = listOf(),
    @SerializedName("carrier") var carrier: String? = "",
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("createdBy") var createdBy: String? = "",
    @SerializedName("driver") var driver: Driver? = null,
    @SerializedName("_id") var id: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("shipmentDate") var shipmentDate: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("__v") var v: Int? = 0
)