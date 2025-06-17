package com.projects.aware.main.settings

import android.R
import android.util.Log
import androidx.compose.material3.ColorScheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.aware.data.repo.Language
import com.projects.aware.data.repo.PreferencesManager
import com.projects.aware.ui.screens.settings.sendEmailFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import kotlin.math.log

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    // Theme state
    private val _theme = MutableStateFlow<AppTheme?>(getDefaultTheme())
    val theme = _theme.asStateFlow()

    // Language state
    private val _language = MutableStateFlow(preferencesManager.getLanguage())
    val language = _language.asStateFlow()


    // Theme props
    fun updateTheme(theme: AppTheme) {
        _theme.update { theme }
        preferencesManager.setTheme(theme.name)
    }

    fun getTheme(theme: AppTheme): ColorScheme {
        return theme.toColorScheme()
    }

    fun getLanguage(abb: String): Language {
        return Language.entries.find { it.value == abb }!!
    }

    fun getDefaultTheme(): AppTheme? {
        return AppTheme.entries.find { it.name == preferencesManager.getTheme() }
    }

    fun updateLanguage(language: Language) {
        _language.update { language.value }
        preferencesManager.saveLanguage(language.value)
    }

    fun sendFeedback(name: String, message: String, onSuccess: () -> Unit, onError: () -> Unit) {
        sendEmailFeedback(
            name = name, message = message,
            onComplete = {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        if (it) {
                            onSuccess()
                        } else {
                            onError()
                        }
                    }
                }
            })
    }
}