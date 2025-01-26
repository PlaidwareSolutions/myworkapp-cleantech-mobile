package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Inspection(
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("state") val state: String?= null,
    @SerializedName("tag") val tag: String?= null
)