package com.c242ps518.tanami.repository

import com.c242ps518.tanami.data.remote.response.ProfileResponse
import com.c242ps518.tanami.data.remote.response.UpdateProfileResponse
import com.c242ps518.tanami.data.remote.retrofit.ApiService
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        @Volatile
        private var instance: ProfileRepository? = null

        fun getInstance(
            apiService: ApiService
        ): ProfileRepository =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(apiService)
            }.also { instance = it }
    }

    suspend fun getProfile(token: String): Result<ProfileResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.getProfile("Bearer $token")
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }

    suspend fun updateProfile(
        token: String,
        photo: MultipartBody.Part? = null,
        name: RequestBody? = null
    ): Result<UpdateProfileResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.updateProfile("Bearer $token", photo, name)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }
}