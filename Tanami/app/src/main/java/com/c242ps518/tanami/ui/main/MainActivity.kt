package com.c242ps518.tanami.ui.main

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.c242ps518.tanami.R
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.databinding.ActivityMainBinding
import com.c242ps518.tanami.ui.main.predict.BottomSheetPredict
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var langPreference: LangPreference

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        langPreference = LangPreference(this)
        val language = langPreference.getLanguage()
        setAppLocale(this, language)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_community,
                R.id.navigation_history,
                R.id.navigation_profile
            )
        )

        binding.fab.setOnClickListener{
            val bottomSheet = BottomSheetPredict()
            bottomSheet.show(supportFragmentManager, BottomSheetPredict.TAG)
        }

        navView.itemIconTintList = null
        navView.setupWithNavController(navController)
    }
}