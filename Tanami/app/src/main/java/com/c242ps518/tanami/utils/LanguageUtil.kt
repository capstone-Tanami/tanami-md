package com.c242ps518.tanami.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.Log
import java.util.Locale

object LanguageUtil {
//    fun Context.setAppLocale(language: String) {
//        val locale = Locale(language)
//        Locale.setDefault(locale)
//        val config = Configuration(resources.configuration)
//        config.setLocale(locale)
//        @Suppress("DEPRECATION")
//        resources.updateConfiguration(config, resources.displayMetrics)
//    }

    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

}