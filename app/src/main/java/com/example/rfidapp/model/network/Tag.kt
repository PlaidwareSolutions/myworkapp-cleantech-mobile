package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("epc") var epc: String? = null,
    @SerializedName("_id") var _id: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("model") var model: String? = null,
    @SerializedName("provider") var provider: String? = null,
    @SerializedName("serial") var serial: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("upc") var upc: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null
)