package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("createdBy") var createdBy: String = "",
    @SerializedName("_id") var _id: String = "",
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("permissions") var permissions: Map<String, Boolean> = mapOf(),
    @SerializedName("updatedAt") var updatedAt: String = "",
    @SerializedName("__v") var v: Int = 0
)