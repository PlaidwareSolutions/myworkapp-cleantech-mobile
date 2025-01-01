package com.example.rfidapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.OrderRepository
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(private val orderRepository: OrderRepository) :
    ViewModel() {

    private val _orderDetail = MutableStateFlow(OrderDetail())
    val orderDetail: StateFlow<OrderDetail> = _orderDetail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchOrderDetail(orderId: String) {
        viewModelScope.launch {
            _loading.value = true
            SharedPrefs.accessToken?.let { token ->
                try {
                    _orderDetail.value =
                        orderRepository.getOrderDetail(orderId, token).data ?: OrderDetail()
                    _errorMessage.value = null
                    Log.e("TAG222", "fetchOrderDetail: "+orderDetail.value)
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                    _orderDetail.value = OrderDetail()
                    Log.e("TAG222", "fetchOrders: " + e.message)
                } finally {
                    _loading.value = false
                }
            }
        }
    }
}