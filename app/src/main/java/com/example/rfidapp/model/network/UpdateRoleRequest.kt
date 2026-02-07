package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoleRequest(
    @SerializedName("permissions") var permissions: Map<String,Boolean> = mapOf()
)