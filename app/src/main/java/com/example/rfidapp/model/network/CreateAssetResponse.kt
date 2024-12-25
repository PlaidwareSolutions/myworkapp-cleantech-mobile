package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAssetResponse(
    @SerialName("data") var asset: Asset? = null,
    @SerialName("success") var success: Boolean? = null
)