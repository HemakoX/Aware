package com.projects.aware.data.repo

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import com.projects.aware.main.settings.AppTheme
import com.projects.aware.ui.screens.overlay.OverlaySettings
import java.util.Locale

class PreferencesManager(
    private val context: Context,
) {
    private val sortPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val overlayPrefs = context.getSharedPreferences("overlay_settings_prefs", Context.MODE_PRIVATE)
    private val settingsPrefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val SORT_KEY = "sort_type"
        private const val DEFAULT_SORT = "Name" // Default value
        private const val LANGUAGE_KEY = "app_language"
        private const val THEME_KEY = "app_theme"
    }

    fun saveSortType(sortType: SortType) {
        sortPrefs.edit { putString(SORT_KEY, sortType.name) }
    }

    fun getSortType(): SortType {
        val sortType = sortPrefs.getString(SORT_KEY, DEFAULT_SORT)
        return sortType?.let { SortType.valueOf(it) } ?: SortType.Name
    }

    fun getOverlaySettings(): OverlaySettings {
        return OverlaySettings(
            background = Color(overlayPrefs.getInt("bg_color", Color.Black.toArgb())),
            textColor = Color(overlayPrefs.getInt("text_color", Color.White.toArgb())),
            showAppName = overlayPrefs.getBoolean("show_app_name", true),
            showAppUsage = overlayPrefs.getBoolean("show_app_usage", true),
            cornerRadius = overlayPrefs.getInt("corner_radius", 30),
            showAppIcon = overlayPrefs.getBoolean("show_app_icon", true),
            isBubbleVisible = overlayPrefs.getBoolean("is_bubble_visible", false)
        )
    }
    fun saveOverlaySettings(overlaySettings: OverlaySettings) {
        overlayPrefs.edit {
            putInt("bg_color", overlaySettings.background.toArgb())
            putInt("text_color", overlaySettings.textColor.toArgb())
            putInt("corner_radius", overlaySettings.cornerRadius)
            putBoolean("show_app_icon", overlaySettings.showAppIcon)
            putBoolean("show_app_name", overlaySettings.showAppName)
            putBoolean("show_app_usage", overlaySettings.showAppUsage)
            putBoolean("is_bubble_visible", overlaySettings.isBubbleVisible)
            apply()
        }
    }

    fun saveLanguage(language: String) {
        settingsPrefs.edit { putString(LANGUAGE_KEY, language) }
    }
    fun getLanguage(): String {
        val language = settingsPrefs.getString(LANGUAGE_KEY, "en")
        if (language == null) {
            val locale = Locale.getDefault().language
            saveLanguage(if (locale == "ar" || locale == "en") locale else "en")
            return locale
        }
        return language
    }

    fun setTheme(theme: String) {
        settingsPrefs.edit {
            putString(THEME_KEY, theme)
        }
    }
    fun getTheme(): String? {
        return settingsPrefs.getString(THEME_KEY, null)
    }

    fun onboardingFinished() {
        settingsPrefs.edit {
            putBoolean("onboarding_finished", true)
        }
    }

    fun isOnboardingFinished(): Boolean {
        return settingsPrefs.getBoolean("onboarding_finished", false)
    }
}

enum class SortType(val value: String) {
    Name("Name"),
    Usage("Usage"),
    UnlockTimes("Unlock Times")
}

enum class Language(val value: String) {
    English("en"),
    Arabic("ar")
}