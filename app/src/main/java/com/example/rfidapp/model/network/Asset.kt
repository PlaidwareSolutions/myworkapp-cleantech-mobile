package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("_id") var id: String? = null,
    @SerializedName("product") var product: String? = null,
    @SerializedName("tag") var tag: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null
)