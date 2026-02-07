package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName

data class ShippedItem(
    @SerializedName("bol_ids") var bolIds: List<String?>? = null,
    @SerializedName("product_id") var productId: String? = null,
    @SerializedName("shippedQuantity") var shippedQuantity: Int? = null
)