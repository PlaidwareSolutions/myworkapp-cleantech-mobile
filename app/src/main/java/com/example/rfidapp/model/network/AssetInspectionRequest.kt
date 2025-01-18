package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AssetInspectionRequest(
    @SerializedName("inspection") val inspection: List<Inspection>?= null,
    @SerializedName("userLocation") val userLocation: UserLocation?= null
)