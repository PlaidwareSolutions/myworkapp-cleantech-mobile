package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ProductRepository
import com.example.rfidapp.model.network.ProductResponse
import com.example.rfidapp.model.network.UpdateProductResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    private val _productState = MutableStateFlow<ProductState>(ProductState.Idle)
    val productState: StateFlow<ProductState> = _productState

    private val _updateProductState = MutableStateFlow<ProductUpdateState>(ProductUpdateState.Idle)
    val updateProductState: StateFlow<ProductUpdateState> = _updateProductState

    fun createProduct(
        token: String,
        name: String,
        description: String,
        customAttributes: Map<String, String>,
        active: Boolean
    ) {
        viewModelScope.launch {
            _productState.value = ProductState.Loading
            try {
                val response = productRepository.createProduct(token, name, description, customAttributes, active)
                _productState.value = ProductState.Success(response)
            } catch (e: Exception) {
                _productState.value = ProductState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateProduct(
        token: String,
        productId: String,
        name: String,
        description: String,
        customAttributes: Map<String, String>
    ) {
        viewModelScope.launch {
            _updateProductState.value = ProductUpdateState.Loading
            try {
                val response = productRepository.updateProduct(token, productId, name, description, customAttributes)
                _updateProductState.value = ProductUpdateState.Success(response)
            } catch (e: Exception) {
                _updateProductState.value = ProductUpdateState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class ProductState {
    data object Idle : ProductState()
    data object Loading : ProductState()
    data class Success(val response: ProductResponse) : ProductState()
    data class Error(val message: String) : ProductState()
}

sealed class ProductUpdateState {
    data object Idle : ProductUpdateState()
    data object Loading : ProductUpdateState()
    data class Success(val response: UpdateProductResponse) : ProductUpdateState()
    data class Error(val message: String) : ProductUpdateState()
}