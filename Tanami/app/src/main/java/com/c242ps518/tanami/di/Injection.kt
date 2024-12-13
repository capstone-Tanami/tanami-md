package com.c242ps518.tanami.di

import com.c242ps518.tanami.data.remote.retrofit.ApiConfig
import com.c242ps518.tanami.repository.AuthRepository
import com.c242ps518.tanami.repository.HistoryRepository
import com.c242ps518.tanami.repository.PredictRepository
import com.c242ps518.tanami.repository.OpenWeatherRepository
import com.c242ps518.tanami.repository.PostsRepository
import com.c242ps518.tanami.repository.ProfileRepository

object Injection {
    fun provideOpenWeatherRepository(): OpenWeatherRepository {
        val apiService = ApiConfig.getApiServiceOpenWeather()
        return OpenWeatherRepository.getInstance(apiService)
    }

    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun providePostsRepository(): PostsRepository {
        val apiService = ApiConfig.getApiService()
        return PostsRepository.getInstance(apiService)
    }

    fun provideProfileRepository(): ProfileRepository {
        val apiService = ApiConfig.getApiService()
        return ProfileRepository.getInstance(apiService)
    }

    fun providePredictRepository(): PredictRepository {
        val apiService = ApiConfig.getApiService()
        return PredictRepository.getInstance(apiService)
    }

    fun provideHistoryRepository(): HistoryRepository {
        val apiService = ApiConfig.getApiService()
        return HistoryRepository.getInstance(apiService)
    }
}