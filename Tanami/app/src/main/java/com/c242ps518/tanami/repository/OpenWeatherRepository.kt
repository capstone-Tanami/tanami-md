package com.c242ps518.tanami.repository

import com.c242ps518.tanami.data.remote.response.ArticlesResponse
import com.c242ps518.tanami.data.remote.response.HistoryResponse
import com.c242ps518.tanami.data.remote.response.OpenWeatherResponse
import com.c242ps518.tanami.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpenWeatherRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        @Volatile
        private var instance: OpenWeatherRepository? = null

        fun getInstance(
            apiService: ApiService
        ): OpenWeatherRepository =
            instance ?: synchronized(this) {
                instance ?: OpenWeatherRepository(apiService)
            }.also { instance = it }
    }

    suspend fun getWeather(lat: Double, lon: Double, appid: String): OpenWeatherResponse =
        withContext(dispatcher) {
            apiService.getWeather(lat, lon, appid)
        }
}