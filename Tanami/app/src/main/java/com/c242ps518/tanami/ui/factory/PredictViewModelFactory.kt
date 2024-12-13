package com.c242ps518.tanami.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.di.Injection
import com.c242ps518.tanami.repository.PredictRepository
import com.c242ps518.tanami.ui.main.predict.PredictViewModel


class PredictViewModelFactory(
    private val predictRepository: PredictRepository,
    private val authPreference: AuthPreference,
    private val langPreference: LangPreference
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: PredictViewModelFactory? = null

        fun getInstance(
            context: Context
        ): PredictViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: PredictViewModelFactory(
                    Injection.providePredictRepository(),
                    AuthPreference(context),
                    LangPreference(context)
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredictViewModel::class.java)) {
            return PredictViewModel(predictRepository, authPreference, langPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}