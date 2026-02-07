package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    @SerializedName("data") var orderList: ArrayList<Order> = arrayListOf(),
    @SerializedName("success") var success: Boolean = false
)