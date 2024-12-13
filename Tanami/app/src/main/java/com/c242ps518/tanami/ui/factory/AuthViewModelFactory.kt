package com.c242ps518.tanami.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.di.Injection
import com.c242ps518.tanami.repository.AuthRepository
import com.c242ps518.tanami.ui.auth.login.LoginViewModel
import com.c242ps518.tanami.ui.auth.register.RegisterViewModel

class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null

        fun getInstance(): AuthViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(
                    Injection.provideAuthRepository()
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}