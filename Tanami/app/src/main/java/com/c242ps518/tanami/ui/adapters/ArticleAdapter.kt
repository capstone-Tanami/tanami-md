package com.c242ps518.tanami.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.ArticlesItem
import com.c242ps518.tanami.databinding.ItemRowArticleBinding
import com.c242ps518.tanami.ui.main.history.HistoryDetailActivity
import com.c242ps518.tanami.ui.main.home.article.ArticleActivity
import com.google.gson.Gson

class ArticlesAdapter: ListAdapter<ArticlesItem, ArticlesAdapter.ListArticleViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ListArticleViewHolder(private val binding: ItemRowArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            Glide.with(binding.imgArticle.context)
                .load(article.image)
                .placeholder(R.drawable.img_placeholder)
                .into(binding.imgArticle)

            binding.article.text = article.title

            itemView.setOnClickListener {
                Intent(itemView.context, ArticleActivity::class.java).apply {
                    putExtra("article", Gson().toJson(article))
                    itemView.context.startActivity(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListArticleViewHolder {
        val binding = ItemRowArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }
}