package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.ContactRepository
import com.example.rfidapp.model.network.CreateContactRequest
import com.example.rfidapp.model.network.CreateContactResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val contactRepository: ContactRepository) : ViewModel() {

    private val _contactState = MutableStateFlow<ContactState>(ContactState.Idle)
    val contactState: StateFlow<ContactState> = _contactState

    fun createContact(token: String, contactRequest: CreateContactRequest) {
        viewModelScope.launch {
            _contactState.value = ContactState.Loading
            try {
                val response = contactRepository.createContact(token, contactRequest)
                _contactState.value = ContactState.Success(response)
            } catch (e: Exception) {
                _contactState.value = ContactState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class ContactState {
    data object Idle : ContactState()
    data object Loading : ContactState()
    data class Success(val response: CreateContactResponse) : ContactState()
    data class Error(val message: String) : ContactState()
}