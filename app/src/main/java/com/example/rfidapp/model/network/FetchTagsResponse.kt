package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchTagsResponse(
    @SerialName("data") var tagList: ArrayList<Tag> = arrayListOf(),
    @SerialName("success") var success: Boolean = false
)