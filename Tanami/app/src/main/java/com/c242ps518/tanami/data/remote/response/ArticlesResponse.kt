package com.c242ps518.tanami.data.remote.response

import com.google.gson.annotations.SerializedName

data class ArticlesResponse(

	@field:SerializedName("articles")
	val articles: List<ArticlesItem> = emptyList()
)

data class ArticlesItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
