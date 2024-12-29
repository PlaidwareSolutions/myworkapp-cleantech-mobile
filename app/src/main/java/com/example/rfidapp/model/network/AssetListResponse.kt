package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AssetListResponse(
    @SerializedName("data") var assetList: ArrayList<Asset> = arrayListOf(),
    @SerializedName("success") var success: Boolean = false
)