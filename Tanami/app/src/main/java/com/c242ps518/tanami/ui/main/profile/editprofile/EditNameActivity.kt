package com.c242ps518.tanami.ui.main.profile.editprofile

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.ActivityEditNameBinding
import com.c242ps518.tanami.ui.factory.ProfileViewModelFactory
import com.c242ps518.tanami.ui.main.profile.ProfileViewModel
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class EditNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNameBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var currentImageUrl: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNameBinding.inflate(layoutInflater)
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

        profileViewModel.profile.observe(this) { profile ->
            if (profile != null) {
                binding.nameInput.setText(profile.name)
                currentImageUrl = profile.profilePicture
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
            val newName = binding.nameInput.text.toString().trim()
            if (newName.isNotEmpty()) {
                val photoPart: MultipartBody.Part? = if (currentImageUrl != null) {
                    createMultipartFromUrl(currentImageUrl!!)
                } else {
                    createEmptyMultipart()
                }

                val nameRequestBody = newName.toRequestBody("text/plain".toMediaTypeOrNull())

                profileViewModel.updateProfile(photoPart, nameRequestBody)

                profileViewModel.successMessage.observe(this) { message ->
                    if (message != null) {
                        this.onBackPressedDispatcher.onBackPressed()
                        profileViewModel.saveName(newName)
                    }
                }
            } else {
                binding.nameInputLayout.error = getString(R.string.error_empty_name)
            }
        }
    }

    private fun createEmptyMultipart(): MultipartBody.Part {
        val emptyFile = ByteArray(0)
        val requestFile = emptyFile.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profilePicture", "", requestFile)
    }

    private fun createMultipartFromUrl(url: String): MultipartBody.Part? {
        return try {
            val file = downloadFileFromUrl(url, cacheDir)
            if (file != null) {
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profilePicture", file.name, requestFile)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun downloadFileFromUrl(url: String, outputDir: File): File? {
        return try {
            val fileName = "temp_image.jpg"
            val file = File(outputDir, fileName)
            val urlConnection = URL(url).openStream()
            val outputStream = FileOutputStream(file)
            outputStream.use { output ->
                urlConnection.copyTo(output)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    override fun onStart() {
//        super.onStart()
//        profileViewModel.getProfile()
//    }
}
