package com.example.rfidapp.model

data class OrderShipmentData(
    val orderRefId: String,
    val totalQuantity: Int,
    val shippedQuantity: Int,
    val tags: ArrayList<String>
) {
    fun getRemainingQuantity() = totalQuantity - shippedQuantity
}
