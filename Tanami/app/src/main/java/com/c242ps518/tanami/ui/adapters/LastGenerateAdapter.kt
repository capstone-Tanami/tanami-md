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
import com.c242ps518.tanami.databinding.ItemRowHistoryHrBinding
import com.c242ps518.tanami.ui.main.history.HistoryDetailActivity
import com.c242ps518.tanami.utils.DateUtil.dateFormat
import com.google.gson.Gson

class LastGenerateAdapter: ListAdapter<DataItem, LastGenerateAdapter.LastGenerateViewHolder>(
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
                return oldItem == newItem
            }
        }
    }

    class LastGenerateViewHolder(private val binding: ItemRowHistoryHrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(generate: DataItem) {
            Glide.with(binding.imagePost.context)
                .load(generate.plantPhoto)
                .placeholder(R.drawable.img_placeholder)
                .into(binding.imagePost)

            binding.prediction.text = generate.label
            binding.date.text = dateFormat(generate.predictionDate)

            itemView.setOnClickListener {
                Intent(itemView.context, HistoryDetailActivity::class.java).apply {
                    putExtra("history", Gson().toJson(generate))
                    itemView.context.startActivity(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastGenerateViewHolder {
        val binding = ItemRowHistoryHrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LastGenerateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LastGenerateViewHolder, position: Int) {
        val generate = getItem(position)
        holder.bind(generate)
    }
}