package com.c242ps518.tanami.ui.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.PostsItem
import com.c242ps518.tanami.databinding.ItemRowPostBinding
import com.c242ps518.tanami.ui.main.community.detailpost.DetailPostActivity
import com.c242ps518.tanami.utils.DateUtil.dateFormat

class ListPostsAdapter : ListAdapter<PostsItem, ListPostsAdapter.ListPostsViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostsItem>() {
            override fun areItemsTheSame(
                oldItem: PostsItem,
                newItem: PostsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostsItem,
                newItem: PostsItem
            ): Boolean {
                return oldItem.postId == newItem.postId
            }
        }
    }

    class ListPostsViewHolder(private val binding: ItemRowPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostsItem) {
            Glide.with(binding.imagePost.context)
                .load(post.photo)
                .placeholder(R.drawable.img_placeholder)
                .into(binding.imagePost)
            Glide.with(binding.profilePicture.context)
                .load(post.profilePicture)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.profilePicture)
            val username = "@${post.username}"
            binding.username.text = username
            binding.description.text = post.caption
            val date = dateFormat(post.uploadDate)
            binding.timestamp.text = date
            binding.name.text = post.name

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("username", username)
                    putString("name", post.name)
                    putString("profilePicture", post.profilePicture)
                    putString("photo", post.photo)
                    putString("caption", post.caption)
                    putString("uploadDate", date)
                }

                val intent = Intent(itemView.context, DetailPostActivity::class.java)
                intent.putExtras(bundle)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPostsViewHolder {
        val binding = ItemRowPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListPostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListPostsViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}