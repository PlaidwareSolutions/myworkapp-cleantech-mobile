package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RoleListResponse(
    @SerializedName("data") var roleList: ArrayList<Role> = arrayListOf(),
    @SerializedName("success") var success: Boolean = false
)