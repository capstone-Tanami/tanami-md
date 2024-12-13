package com.c242ps518.tanami.ui.main.home.article

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.ArticlesItem
import com.c242ps518.tanami.data.remote.response.DataItem
import com.c242ps518.tanami.databinding.ActivityArticleBinding
import com.google.gson.Gson

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        val article = intent.getStringExtra("article")
        val articleData = Gson().fromJson(article, ArticlesItem::class.java)

        Glide.with(this)
            .load(articleData.image)
            .placeholder(R.drawable.img_placeholder)
            .into(binding.imageArticle)

        binding.tvTitle.text = articleData.title
        binding.tvContent.text = articleData.content
    }
}