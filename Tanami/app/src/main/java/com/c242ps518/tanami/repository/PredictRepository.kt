package com.c242ps518.tanami.repository

import com.c242ps518.tanami.data.remote.response.FieldResponse
import com.c242ps518.tanami.data.remote.retrofit.ApiService
import com.c242ps518.tanami.data.remote.retrofit.FieldRequest
import com.c242ps518.tanami.data.remote.retrofit.InputData
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PredictRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        @Volatile
        private var instance: PredictRepository? = null

        fun getInstance(
            apiService: ApiService
        ): PredictRepository =
            instance ?: synchronized(this) {
                instance ?: PredictRepository(apiService)
            }.also { instance = it }
    }

    suspend fun fieldPrediction(
        token: String,
        n: Float,
        p: Float,
        k: Float,
        temperature: Float,
        humidity: Float,
        ph: Float,
        rainfall: Float
    ): Result<FieldResponse> = withContext(dispatcher) {
        try {
            Result.Loading

            val request = FieldRequest(
                inputData = InputData(
                    n = n,
                    p = p,
                    k = k,
                    temperature = temperature,
                    humidity = humidity,
                    ph = ph,
                    rainfall = rainfall
                )
            )

            // Panggil API dan dapatkan respons
            val response = apiService.predictField("Bearer $token", request)

            // Kembalikan hasil sukses
            Result.Success(response)
        } catch (e: Exception) {
            // Tangani error dan kembalikan hasil error
            Result.Error(getErrorMessage(e))
        }
    }

    suspend fun imagePrediction(
        token: String,
        photo: MultipartBody.Part?,
        n: RequestBody,
        p: RequestBody,
        k: RequestBody,
        temperature: RequestBody,
        humidity: RequestBody,
        ph: RequestBody,
        rainfall: RequestBody
    ): Result<FieldResponse> = withContext(dispatcher) {
        try {
            Result.Loading
            val response = apiService.predictImage(
                "Bearer $token",
                photo,
                n,
                p,
                k,
                temperature,
                humidity,
                ph,
                rainfall
            )
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(getErrorMessage(e))
        }
    }


}