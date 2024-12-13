package com.c242ps518.tanami.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.c242ps518.tanami.data.pref.AuthPreference
import com.c242ps518.tanami.data.pref.LangPreference
import com.c242ps518.tanami.databinding.ActivitySplashBinding
import com.c242ps518.tanami.ui.auth.AuthActivity
import com.c242ps518.tanami.ui.main.MainActivity
import com.c242ps518.tanami.utils.LanguageUtil.setAppLocale

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var langPreference: LangPreference
    private lateinit var authPreference: AuthPreference
    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
//        val green = ContextCompat.getColor(this, R.color.green)
//        enableEdgeToEdge(
//            navigationBarStyle = if(!isDarkMode){
//                SystemBarStyle.light(
//                    green,
//                    green
//                )
//            } else {
//                SystemBarStyle.dark(green)
//            }
//        )

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, 0, systemBars.right, 0)
//            insets
//        }


        langPreference = LangPreference(this)
        val language = langPreference.getLanguage()
        setAppLocale(this, language)

        Log.d("MyApp", "Language set to: $language")

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            Handler(Looper.getMainLooper()).postDelayed({

                authPreference = AuthPreference(this)
                val token = authPreference.getToken()

                Log.d("MyApp", "Token: $token")

                if (!token.isNullOrEmpty()) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }

//
//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, AuthActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
//        }, 2000)

//        val logo = findViewById<ImageView>(R.id.logo)
//
//        val slideUp = ObjectAnimator.ofFloat(logo, "translationY", 300f, 0f).apply {
//            duration = 1000
//            startDelay = 500
//        }
//
//        slideUp.start()
//        slideUp.doOnEnd {
//            // Pindah ke aktivitas setelah animasi selesai
//            val intent = Intent(this, AuthActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

    }
}