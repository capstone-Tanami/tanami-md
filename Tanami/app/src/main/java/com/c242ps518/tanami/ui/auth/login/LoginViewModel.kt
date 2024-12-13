package com.c242ps518.tanami.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.remote.response.LoginData
import com.c242ps518.tanami.repository.AuthRepository
import com.c242ps518.tanami.repository.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _loginData = MutableLiveData<LoginData?>()
    val loginData: LiveData<LoginData?> get() = _loginData

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val response = authRepository.login(email, password)) {
                is Result.Loading -> {
                    _isLoading.value = true
                }

                is Result.Success -> {
                    _loginData.value = response.data.responseData
                    _isLoading.value = false
                    _message.value = response.data.message
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _error.value = response.error
                }
            }
        }
    }
}