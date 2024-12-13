package com.c242ps518.tanami.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.remote.response.OpenWeatherResponse
import com.c242ps518.tanami.repository.OpenWeatherRepository
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.launch

class HomeViewModel(
    private val openWeatherRepository: OpenWeatherRepository
) : ViewModel() {

    private val _weatherData = MutableLiveData<OpenWeatherResponse>()
    val weatherData: LiveData<OpenWeatherResponse> = _weatherData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getWeather(lat: Double, lon: Double, appid: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = openWeatherRepository.getWeather(lat, lon, appid)
                _weatherData.value = response
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = getErrorMessage(e)
                _isLoading.value = false
            }
        }
    }

    data class Location(val latitude: Double?, val longitude: Double?)

    private val location = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = location

    fun addLocation(lat: Double? = null, lon: Double? = null) {
        location.value = Location(lat, lon)
    }
}