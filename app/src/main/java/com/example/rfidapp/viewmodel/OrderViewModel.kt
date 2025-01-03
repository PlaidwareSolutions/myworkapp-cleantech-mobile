package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ContactRepository
import com.example.rfidapp.data.repository.OrderRepository
import com.example.rfidapp.model.network.Contact
import com.example.rfidapp.model.network.Order
import com.example.rfidapp.util.ScreenState
import com.example.rfidapp.util.SharedPrefs
import com.example.rfidapp.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val contactRepository: ContactRepository
) :
    ViewModel() {

    private val _orderList = MutableStateFlow<ScreenState<List<Order>>>(ScreenState.Idle)
    val orderList: StateFlow<ScreenState<List<Order>>> = _orderList.asStateFlow()

    private val _contacts = MutableStateFlow<ArrayList<Contact>>(arrayListOf())
    val contacts: StateFlow<ArrayList<Contact>> = _contacts.asStateFlow()


    init {
        fetchOrders()
    }

    fun fetchContacts() {
        viewModelScope.launch {
            _contacts.value = arrayListOf()
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = contactRepository.fetchContacts(token)
                    if (response.isSuccess()) {
                        _contacts.value = response.data ?: arrayListOf()
                    } else {
                        _contacts.value = arrayListOf()
                    }

                } catch (e: Exception) {
                    _contacts.value = arrayListOf()
                }
            }
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _orderList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = orderRepository.getOrders(token = token)
                    if (response.isSuccess()) {
                        _orderList.value = ScreenState.Success(response.data ?: emptyList())
                    } else {
                        _orderList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }

                } catch (e: Exception) {
                    _orderList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun performSearchByCarrier(id: String) {
        viewModelScope.launch {
            _orderList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = orderRepository.getOrders(token = token, carrierId = id)
                    if (response.isSuccess()) {
                        _orderList.value = ScreenState.Success(response.data ?: emptyList())
                    } else {
                        _orderList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }

                } catch (e: Exception) {
                    _orderList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun performSearchByCustomer(id: String) {
        viewModelScope.launch {
            _orderList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = orderRepository.getOrders(token = token, customerId = id)
                    if (response.isSuccess()) {
                        _orderList.value = ScreenState.Success(response.data ?: emptyList())
                    } else {
                        _orderList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }

                } catch (e: Exception) {
                    _orderList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun performSearchByReferenceId(refId: String) {
        viewModelScope.launch {
            _orderList.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = orderRepository.getOrders(token = token, referenceId = refId)
                    if (response.isSuccess()) {
                        _orderList.value = ScreenState.Success(response.data ?: emptyList())
                    } else {
                        _orderList.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }

                } catch (e: Exception) {
                    _orderList.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }
}