package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    @SerializedName("data") var `data`: List<Data> = listOf(),
    @SerializedName("success") var success: Boolean = false
) {
    data class Data(
        @SerializedName("active") var active: Boolean = false,
        @SerializedName("createdAt") var createdAt: String? = null,
        @SerializedName("createdBy") var createdBy: String = "",
        @SerializedName("customAttributes") var customAttributes: Map<String, String>,
        @SerializedName("description") var description: String = "",
        @SerializedName("_id") var id: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("updatedAt") var updatedAt: String = "",
        @SerializedName("__v") var v: Int = 0
    )
}