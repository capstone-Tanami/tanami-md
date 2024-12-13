package com.c242ps518.tanami.ui.main.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.data.remote.response.ArticlesItem
import com.c242ps518.tanami.data.remote.response.DataItem
import com.c242ps518.tanami.repository.HistoryRepository
import kotlinx.coroutines.launch
import com.c242ps518.tanami.repository.Result
import com.c242ps518.tanami.utils.ErrorUtil.getErrorMessage

class HistoryViewModel(
    private val historyRepository: HistoryRepository,
    private val authPreference: AuthPreference,
    private val langPreference: LangPreference
) : ViewModel() {

    private val _history = MutableLiveData<List<DataItem>>()
    val history: LiveData<List<DataItem>> get() = _history

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val token = authPreference.getToken()

    val language = langPreference.getLanguage()

    fun getHistory() {
        _isLoading.value = true
        if (token != null) {
            Log.d("HistoryViewModel", "Token: $token")
            viewModelScope.launch {
                when (val response = historyRepository.getHistory(token)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }

                    is Result.Success -> {
                        _isLoading.value = false
                        _history.value = response.data.dataItem
                        Log.d("HistoryViewModel", "Success: ${response.data.dataItem}")
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = response.error
                        Log.e("HistoryViewModel", "Error: ${response.error}")
                    }
                }
            }
        }
    }

    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> get() = _articles

    fun getArticles() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = historyRepository.getArticles()
                _articles.value = response.articles
                _isLoading.value = false
                Log.d("HistoryViewModel", "Articles: ${response.articles}")
            } catch (e: Exception) {
                _errorMessage.value = getErrorMessage(e)
                _isLoading.value = false
                Log.e("HistoryViewModel", "Error: ${e.message}")
            }
        }
    }

    private val _history5 = MutableLiveData<List<DataItem>>()
    val history5: LiveData<List<DataItem>> get() = _history5

    fun get5History() {
        _isLoading.value = true
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = historyRepository.get5History(token)
                    _isLoading.value = false
                    _history5.value = response.dataItem
                    Log.d("HistoryViewModel", "Success: ${response.dataItem}")
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = getErrorMessage(e)
                }
            }
        }
    }

    fun deletePrediction(predictionId: Int) {
        _isLoading.value = true
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = historyRepository.deletePrediction(token, predictionId)
                    _isLoading.value = false
                    getHistory()
                    Log.d("HistoryViewModel", "Success: $response")
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = getErrorMessage(e)
                    Log.e("HistoryViewModel", "Error: ${e.message}")
                }
            }
        }
    }

    init {
        getHistory()
    }
}