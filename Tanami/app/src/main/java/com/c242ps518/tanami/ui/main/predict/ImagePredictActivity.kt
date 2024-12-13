package com.c242ps518.tanami.ui.main.predict

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.ActivityImagePredictBinding
import com.c242ps518.tanami.helpers.SoilHelper
import com.c242ps518.tanami.ui.factory.HomeViewModelFactory
import com.c242ps518.tanami.ui.factory.PredictViewModelFactory
import com.c242ps518.tanami.ui.main.home.HomeViewModel
import com.c242ps518.tanami.utils.ImageUtil
import com.c242ps518.tanami.utils.ImageUtil.reduceFileImage
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import com.c242ps518.tanami.utils.MapResultToData.mapResult
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class ImagePredictActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagePredictBinding
    private lateinit var soilHelper: SoilHelper

    private lateinit var locationRequest: LocationRequest
    private lateinit var predictViewModel: PredictViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var imgUri: Uri? = null

    private var temperature: Float? = null
    private var humidity: Float? = null
    private var rainfall: Float? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePredictBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        predictViewModel = ViewModelProvider(
            this,
            PredictViewModelFactory.getInstance(this)
        )[PredictViewModel::class.java]
        setAppLocale(this, predictViewModel.language)

        val imageUri = intent.getStringExtra("image_uri")
        val imageView = binding.imageSoil
        imageView.setImageURI(Uri.parse(imageUri))

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory.getInstance()
        )[HomeViewModel::class.java]

        homeViewModel.currentLocation.observe(this) { location ->
            if (location.latitude != null && location.longitude != null) {
                homeViewModel.getWeather(
                    location.latitude,
                    location.longitude,
                    "dd5ffe5135d0ad030b6ac240733df87d"
                )
            }
        }

        getLocationAccess()

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        imgUri = imageUri?.toUri()

        setupSoilHelper()

        binding.btnPredict.setOnClickListener {
            if (imgUri != null) {
                binding.progressBar.visibility = View.VISIBLE
                soilHelper.classifyStaticImage(imgUri!!)
            }
        }

        predictViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnPredict.text = ""
                binding.btnPredict.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.btnPredict.text = getString(R.string.predict)
                binding.btnPredict.isEnabled = true
            }
        }

        predictViewModel.result.observe(this) { result ->
            if (result.status == "success") {
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("prediction", result.prediction)
                    putExtra("plant_details", Gson().toJson(result.plantDetails))
                }
                startActivity(intent)
            }
        }

        homeViewModel.weatherData.observe(this) { weatherData ->
            if (weatherData != null) {
                temperature = weatherData.main?.temp?.let { kelvinToCelsius(it).toFloat() }
                humidity = weatherData.main?.humidity?.toFloat()
                rainfall = weatherData.rain?.jsonMember1h?.toFloat() ?: 0f
            }
        }
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        val celsius = kelvin - 273.15
        return celsius
    }

    private fun setupSoilHelper() {
        soilHelper = SoilHelper(
            context = this,
            classifierListener = object : SoilHelper.ClassifierListener {
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    val resultText = results
                        ?.firstOrNull()
                        ?.categories?.firstOrNull()
                        ?.label?.trim()

                    if (resultText.isNullOrEmpty()) {
                        Log.e("ImagePredictActivity", "No valid result text found!")
                        return
                    }

                    Log.d("ImagePredictActivity", "Result Text: $resultText")
                    val result = mapResult(resultText)
                    if (result == null) {
                        Log.e("ImagePredictActivity", "Mapping result failed!")
                        return
                    }

                    // Cek imgUri
                    if (imgUri == null) {
                        Log.e("ImagePredictActivity", "Image URI is null!")
                        return
                    }

                    val image = try {
                        ImageUtil.uriToFile(imgUri!!, this@ImagePredictActivity).reduceFileImage()
                    } catch (e: Exception) {
                        Log.e("ImagePredictActivity", "Failed to process image: ${e.message}")
                        return
                    }

                    val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
                    val imageMultipartBody = MultipartBody.Part.createFormData(
                        "image",
                        image.name,
                        requestImageFile
                    )
                    val phValue = result["pH"] as? Double // Mengambil pH sebagai Double
                    val nRequestBody = (result["nitrogen"]?.toString() ?: "0").toRequestBody("text/plain".toMediaType())
                    val pRequestBody = (result["phosphorus"]?.toString() ?: "0").toRequestBody("text/plain".toMediaType())
                    val kRequestBody = (result["potassium"]?.toString() ?: "0").toRequestBody("text/plain".toMediaType())
                    val phRequestBody = (phValue.toString() ?: "0").toRequestBody("text/plain".toMediaType())
                    val temperatureRequestBody = (temperature?.toString() ?: "25").toRequestBody("text/plain".toMediaType())
                    val humidityRequestBody = (humidity?.toString() ?: "50").toRequestBody("text/plain".toMediaType())
                    val rainfallRequestBody = (rainfall?.toString() ?: "0").toRequestBody("text/plain".toMediaType())

                    predictViewModel.imagePredict(
                        imageMultipartBody,
                        nRequestBody,
                        pRequestBody,
                        kRequestBody,
                        temperatureRequestBody,
                        humidityRequestBody,
                        phRequestBody,
                        rainfallRequestBody
                    )

                    Log.d("ImagePredictActivity", "Prediction request sent successfully.")
                }

                override fun onError(error: String) {
                    Toast.makeText(this@ImagePredictActivity, error, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast(getString(R.string.permission_granted))
            enablePredictButton()
            getMyLastLocation()
        } else {
            showToast(getString(R.string.please_grant_location_permission))
            disablePredictButton()
        }
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enablePredictButton()
            checkLocationSettings()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getMyLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    homeViewModel.addLocation(location.latitude, location.longitude)
                } else {
                    getCurrentAccurateLocation(fusedLocationClient)
                }
            }.addOnFailureListener {
                showToast(getString(R.string.failed_get_location))
                getCurrentAccurateLocation(fusedLocationClient)
            }

        }
    }

    private fun enablePredictButton() {
        binding.btnPredict.isEnabled = true
    }

    private fun disablePredictButton() {
        binding.btnPredict.isEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentAccurateLocation(fusedLocationClient: FusedLocationProviderClient) {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        fusedLocationClient.getCurrentLocation(priority, null).addOnSuccessListener { location ->
            if (location != null) {
                homeViewModel.addLocation(location.latitude, location.longitude)
            } else {
                showToast(getString(R.string.failed_location))
            }
        }
    }

    private fun checkLocationSettings() {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val interval = TimeUnit.MINUTES.toMillis(1)
        val maxWaitTime = TimeUnit.MINUTES.toMillis(1)

        locationRequest = LocationRequest.Builder(
            priority,
            interval
        ).apply {
            setMaxUpdateDelayMillis(maxWaitTime)
        }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        showToast("Error: ${sendEx.message}")
                    }
                }
            }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    getMyLastLocation()
                }

                RESULT_CANCELED -> {
                    disablePredictButton()
                    showToast(getString(R.string.gps_enable_feture))
                }
            }
        }

    private fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

}