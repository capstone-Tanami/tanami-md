package com.c242ps518.tanami.ui.main.profile.editprofile

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.ActivityEditPhotoBinding
import com.c242ps518.tanami.ui.factory.ProfileViewModelFactory
import com.c242ps518.tanami.ui.main.profile.ProfileViewModel
import com.c242ps518.tanami.utils.ImageUtil
import com.c242ps518.tanami.utils.ImageUtil.reduceFileImage
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPhotoBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var currentName: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory.getInstance(this)
        )[ProfileViewModel::class.java]

        setAppLocale(this, profileViewModel.language)

        val imageUri = intent.getStringExtra("image_uri")
        val imageView = binding.photoPreview
        imageView.setImageURI(Uri.parse(imageUri))

        profileViewModel.profile.observe(this) { profile ->
            if (profile != null) {
                currentName = profile.name
            }
        }

        profileViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.saveButton.text = ""
                binding.saveButton.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.saveButton.text = getString(R.string.save)
                binding.saveButton.isEnabled = true
            }
        }

        binding.saveButton.setOnClickListener {
            val imgUri = imageUri?.toUri()

            if (imgUri != null) {
                val image = ImageUtil.uriToFile(imgUri, this).reduceFileImage()
                val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
                val photoPart = MultipartBody.Part.createFormData(
                    "profilePicture",
                    image.name,
                    requestImageFile
                )

                val nameRequestBody = currentName?.toRequestBody("text/plain".toMediaTypeOrNull())

                profileViewModel.updateProfile(photoPart, nameRequestBody)
                profileViewModel.successMessage.observe(this) { message ->
                    if (message != null) {
                        this.onBackPressedDispatcher.onBackPressed()
                    }
                }
            } else {
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }
}