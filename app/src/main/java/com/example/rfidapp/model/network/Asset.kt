package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("_id") var id: String? = null,
    @SerializedName("product") var product: Product? = null,
    @SerializedName("tag") var tag: String? = null,
    @SerializedName("active") var active: Boolean? = null,
    @SerializedName("lastState") var lastState: String? = null,
    @SerializedName("createdBy") var createdBy: CreatedBy? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("__v") var v: Int? = null,
    @SerializedName("history") var history: List<HistoryAsset>? = null
)