package com.c242ps518.tanami.ui.main.predict

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.ActivityFormPredictBinding
import com.c242ps518.tanami.ui.factory.PredictViewModelFactory
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import com.google.gson.Gson

class FormPredictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormPredictBinding
    private lateinit var predictViewModel: PredictViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        predictViewModel = ViewModelProvider(
            this,
            PredictViewModelFactory.getInstance(this)
        )[PredictViewModel::class.java]

        setAppLocale(this, predictViewModel.language)

        binding.btnPredict.setOnClickListener {
            val nitrogenInput = binding.etNitrogen.text.toString()
            val phosphorusInput = binding.etPhosphorus.text.toString()
            val potassiumInput = binding.etPotassium.text.toString()
            val temperatureInput = binding.etTemperature.text.toString()
            val humidityInput = binding.etHumidity.text.toString()
            val phInput = binding.etPH.text.toString()
            val rainfallInput = binding.etRainfall.text.toString()

            // Validate inputs
            if (validateInputs(
                    nitrogenInput,
                    phosphorusInput,
                    potassiumInput,
                    temperatureInput,
                    humidityInput,
                    phInput,
                    rainfallInput
                )
            ) {
                // Parse inputs to Float after validation
                val nitrogen = nitrogenInput.toFloat()
                val phosphorus = phosphorusInput.toFloat()
                val potassium = potassiumInput.toFloat()
                val temperature = temperatureInput.toFloat()
                val humidity = humidityInput.toFloat()
                val ph = phInput.toFloat()
                val rainfall = rainfallInput.toFloat()

                predictViewModel.fieldPredict(
                    nitrogen,
                    phosphorus,
                    potassium,
                    temperature,
                    humidity,
                    ph,
                    rainfall
                )
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

        predictViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(
        nitrogen: String,
        phosphorus: String,
        potassium: String,
        temperature: String,
        humidity: String,
        ph: String,
        rainfall: String
    ): Boolean {
        var isValid = true

        val nitrogenLayout = binding.nitrogenInputLayout
        val phosphorusLayout = binding.phosphorusInputLayout
        val potassiumLayout = binding.potassiumInputLayout
        val temperatureLayout = binding.temperatureInputLayout
        val humidityLayout = binding.humidityInputLayout
        val phLayout = binding.phInputLayout
        val rainfallLayout = binding.rainfallInputLayout

        // Validasi Nitrogen
        if (nitrogen.isBlank()) {
            nitrogenLayout.error = getString(R.string.error_empty_nitrogen)
            isValid = false
        } else if (nitrogen.toFloatOrNull() == null || nitrogen.toFloat() < 0) {
            nitrogenLayout.error = getString(R.string.error_invalid_nitrogen)
            isValid = false
        } else {
            nitrogenLayout.error = null
            nitrogenLayout.isErrorEnabled = false
        }

        // Validasi Phosphorus
        if (phosphorus.isBlank()) {
            phosphorusLayout.error = getString(R.string.error_empty_phosphorus)
            isValid = false
        } else if (phosphorus.toFloatOrNull() == null || phosphorus.toFloat() < 0) {
            phosphorusLayout.error = getString(R.string.error_invalid_phosphorus)
            isValid = false
        } else {
            phosphorusLayout.error = null
            phosphorusLayout.isErrorEnabled = false
        }

        // Validasi Potassium
        if (potassium.isBlank()) {
            potassiumLayout.error = getString(R.string.error_empty_potassium)
            isValid = false
        } else if (potassium.toFloatOrNull() == null || potassium.toFloat() < 0) {
            potassiumLayout.error = getString(R.string.error_invalid_potassium)
            isValid = false
        } else {
            potassiumLayout.error = null
            potassiumLayout.isErrorEnabled = false
        }

        if (temperature.isBlank()) {
            temperatureLayout.error = getString(R.string.error_empty_temperature)
            isValid = false
        } else if (temperature.toFloatOrNull() == null || temperature.toFloat() !in 0f..50f) {
            temperatureLayout.error = getString(R.string.error_invalid_temperature)
            isValid = false
        } else {
            temperatureLayout.error = null
            temperatureLayout.isErrorEnabled = false
        }

        // Validasi Humidity
        if (humidity.isBlank()) {
            humidityLayout.error = getString(R.string.error_empty_humidity)
            isValid = false
        } else if (humidity.toFloatOrNull() == null || humidity.toFloat() !in 0f..100f) {
            humidityLayout.error = getString(R.string.error_invalid_humidity)
            isValid = false
        } else {
            humidityLayout.error = null
            humidityLayout.isErrorEnabled = false
        }

        // Validasi pH
        if (ph.isBlank()) {
            phLayout.error = getString(R.string.error_empty_ph)
            isValid = false
        } else if (ph.toFloatOrNull() == null || ph.toFloat() !in 0f..14f) {
            phLayout.error = getString(R.string.error_invalid_ph)
            isValid = false
        } else {
            phLayout.error = null
            phLayout.isErrorEnabled = false
        }

        // Validasi Rainfall
        if (rainfall.isBlank()) {
            rainfallLayout.error = getString(R.string.error_empty_rainfall)
            isValid = false
        } else if (rainfall.toFloatOrNull() == null || rainfall.toFloat() < 0) {
            rainfallLayout.error = getString(R.string.error_invalid_rainfall)
            isValid = false
        } else {
            rainfallLayout.error = null
            rainfallLayout.isErrorEnabled = false
        }

        return isValid
    }
}