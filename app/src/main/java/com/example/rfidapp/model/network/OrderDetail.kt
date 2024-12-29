package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetail(
    @SerializedName("carrier") var carrier: Any? = Any(),
    @SerializedName("createdAt") var createdAt: String = "",
    @SerializedName("createdBy") var createdBy: CreatedBy = CreatedBy(),
    @SerializedName("customer") var customer: Any? = Any(),
    @SerializedName("_id") var id: String = "",
    @SerializedName("items") var items: List<Item> = listOf(),
    @SerializedName("requiredDate") var requiredDate: String = "",
    @SerializedName("status") var status: String = "",
    @SerializedName("updatedAt") var updatedAt: String = "",
    @SerializedName("updatedBy") var updatedBy: String = "",
    @SerializedName("__v") var v: Int = 0
){
    @Serializable
    data class CreatedBy(
        @SerializedName("email") var email: List<String> = listOf(),
        @SerializedName("_id") var id: String = "",
        @SerializedName("name") var name: String = ""
    )
}