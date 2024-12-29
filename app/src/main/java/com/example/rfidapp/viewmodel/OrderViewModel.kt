package com.example.rfidapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.OrderRepository
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.util.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orderList = MutableStateFlow<List<Order>>(emptyList())
    val orderList: StateFlow<List<Order>> = _orderList

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            _loading.value = true
            SharedPrefs.accessToken?.let { token->
                try {
                    _orderList.value = orderRepository.getOrders(token).data ?: emptyList()
                    _errorMessage.value = null
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                    _orderList.value = emptyList()
                    Log.e("TAG222", "fetchOrders: "+e.message)
                } finally {
                    _loading.value = false
                }
            }
        }
    }
}