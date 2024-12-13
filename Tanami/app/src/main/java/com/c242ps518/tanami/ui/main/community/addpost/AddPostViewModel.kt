package com.c242ps518.tanami.ui.main.community.addpost

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.repository.PostsRepository
import com.c242ps518.tanami.repository.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddPostViewModel(private val postsRepository: PostsRepository, private val authPreference: AuthPreference): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    val token: String? = authPreference.getToken()

    fun createPost(photo: MultipartBody.Part, caption: RequestBody) {
        _isLoading.value = true

        if (token != null) {
            viewModelScope.launch {
                when (val response = postsRepository.createPost(token, photo, caption)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }

                    is Result.Success -> {
                        _isLoading.value = false
                        _successMessage.value = response.data.message
                        Log.d("AddPostViewModel", "Success: ${response.data.message}")
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = response.error
                        Log.e("PostsViewModels", "Error: ${response.error}")
                    }
                }
            }
        }
    }
}