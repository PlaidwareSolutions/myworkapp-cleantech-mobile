package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class InputBol(
    @SerializedName("order") var order: String? = null,
    @SerializedName("tags") var tags: List<String?>? = null
)