package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationX(
    @SerialName("coordinates") var coordinates: List<Double> = listOf(),
    @SerialName("type") var type: String = ""
)