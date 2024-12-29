package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAssetRequest(
    @SerialName("product") var product: String? = null,
    @SerialName("tag") var tag: String? = null
)