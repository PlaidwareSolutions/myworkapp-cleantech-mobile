package com.example.rfidapp.data.repository

import com.example.rfidapp.data.network.TagApi
import com.example.rfidapp.model.network.ApiResponse
import com.example.rfidapp.model.network.CreateTagRequest
import com.example.rfidapp.model.network.CreateTagResponse
import com.example.rfidapp.model.network.FetchTagsResponse
import com.example.rfidapp.model.network.UpdateTagRequest
import javax.inject.Inject

class TagRepository @Inject constructor(private val tagApi: TagApi) {

    suspend fun createTag(
        token: String,
        request: CreateTagRequest
    ): ApiResponse<CreateTagResponse> {
        return tagApi.createTag(token, request)
    }

    suspend fun updateTag(
        token: String,
        tagId: String,
        request: UpdateTagRequest
    ):  ApiResponse<CreateTagResponse> {
        return tagApi.updateTag(token, tagId, request)
    }

    suspend fun fetchTags(token: String):  ApiResponse<FetchTagsResponse> {
        return tagApi.getTags(token)
    }

}
