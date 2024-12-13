package com.c242ps518.tanami.ui.main.community

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.remote.response.PostsItem
import com.c242ps518.tanami.repository.PostsRepository
import com.c242ps518.tanami.repository.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody


class CommunityViewModel(
    private val postsRepository: PostsRepository,
    private val authPreference: AuthPreference
) : ViewModel() {

    private val _posts = MutableLiveData<List<PostsItem>>()
    val posts: LiveData<List<PostsItem>> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    val token: String? = authPreference.getToken()
    fun getPosts() {
        _isLoading.value = true

        if (token != null) {
            viewModelScope.launch {
                when (val response = postsRepository.getPosts(token)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }

                    is Result.Success -> {
                        _isLoading.value = false
                        _posts.value = response.data.posts
                        Log.d("PostsViewModel", "Posts: ${response.data.posts}")
                    }

                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = response.error
                        Log.e("PostsViewModel", "Error: ${response.error}")
                    }
                }
            }
        }
    }

    init {
        getPosts()
    }
}