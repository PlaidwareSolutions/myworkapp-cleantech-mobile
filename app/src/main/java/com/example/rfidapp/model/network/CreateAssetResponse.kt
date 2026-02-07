package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAssetResponse(
    @SerializedName("data") var asset: Asset? = null,
    @SerializedName("success") var success: Boolean? = null
)