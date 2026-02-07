package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Carrier(
    @SerializedName("email") var email: List<String?>? = null,
    @SerializedName("_id") var id: String? = null,
    @SerializedName("name") var name: String? = null
)