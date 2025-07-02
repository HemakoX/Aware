package com.projects.aware.main.settings

import androidx.compose.material3.ColorScheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.aware.data.repo.Language
import com.projects.aware.data.repo.PreferencesManager
import com.projects.aware.ui.screens.settings.PasswordDialogType
import com.projects.aware.ui.screens.settings.sendEmailFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _awareSettings = MutableStateFlow(
        Settings(
            isAppDisabled = preferencesManager.getDisabilityState(),
            theme = getDefaultTheme(),
            passwordSettings = PasswordProps(
                isPasswordSet = isPinSet(),
                error = null
            ),
            language = preferencesManager.getLanguage()
        )
    )
    val awareSettings = _awareSettings.asStateFlow()

    fun createPin(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        if (password.length == 4) {
            preferencesManager.savePassword(password)
            onSuccess()
        } else {
            onError()
        }
    }

    fun verifyPin(password: String, onSuccess: () -> Unit = {}, onError: () -> Unit = {}): Boolean {
        val storedPassword = preferencesManager.getPassword() == password && preferencesManager.getPassword() != null
        return if (storedPassword || password == preferencesManager.getRecoveryPassword()) {
            _awareSettings.update {
                it.copy(
                    passwordSettings = it.passwordSettings.copy(
                        error = null,
                        isPasswordSet = true,
                        isAuth = true
                    )
                )
            }
            onSuccess()
            true
        } else {
            _awareSettings.update {
                it.copy(
                    passwordSettings = it.passwordSettings.copy(
                        error = "Incorrect password",
                    )
                )
            }
            onError()
            false
        }
    }

    fun changePin(oldPin: String, newPin: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (verifyPin(oldPin)) {
            preferencesManager.savePassword(newPin)
            _awareSettings.update {
                it.copy(
                    passwordSettings = it.passwordSettings.copy(isPasswordSet = true, error = null)
                )
            }
            onSuccess()
        } else {
            onFailure()
        }
    }

    fun removePin(currentPin: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (verifyPin(currentPin)) {
            preferencesManager.savePassword(null)
            _awareSettings.update {
                it.copy(
                    passwordSettings = it.passwordSettings.copy(isPasswordSet = false, error = null)
                )
            }
            onSuccess()
        } else {
            onFailure()
        }
    }

    fun isPinSet(): Boolean {
        return preferencesManager.getPassword() != null
    }

    fun setNewDialogType(type: PasswordDialogType) {
        _awareSettings.update {
            it.copy(
                passwordSettings = it.passwordSettings.copy(dialogType = type)
            )
        }
    }

    // Theme props
    fun updateTheme(theme: AppTheme) {
        _awareSettings.update {
            it.copy(
                theme = theme
            )
        }
        preferencesManager.setTheme(theme.name)
    }

    fun getTheme(theme: AppTheme): ColorScheme {
        return theme.toColorScheme()
    }


    fun setDisabilityState(state: Boolean) {
        preferencesManager.setDisabilityState(state)
        _awareSettings.update {
            it.copy(
                isAppDisabled = state
            )
        }
    }

    private fun getDefaultTheme(): AppTheme? {
        return AppTheme.entries.find { it.name == preferencesManager.getTheme() }
    }

    fun updateLanguage(language: Language) {
        _awareSettings.update {
            it.copy(
                language = language.value
            )
        }
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

data class Settings(
    val isAppDisabled: Boolean = false,
    val passwordSettings: PasswordProps,
    val language: String,
    val theme: AppTheme? = null,
)

data class PasswordProps(
    val isPasswordSet: Boolean,
    val error: String? = null,
    val dialogType: PasswordDialogType = PasswordDialogType.NONE,
    val isAuth: Boolean = false,
)