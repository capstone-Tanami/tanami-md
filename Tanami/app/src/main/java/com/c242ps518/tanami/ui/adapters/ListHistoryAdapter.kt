package com.c242ps518.tanami.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.DataItem
import com.c242ps518.tanami.databinding.ItemRowHistoryBinding
import com.c242ps518.tanami.ui.main.history.HistoryDetailActivity
import com.c242ps518.tanami.utils.DateUtil.dateFormat
import com.google.gson.Gson

class ListHistoryAdapter(private val onDeleteClicked: (DataItem) -> Unit): ListAdapter<DataItem, ListHistoryAdapter.ListHistoryViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem.predictionID == newItem.predictionID
            }
        }
    }

    class ListHistoryViewHolder(private val binding: ItemRowHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: DataItem, onDeleteClicked: (DataItem) -> Unit) {
            Glide.with(binding.imagePost.context)
                .load(history.plantPhoto)
                .placeholder(R.drawable.img_placeholder)
                .into(binding.imagePost)

            val date = dateFormat(history.predictionDate)
            binding.date.text = date
            binding.prediction.text = history.label

            itemView.setOnClickListener {
                Intent(itemView.context, HistoryDetailActivity::class.java).apply {
                    putExtra("history", Gson().toJson(history))
                    itemView.context.startActivity(this)
                }
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClicked(history)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHistoryViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHistoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story, onDeleteClicked)
    }
}