package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoleRequest(
    @SerialName("permissions") var permissions: Map<String,Boolean> = mapOf()
)