package com.c242ps518.tanami.ui.main.predict

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.PlantDetails
import com.c242ps518.tanami.databinding.ActivityResultBinding
import com.c242ps518.tanami.ui.factory.PredictViewModelFactory
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var predictViewModel: PredictViewModel

    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
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


        val prediction = intent.getStringExtra("prediction")
        val plantDetailsJson = intent.getStringExtra("plant_details")
        val plantDetails = Gson().fromJson(plantDetailsJson, PlantDetails::class.java)

        binding.tvPrediction.text = "$prediction"
        binding.tvDescription.text = plantDetails.description

        val customFont = getFont(this, R.font.gabarito_reguler)

// Tambahkan langkah-langkah perawatan
        plantDetails.careInstructions?.steps?.forEach { step ->
            val textView = TextView(this).apply {
                text = "• $step"
                typeface = customFont
                setPadding(8, 8, 8, 8)
            }
            binding.containerCareInstructions.addView(textView)
        }

// Tambahkan daftar belanja
        plantDetails.shoppingList?.forEach { item ->
            val itemView = TextView(this).apply {
                if (item != null) {
                    text = "${item.item}: ${item.tips}"
                }
                typeface = customFont
                setPadding(8, 8, 8, 8)
            }
            binding.containerShoppingList.addView(itemView)

            val linkView = TextView(this).apply {
                text = "Beli di sini"
                setTextColor(getColor(R.color.green))
                setPadding(8, 0, 8, 16)
                typeface = getFont(this@ResultActivity, R.font.gabarito_semibold)
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item?.purchaseLink ?: ""))
                    startActivity(intent)
                }
            }
            binding.containerShoppingList.addView(linkView)
        }

// Tambahkan rekomendasi tanah
        val soilDetails = """
    Tipe Tanah: ${plantDetails.recommendedSoil?.type}
    pH: ${plantDetails.recommendedSoil?.ph}
    Drainase: ${plantDetails.recommendedSoil?.drainage}
    Kandungan Organik: ${plantDetails.recommendedSoil?.organicContent}
""".trimIndent()
        binding.containerRecommendedSoil.addView(TextView(this).apply {
            text = soilDetails
            typeface = customFont
            setPadding(8, 8, 8, 16)
        })

        val cultivationDetails = """
    Durasi dari Benih ke Panen: ${plantDetails.cultivationDuration?.seedToHarvest}
    Musim Tanam Terbaik: ${plantDetails.cultivationDuration?.bestPlantingSeason}
    Suhu Optimal: ${plantDetails.cultivationDuration?.optimalTemperature}°C
""".trimIndent()
        binding.containerCultivationDuration.addView(TextView(this).apply {
            text = cultivationDetails
            typeface = customFont
            setPadding(8, 8, 8, 16)
        })

        plantDetails.additionalTips?.forEach { tip ->
            binding.containerAdditionalTips.addView(TextView(this).apply {
                text = "• $tip"
                typeface = customFont
                setPadding(8, 8, 8, 8)
            })
        }

    }

}