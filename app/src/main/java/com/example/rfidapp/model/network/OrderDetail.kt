package com.example.rfidapp.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetail(
    @SerialName("carrier") var carrier: Any? = Any(),
    @SerialName("createdAt") var createdAt: String = "",
    @SerialName("createdBy") var createdBy: CreatedBy = CreatedBy(),
    @SerialName("customer") var customer: Any? = Any(),
    @SerialName("_id") var id: String = "",
    @SerialName("items") var items: List<Item> = listOf(),
    @SerialName("requiredDate") var requiredDate: String = "",
    @SerialName("status") var status: String = "",
    @SerialName("updatedAt") var updatedAt: String = "",
    @SerialName("updatedBy") var updatedBy: String = "",
    @SerialName("__v") var v: Int = 0
){
    @Serializable
    data class CreatedBy(
        @SerialName("email") var email: List<String> = listOf(),
        @SerialName("_id") var id: String = "",
        @SerialName("name") var name: String = ""
    )
}