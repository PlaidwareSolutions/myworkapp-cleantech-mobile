package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName

data class HistoryAsset(
    @SerializedName("__v")
    val __v: Int? = null,
    @SerializedName("_id")
    val _id: String? = null,
    @SerializedName("asset")
    val asset: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("createdBy")
    val createdBy: String? = null,
    @SerializedName("process")
    val process: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)