package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Item(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("product") var product: Product? = null,
    @SerializedName("requiredQuantity") var requiredQuantity: Int? = null
) : Serializable