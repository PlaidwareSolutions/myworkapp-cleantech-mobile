package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    @SerialName("createdAt") var createdAt: String = "",
    @SerialName("createdBy") var createdBy: String = "",
    @SerialName("_id") var _id: String = "",
    @SerialName("id") var id: String = "",
    @SerialName("name") var name: String = "",
    @SerialName("permissions") var permissions: Map<String, Boolean> = mapOf(),
    @SerialName("updatedAt") var updatedAt: String = "",
    @SerialName("__v") var v: Int = 0
)