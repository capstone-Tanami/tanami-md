package com.c242ps518.tanami.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.di.Injection
import com.c242ps518.tanami.repository.ProfileRepository
import com.c242ps518.tanami.ui.main.profile.ProfileViewModel

class ProfileViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val authPreference: AuthPreference,
    private val langPreference: LangPreference
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: ProfileViewModelFactory? = null

        fun getInstance(
            context: Context
        ): ProfileViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ProfileViewModelFactory(
                    Injection.provideProfileRepository(),
                    AuthPreference(context),
                    LangPreference(context)
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(profileRepository, authPreference, langPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}