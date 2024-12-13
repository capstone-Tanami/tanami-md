package com.c242ps518.tanami.repository

import com.c242ps518.tanami.data.remote.response.LoginResponse
import com.c242ps518.tanami.data.remote.response.RegisterResponse
import com.c242ps518.tanami.data.remote.retrofit.ApiService
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }

    suspend fun login(identifier: String, password: String): Result<LoginResponse> =
        withContext(dispatcher) {
            try {
                Result.Loading
                val response = apiService.login(identifier, password)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }

    suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): Result<RegisterResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.register(name, username, email, password)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }

}