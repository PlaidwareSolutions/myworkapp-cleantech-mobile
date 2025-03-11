package com.example.rfidapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.toFormattedDate(): String {
    if (this.isEmpty()) return ""
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(this)
    val outputFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
    return outputFormat.format(date ?: Date())
}

inline fun <reified T> Gson.fromJson(json: String): T {
    return this.fromJson(json, object : TypeToken<T>() {}.type)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Exception.getErrorMessage(): String {
    return when (this) {
        is HttpException -> {
            // Handle HTTP-related errors (e.g., 404, 500, etc.)
            val errorMessage = when (val errorCode = code()) {
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

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Activity.openPdf(pdfUrl: String) {
    try {
        Log.e("TAG243", "openPdfqqq: " )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(pdfUrl), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    } catch (e: Exception) {
        Log.e("TAG243", "openPdf: "+e.message )
        // Fallback: Open in Chrome or default browser
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(browserIntent)
    }
}