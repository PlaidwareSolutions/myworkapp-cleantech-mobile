package com.example.rfidapp.util

sealed class ScreenState<out T> {
    data object Idle : ScreenState<Nothing>()
    data object Loading : ScreenState<Nothing>()
    data class Success<out T>(val response: T) : ScreenState<T>()
    data class Error(val message: String) : ScreenState<Nothing>()
}