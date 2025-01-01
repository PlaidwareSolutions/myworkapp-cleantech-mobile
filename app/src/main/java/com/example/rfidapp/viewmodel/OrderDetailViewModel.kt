package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.OrderRepository
import com.example.rfidapp.model.network.OrderDetail
import com.example.rfidapp.util.ScreenState
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

    private val _orderDetail: MutableStateFlow<ScreenState<OrderDetail?>> =
        MutableStateFlow(ScreenState.Idle)
    val orderDetail: StateFlow<ScreenState<OrderDetail?>> = _orderDetail.asStateFlow()

    fun fetchOrderDetail(orderId: String) {
        viewModelScope.launch {
            _orderDetail.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    _orderDetail.value =
                        ScreenState.Success(orderRepository.getOrderDetail(orderId, token).data)
                } catch (e: Exception) {
                    _orderDetail.value = ScreenState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }
}