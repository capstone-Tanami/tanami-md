package com.c242ps518.tanami.ui.main.predict

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.BottomSheetPredictBinding
import com.c242ps518.tanami.utils.ImageUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetPredict : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPredictBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private lateinit var predictViewModel: PredictViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("current_image_uri", currentImageUri?.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPredictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.getString("current_image_uri")?.let {
            currentImageUri = Uri.parse(it)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnCamera.setOnClickListener {
            startCamera()

        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnManual.setOnClickListener {
            val intent = Intent(requireContext(), FormPredictActivity::class.java)
            startActivity(intent)
            dismiss()
        }

    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && currentImageUri != null) {
            val intent = Intent(requireContext(), ImagePredictActivity::class.java)
            intent.putExtra("image_uri", currentImageUri.toString())
            startActivity(intent)
            dismiss()
        } else {
            currentImageUri = null
            showToast("No image selected")
        }
    }

    private fun startCamera() {
        currentImageUri = ImageUtil.getImageUri(requireContext())
        Log.d("BottomSheetPredict", "Start Camera URI: $currentImageUri")
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            val intent = Intent(requireContext(), ImagePredictActivity::class.java)
            intent.putExtra("image_uri", currentImageUri.toString())
            startActivity(intent)
            dismiss()
        } else {
            showToast("No image selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun showToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "BottomSheetPredict"
    }
}