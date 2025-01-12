package com.example.rfidapp.model

data class OrderShipmentData(
    var orderId: String,
    var orderRefId: String,
    var totalQuantity: Int,
    var shippedQuantity: Int,
    var tags: ArrayList<String>
) {
    fun getRemainingQuantity() = totalQuantity - shippedQuantity
}
