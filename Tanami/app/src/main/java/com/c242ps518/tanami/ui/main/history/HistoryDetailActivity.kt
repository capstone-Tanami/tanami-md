package com.c242ps518.tanami.ui.main.history

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.remote.response.DataItem
import com.c242ps518.tanami.databinding.ActivityHistoryDetailBinding
import com.c242ps518.tanami.ui.factory.HistoryViewModelFactory
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import com.google.gson.Gson

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var historyViewModel: HistoryViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        historyViewModel = ViewModelProvider(
            this,
            HistoryViewModelFactory.getInstance(this)
        )[HistoryViewModel::class.java]
        setAppLocale(this, historyViewModel.language)

        val history = intent.getStringExtra("history")
        val historyData = Gson().fromJson(history, DataItem::class.java)

        val plantDetails = historyData.plantDetails

        Log.d("asu", "${historyData.plantPhoto}")

        Glide.with(this)
            .load(historyData.plantPhoto)
            .placeholder(R.drawable.img_placeholder)
            .into(binding.imageSoil)

        binding.tvPrediction.text = historyData.label
        binding.tvDescription.text = plantDetails?.description

        val customFont = getFont(this, R.font.gabarito_reguler)

// Tambahkan langkah-langkah perawatan
        plantDetails?.careInstructions?.steps?.forEach { step ->
            val textView = TextView(this).apply {
                text = "• $step"
                typeface = customFont
                setPadding(8, 8, 8, 8)
            }
            binding.containerCareInstructions.addView(textView)
        }

// Tambahkan daftar belanja
        plantDetails?.shoppingList?.forEach { item ->
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
                typeface = customFont
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item?.purchaseLink ?: ""))
                    startActivity(intent)
                }
            }
            binding.containerShoppingList.addView(linkView)
        }

// Tambahkan rekomendasi tanah
        val soilDetails = """
    Tipe Tanah: ${plantDetails?.recommendedSoil?.type}
    pH: ${plantDetails?.recommendedSoil?.ph}
    Drainase: ${plantDetails?.recommendedSoil?.drainage}
    Kandungan Organik: ${plantDetails?.recommendedSoil?.organicContent}
""".trimIndent()
        binding.containerRecommendedSoil.addView(TextView(this).apply {
            text = soilDetails
            typeface = customFont
            setPadding(8, 8, 8, 16)
        })

        val cultivationDetails = """
    Durasi dari Benih ke Panen: ${plantDetails?.cultivationDuration?.seedToHarvest}
    Musim Tanam Terbaik: ${plantDetails?.cultivationDuration?.bestPlantingSeason}
    Suhu Optimal: ${plantDetails?.cultivationDuration?.optimalTemperature}°C
""".trimIndent()
        binding.containerCultivationDuration.addView(TextView(this).apply {
            text = cultivationDetails
            typeface = customFont
            setPadding(8, 8, 8, 16)
        })

        plantDetails?.additionalTips?.forEach { tip ->
            binding.containerAdditionalTips.addView(TextView(this).apply {
                text = "• $tip"
                typeface = customFont
                setPadding(8, 8, 8, 8)
            })
        }

    }
}