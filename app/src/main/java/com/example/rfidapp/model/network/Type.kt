package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Type(
    @SerializedName("createdAt") var createdAt: String = "",
    @SerializedName("_id") var _id: String = "",
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("permissions") var permissions: Permissions = Permissions(),
    @SerializedName("updatedAt") var updatedAt: String = "",
    @SerializedName("__v") var v: Int = 0
)