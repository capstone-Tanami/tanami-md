package com.c242ps518.tanami.ui.main.community.detailpost

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.ActivityDetailPostBinding
import com.c242ps518.tanami.utils.DateUtil.dateFormat

class DetailPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPostBinding
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        setUi()
    }

    private fun setUi(){
        val username = intent.getStringExtra("username")
        val name = intent.getStringExtra("name")
        val profilePicture = intent.getStringExtra("profilePicture")
        val photo = intent.getStringExtra("photo")
        val caption = intent.getStringExtra("caption")
        val uploadDate = intent.getStringExtra("uploadDate")

        binding.username.text = username
        binding.name.text = name
        binding.description.text = caption
        binding.timestamp.text = uploadDate
        Glide.with(binding.profilePicture.context)
            .load(profilePicture)
            .placeholder(R.drawable.baseline_account_circle_24)
            .into(binding.profilePicture)
        Glide.with(binding.imagePost.context)
            .load(photo)
            .placeholder(R.drawable.img_placeholder)
            .into(binding.imagePost)
    }
}