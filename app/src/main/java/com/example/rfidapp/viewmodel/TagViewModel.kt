package com.example.rfidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rfidapp.data.repository.TagRepository
import com.example.rfidapp.model.network.CreateTagRequest
import com.example.rfidapp.model.network.CreateTagResponse
import com.example.rfidapp.model.network.UpdateTagRequest
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
class TagViewModel @Inject constructor(private val tagRepository: TagRepository) : ViewModel() {

    private val _tagState = MutableStateFlow<ScreenState<CreateTagResponse>>(ScreenState.Idle)
    val tagState: StateFlow<ScreenState<CreateTagResponse>> = _tagState.asStateFlow()

    private val _updateTagState = MutableStateFlow<ScreenState<CreateTagResponse>>(ScreenState.Idle)
    val updateTagState: StateFlow<ScreenState<CreateTagResponse>> = _updateTagState.asStateFlow()

    fun createTag(createTagRequest: CreateTagRequest) {
        viewModelScope.launch {
            _tagState.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = tagRepository.createTag(
                        token, request = createTagRequest
                    )
                    if (response.isSuccess()) {
                        _tagState.value = ScreenState.Success(response.data ?: CreateTagResponse())
                    } else {
                        _tagState.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _tagState.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }

    fun updateTag(tagId: String, updateTagRequest: UpdateTagRequest) {
        viewModelScope.launch {
            _updateTagState.value = ScreenState.Loading
            SharedPrefs.accessToken?.let { token ->
                try {
                    val response = tagRepository.updateTag(
                        token, tagId = tagId,
                        request = updateTagRequest
                    )
                    if (response.isSuccess()) {
                        _updateTagState.value =
                            ScreenState.Success(response.data ?: CreateTagResponse())
                    } else {
                        _updateTagState.value =
                            ScreenState.Error(message = response.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _updateTagState.value = ScreenState.Error(message = e.getErrorMessage())
                }
            }
        }
    }
}
