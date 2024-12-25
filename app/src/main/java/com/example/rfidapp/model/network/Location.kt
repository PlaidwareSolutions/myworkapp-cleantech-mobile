package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("coordinates") val coordinates: List<Double>
)