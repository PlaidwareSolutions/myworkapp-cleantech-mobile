package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("limit") var limit: Int = 0,
    @SerializedName("page") var page: Int = 0,
    @SerializedName("skip") var skip: Int = 0
)