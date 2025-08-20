package com.example.mtrschedule

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.example.mtrschedule.util.LanguageHelper
import java.util.Locale

class MtrApplication : Application() {
    override fun attachBaseContext(base: Context) {
        // Apply saved locale when app starts
        val locale = LanguageHelper.getLocale(base)
        val context = updateBaseContextLocale(base, locale)
        super.attachBaseContext(context)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Apply locale on configuration changes
        val locale = LanguageHelper.getLocale(this)
        updateBaseContextLocale(this, locale)
    }

    private fun updateBaseContextLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }
}