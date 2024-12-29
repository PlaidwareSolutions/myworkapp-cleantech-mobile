package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerialName("brand") var brand: String? = null,
    @SerialName("createdAt") var createdAt: String? = null,
    @SerialName("epc") var epc: String? = null,
    @SerialName("_id") var _id: String? = null,
    @SerialName("id") var id: String? = null,
    @SerialName("model") var model: String? = null,
    @SerialName("provider") var provider: String? = null,
    @SerialName("serial") var serial: String? = null,
    @SerialName("type") var type: String? = null,
    @SerialName("upc") var upc: String? = null,
    @SerialName("updatedAt") var updatedAt: String? = null,
    @SerialName("__v") var v: Int? = null
)