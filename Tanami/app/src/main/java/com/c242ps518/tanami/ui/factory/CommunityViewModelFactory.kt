package com.c242ps518.tanami.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.di.Injection
import com.c242ps518.tanami.repository.PostsRepository
import com.c242ps518.tanami.ui.main.community.CommunityViewModel
import com.c242ps518.tanami.ui.main.community.addpost.AddPostViewModel

class CommunityViewModelFactory(
    private val postsRepository: PostsRepository,
    private val authPreference: AuthPreference
) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var instance: CommunityViewModelFactory? = null

        fun getInstance(
            context: Context
        ): CommunityViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: CommunityViewModelFactory(
                    Injection.providePostsRepository(),
                    AuthPreference(context)
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            return CommunityViewModel(postsRepository, authPreference) as T
        } else if (modelClass.isAssignableFrom(AddPostViewModel::class.java)) {
            return AddPostViewModel(postsRepository, authPreference) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}