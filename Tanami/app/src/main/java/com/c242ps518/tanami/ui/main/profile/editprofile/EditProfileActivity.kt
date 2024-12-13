package com.c242ps518.tanami.ui.main.profile.editprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.DataProfile
import com.c242ps518.tanami.databinding.ActivityEditProfileBinding
import com.c242ps518.tanami.ui.factory.ProfileViewModelFactory
import com.c242ps518.tanami.ui.main.predict.ImagePredictActivity
import com.c242ps518.tanami.ui.main.predict.PredictViewModel
import com.c242ps518.tanami.ui.main.profile.ProfileViewModel
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private var currentImageUri: Uri? = null
    private lateinit var profileViewModel: ProfileViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory.getInstance(this)
        )[ProfileViewModel::class.java]

        setAppLocale(this, profileViewModel.language)

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding.layoutPhoto.setOnClickListener {
            startGallery()
        }

        binding.layoutName.setOnClickListener {
            val intent = Intent(this, EditNameActivity::class.java)
            startActivity(intent)
        }

        profileViewModel.profile.observe(this) { profile ->
            if (profile != null) {
                setProfile(profile)
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            val intent = Intent(this, EditPhotoActivity::class.java)
            intent.putExtra("image_uri", currentImageUri.toString())
            startActivity(intent)
        } else {
            showToast(getString(R.string.no_image_selected))
        }
    }

    private fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setProfile(profile: DataProfile) {
        binding.tvProfileName.text = profile.name
        Glide.with(binding.ivProfilePicture.context)
            .load(profile.profilePicture)
            .placeholder(R.drawable.baseline_account_circle_24)
            .into(binding.ivProfilePicture)
    }

    override fun onStart() {
        super.onStart()
        profileViewModel.getProfile()
    }
}