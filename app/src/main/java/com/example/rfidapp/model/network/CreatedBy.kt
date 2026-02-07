package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreatedBy(
    @SerializedName("email") var email: List<String> = listOf(),
    @SerializedName("_id") var id: String = "",
    @SerializedName("name") var name: String = ""
)