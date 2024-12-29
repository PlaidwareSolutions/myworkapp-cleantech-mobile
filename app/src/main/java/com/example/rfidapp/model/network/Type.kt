package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Type(
    @SerialName("createdAt") var createdAt: String = "",
    @SerialName("_id") var _id: String = "",
    @SerialName("id") var id: String = "",
    @SerialName("name") var name: String = "",
    @SerialName("permissions") var permissions: Permissions = Permissions(),
    @SerialName("updatedAt") var updatedAt: String = "",
    @SerialName("__v") var v: Int = 0
)