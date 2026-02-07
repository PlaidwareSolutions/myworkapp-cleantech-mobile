package com.example.rfidapp.model.network

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerializedName("data") val data: T? = null, // Generic data that could be any type (success case)
    @SerializedName("status") val status: String? = null, // For error cases
    @SerializedName("statusCode") val statusCode: Int? = null, // For error cases
    @SerializedName("message") val message: String? = null, // For error cases
    @SerializedName("success") val success: Boolean? = null, // For success case
    @SerializedName("pagination") val pagination: Pagination? = null
) {
    // To check if the response is successful
    fun isSuccess(): Boolean = success == true || statusCode == 200 // Assuming 200 as a success status code
}