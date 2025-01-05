package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    @SerializedName("dl") var dl: String? = null,
    @SerializedName("name") var name: String? = null
)