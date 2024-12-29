package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssetListResponse(
    @SerialName("data") var assetList: ArrayList<Asset> = arrayListOf(),
    @SerialName("success") var success: Boolean = false
)