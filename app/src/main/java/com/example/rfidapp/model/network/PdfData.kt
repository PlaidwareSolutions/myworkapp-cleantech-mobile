package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName

data class PdfData(
    @SerializedName("url") val url: String? = null,
    @SerializedName("fileName") val fileName: String? = null
)