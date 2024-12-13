package com.c242ps518.tanami.ui.main.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.data.remote.response.DataProfile
import com.c242ps518.tanami.repository.ProfileRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.c242ps518.tanami.repository.Result

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authPreference: AuthPreference,
    private val langPreference: LangPreference
) : ViewModel() {
    private val _profile = MutableLiveData<DataProfile?>()
    val profile: LiveData<DataProfile?> get() = _profile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    val language: String = langPreference.getLanguage()

    fun saveLanguage(language: String) {
        langPreference.saveLanguage(language)
    }

    fun clearAuthData() {
        authPreference.clearAuthData()
    }

    private val token: String? = authPreference.getToken()

    fun saveName(name: String) {
        authPreference.saveName(name)
    }

    fun getProfile() {
        viewModelScope.launch {
            if (token != null) {
                when (val response = profileRepository.getProfile(token)) {
                    is Result.Success -> {
                        _profile.value = response.data.dataProfile
                        Log.d("ProfileViewModel", "Success: ${response.data.dataProfile}")
                    }

                    is Result.Error -> {
                        Log.e("ProfileViewModel", "Error: ${response.error}")
                    }

                    Result.Loading -> {}
                }
            }
        }
    }

    init {
        getProfile()
    }

    fun updateProfile(photo: MultipartBody.Part? = null, name: RequestBody? = null) {
        _isLoading.value = true

        viewModelScope.launch {
            if (token != null) {
                when (val response = profileRepository.updateProfile(token, photo, name)) {
                    Result.Loading -> {
                        _isLoading.value = true
                    }

                    is Result.Success -> {
                        _isLoading.value = false
                        _successMessage.value = response.data.message
                        Log.d("updateProfile", "Success: ${response.data.message}")
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = response.error
                        Log.e("updateProfile", "Error: ${response.error}")
                    }
                }
            }
        }
    }

}