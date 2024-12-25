package com.example.rfidapp.model.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTagResponse(
    @SerialName("data") var tag: Tag? = null,
    @SerialName("success") var success: Boolean? = null
)