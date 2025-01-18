package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AssetInspectionResponse(
    @SerializedName("errorCount") val errorCount: Int?= null,
    @SerializedName("promiseResults") val promiseResults: List<PromiseResult>? = null,
    @SerializedName("successCount") val successCount: Int?= null
)