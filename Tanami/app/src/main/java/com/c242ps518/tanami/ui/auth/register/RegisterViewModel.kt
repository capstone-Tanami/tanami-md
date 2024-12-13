package com.c242ps518.tanami.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.repository.AuthRepository
import com.c242ps518.tanami.repository.Result
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun register(name: String, username: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val response = authRepository.register(name, username, email, password)) {
                is Result.Loading -> {
                    _isLoading.value = true
                }

                is Result.Success -> {
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