package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Shipment(
    @SerialName("bols") var bols: List<String>? = listOf(),
    @SerialName("carrier") var carrier: Carrier? = null,
    @SerialName("createdAt") var createdAt: String? = "",
    @SerialName("createdBy") var createdBy: CreatedBy? = CreatedBy(),
    @SerialName("driver") var driver: Driver? = null,
    @SerialName("_id") var id: String? = "",
    @SerialName("referenceId") var referenceId: String? = "",
    @SerialName("shipmentDate") var shipmentDate: String? = "",
    @SerialName("updatedAt") var updatedAt: String? = "",
    @SerialName("updatedBy") var updatedBy: UpdatedBy? = UpdatedBy(),
    @SerialName("__v") var v: Int? = 0
)