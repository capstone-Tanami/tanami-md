package com.c242ps518.tanami.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostsResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("posts")
	val posts: List<PostsItem> = emptyList()
)

data class PostsItem(

	@field:SerializedName("profilePicture")
	val profilePicture: String? = null,

	@field:SerializedName("uploadDate")
	val uploadDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null,

	@field:SerializedName("caption")
	val caption: String? = null,

	@field:SerializedName("postId")
	val postId: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("username")
	val username: String? = null
)
