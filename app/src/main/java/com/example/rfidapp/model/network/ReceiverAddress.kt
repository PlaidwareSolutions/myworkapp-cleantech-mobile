package com.example.rfidapp.model.network


import com.google.gson.annotations.SerializedName

data class ReceiverAddress(
    @SerializedName("address") var address: Address? = null,
    @SerializedName("name") var name: String? = null
)