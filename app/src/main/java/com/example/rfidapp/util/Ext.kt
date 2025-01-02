package com.example.rfidapp.util

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.toFormattedDate(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(this)
    val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return outputFormat.format(date ?: Date())
}

fun Exception.getErrorMessage(): String {
    return when (this) {
        is HttpException -> {
            // Handle HTTP-related errors (e.g., 404, 500, etc.)
            val errorCode = code()
            val errorMessage = when (errorCode) {
                400 -> "Bad request. Please check your credentials."
                401 -> "Invalid Username or Password"
                403 -> "Forbidden. You do not have permission."
                404 -> "Not found. The requested resource does not exist."
                500 -> "Server error. Please try again later."
                else -> "Unknown server error. Code: $errorCode"
            }
            errorMessage
        }
        is SocketTimeoutException -> {
            // Handle network timeout errors
            "Network timeout. Please try again."
        }
        is UnknownHostException -> {
            // Handle no internet connection errors
            "No internet connection. Please check your connection."
        }
        is JsonParseException -> {
            // Handle JSON parsing errors
            "Failed to process data. Please try again."
        }
        else -> {
            // Generic catch for any other type of exception
            message ?: "Unknown error"
        }
    }
}