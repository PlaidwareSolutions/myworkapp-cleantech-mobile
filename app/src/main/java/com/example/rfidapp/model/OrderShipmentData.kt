package com.example.rfidapp.model

import com.example.rfidapp.model.network.orderdetail.OrderDetail

data class OrderShipmentData(
    var orderId: String,
    var orderRefId: String,
    var totalQuantity: Int,
    var shippedQuantity: Int,
    var tags: ArrayList<String>,
    var orderDetail: OrderDetail? = null,
) {
    fun getRemainingQuantity() = totalQuantity - shippedQuantity
}
