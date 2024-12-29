package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    @SerialName("createdAt") var createdAt: String? = null,
    @SerialName("_id") var id: String? = null,
    @SerialName("product") var product: String? = null,
    @SerialName("tag") var tag: String? = null,
    @SerialName("updatedAt") var updatedAt: String? = null,
    @SerialName("__v") var v: Int? = null
)