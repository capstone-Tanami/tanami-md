package com.c242ps518.tanami.repository

import com.c242ps518.tanami.data.remote.response.ArticlesResponse
import com.c242ps518.tanami.data.remote.response.DeleteHistoryResponse
import com.c242ps518.tanami.data.remote.response.HistoryResponse
import com.c242ps518.tanami.data.remote.retrofit.ApiService
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(
            apiService: ApiService
        ): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(apiService)
            }.also { instance = it }
    }

    suspend fun getHistory(token: String): Result<HistoryResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.getHistory("Bearer $token")
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }

    suspend fun getArticles(): ArticlesResponse = withContext(dispatcher) {
        apiService.getArticles()
    }

    suspend fun get5History(token: String): HistoryResponse = withContext(dispatcher) {
        apiService.get5History("Bearer $token")
    }

    suspend fun deletePrediction(token: String, predictionId: Int): DeleteHistoryResponse = withContext(dispatcher) {
        apiService.deletePrediction("Bearer $token", predictionId)
    }

}