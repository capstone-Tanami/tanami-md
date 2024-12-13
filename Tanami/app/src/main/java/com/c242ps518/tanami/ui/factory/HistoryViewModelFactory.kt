package com.c242ps518.tanami.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.di.Injection
import com.c242ps518.tanami.repository.HistoryRepository
import com.c242ps518.tanami.ui.main.history.HistoryViewModel

class HistoryViewModelFactory(
    private val historyRepository: HistoryRepository,
    private val authPreference: AuthPreference,
    private val langPreference: LangPreference
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: HistoryViewModelFactory? = null

        fun getInstance(
            context: Context
        ): HistoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HistoryViewModelFactory(
                    Injection.provideHistoryRepository(),
                    AuthPreference(context),
                    LangPreference(context)
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(historyRepository, authPreference, langPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}