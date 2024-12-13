package com.c242ps518.tanami.repository

import com.c242ps518.tanami.data.remote.response.AddPostResponse
import com.c242ps518.tanami.data.remote.response.PostsResponse
import com.c242ps518.tanami.data.remote.retrofit.ApiService
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostsRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
){
    companion object {
        @Volatile
        private var instance: PostsRepository? = null

        fun getInstance(
            apiService: ApiService
        ): PostsRepository =
            instance ?: synchronized(this) {
                instance ?: PostsRepository(apiService)
            }.also { instance = it }
    }

    suspend fun getPosts(token: String): Result<PostsResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.getPosts("Bearer $token")
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }

    suspend fun createPost(token: String, photo: MultipartBody.Part, caption: RequestBody): Result<AddPostResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.createPost("Bearer $token", photo, caption)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }

}