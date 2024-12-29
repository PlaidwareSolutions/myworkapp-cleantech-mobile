package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LocationX(
    @SerializedName("coordinates") var coordinates: List<Double> = listOf(),
    @SerializedName("type") var type: String = ""
)