package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("street") val street: String,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("zipCode") val zipCode: String,
    @SerialName("country") val country: String,
    @SerialName("location") val location: Location
)