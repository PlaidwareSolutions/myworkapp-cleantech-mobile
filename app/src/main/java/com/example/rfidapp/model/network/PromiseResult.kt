package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PromiseResult(
    @SerializedName("reason") val reason: Reason?= null,
    @SerializedName("status") val status: String?
)