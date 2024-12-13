package com.c242ps518.tanami.ui.main.community.addpost

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.databinding.ActivityAddPostBinding
import com.c242ps518.tanami.ui.factory.CommunityViewModelFactory
import com.c242ps518.tanami.ui.main.community.CommunityViewModel
import com.c242ps518.tanami.ui.main.predict.ImagePredictActivity
import com.c242ps518.tanami.ui.main.predict.PredictViewModel
import com.c242ps518.tanami.utils.ImageUtil
import com.c242ps518.tanami.utils.ImageUtil.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding

    private var currentImageUri: Uri? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("current_image_uri", currentImageUri?.toString())
    }

    private lateinit var addPostViewModel: AddPostViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addPostViewModel = ViewModelProvider(
            this,
            CommunityViewModelFactory.getInstance(this)
        )[AddPostViewModel::class.java]

        savedInstanceState?.getString("current_image_uri")?.let {
            currentImageUri = Uri.parse(it)
        }

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        addPostViewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                showImage(uri)
            }
        }

        addPostViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.postButton.text = ""
                binding.postButton.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.postButton.text = getString(R.string.post)
                binding.postButton.isEnabled = true
            }
        }

        addPostViewModel.successMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                this.onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.postButton.setOnClickListener {
            post()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && currentImageUri != null) {
            addPostViewModel.setCurrentImageUri(currentImageUri!!)
            showImage(currentImageUri!!)
        } else {
            currentImageUri = null
            showToast(getString(R.string.no_image_selected))
        }
    }

    private fun startCamera() {
        currentImageUri = ImageUtil.getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            addPostViewModel.setCurrentImageUri(uri)
            showImage(uri)
        } else {
            showToast(getString(R.string.no_image_selected))
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage(uri: Uri) {
        binding.previewImage.setImageURI(uri)
    }

    private fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun post() {
        addPostViewModel.currentImageUri.value?.let { uri ->
            val caption = binding.editTextCaption.text.toString()
            if (caption.isEmpty()) {
                binding.textCaptionLayout.error = getString(R.string.caption_cannot_be_empty)
                return
            }

            val image = ImageUtil.uriToFile(uri, this).reduceFileImage()

            val captionRequestBody = caption.toRequestBody("text/plain".toMediaType())
            val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
            val imageMultipartBody = MultipartBody.Part.createFormData(
                "image",
                image.name,
                requestImageFile
            )

            addPostViewModel.createPost(imageMultipartBody, captionRequestBody)
        } ?: showToast(getString(R.string.no_image_selected))
    }
}