package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAssetRequest(
    @SerializedName("product") var product: String? = null,
    @SerializedName("tag") var tag: String? = null
)