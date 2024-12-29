package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PermissionListResponse(
    @SerializedName("data") var permissionList: ArrayList<String> = arrayListOf(),
    @SerializedName("success") var success: Boolean = false
)