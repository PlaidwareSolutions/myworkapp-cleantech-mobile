package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FetchTagsResponse(
    @SerializedName("data") var tagList: ArrayList<Tag> = arrayListOf(),
    @SerializedName("success") var success: Boolean = false
)