package com.c242ps518.tanami.ui.main.predict

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.data.remote.response.FieldResponse
import com.c242ps518.tanami.repository.PredictRepository
import kotlinx.coroutines.launch
import com.c242ps518.tanami.repository.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PredictViewModel(
    private val predictRepository: PredictRepository,
    private val authPreference: AuthPreference,
    private val langPreference: LangPreference
) : ViewModel() {

    private val _result = MutableLiveData<FieldResponse>()
    val result: LiveData<FieldResponse> get() = _result

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val token: String? = authPreference.getToken()

    val language = langPreference.getLanguage()

    fun imagePredict(
        photo: MultipartBody.Part?,
        n: RequestBody,
        p: RequestBody,
        k: RequestBody,
        temperature: RequestBody,
        humidity: RequestBody,
        ph: RequestBody,
        rainfall: RequestBody
    ) {
        _isLoading.value = true
        if (token != null) {
            viewModelScope.launch {
                when (val response = predictRepository.imagePrediction(token, photo, n, p, k, temperature, humidity, ph, rainfall)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _isLoading.value = false
                        _result.value = response.data
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = response.error
                        Log.e("PredictViewModel", "Error: ${response.error}")
                    }
                }
            }
        }
    }


    fun fieldPredict(
        n: Float,
        p: Float,
        k: Float,
        temperature: Float,
        humidity: Float,
        ph: Float,
        rainfall: Float
    ) {
        _isLoading.value = true

        if (token != null) {
            viewModelScope.launch {
                when (val response = predictRepository.fieldPrediction(token, n, p, k, temperature, humidity, ph, rainfall)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _isLoading.value = false
                        _result.value = response.data
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = response.error
                        Log.e("PredictViewModel", "Error: ${response.error}")
                    }
                }
            }
        }
    }
}