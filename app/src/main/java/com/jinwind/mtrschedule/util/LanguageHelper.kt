package com.jinwind.mtrschedule.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LanguageHelper {
    private const val LANGUAGE_PREF = "language_preference"
    private const val SELECTED_LANGUAGE = "selected_language"

    // Get the current locale from preferences
    fun getLocale(context: Context): Locale {
        val sharedPreferences = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        val language = sharedPreferences.getString(SELECTED_LANGUAGE, null)

        return if (language != null) {
            if (language.contains("_")) {
                val parts = language.split("_")
                Locale(parts[0], parts[1])
            } else {
                Locale(language)
            }
        } else {
            // If no language is set, use traditional Chinese for HK by default
            Locale("zh", "HK")
        }
    }

    // Set a new locale
    fun setLocale(context: Context, language: String, country: String = "") {
        val locale = if (country.isEmpty()) Locale(language) else Locale(language, country)
        val sharedPreferences = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(SELECTED_LANGUAGE, if (country.isEmpty()) language else "${language}_$country")
        editor.apply()

        updateResources(context, locale)
    }

    // Toggle between English and Chinese
    fun toggleLanguage(context: Context): Boolean {
        val currentLocale = getLocale(context)
        return if (currentLocale.language == "en") {
            // Switch to Chinese (Traditional for Hong Kong)
            setLocale(context, "zh", "HK")
            true
        } else {
            // Switch to English
            setLocale(context, "en")
            false
        }
    }

    // Update resources configuration
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else {
            configuration.locale = locale
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        return context
    }
}