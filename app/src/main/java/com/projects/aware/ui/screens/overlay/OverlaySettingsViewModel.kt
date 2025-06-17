package com.projects.aware.ui.screens.overlay

import android.os.Parcelable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.projects.aware.data.repo.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class OverlaySettingsViewModel(
    private val preferencesManager: PreferencesManager,
): ViewModel() {
    private val _overlaySettings = MutableStateFlow(preferencesManager.getOverlaySettings())
    val overlaySettings = _overlaySettings.asStateFlow()

    fun updateBackgroundColor(color: Color) {
        _overlaySettings.update { it.copy(background = color) }
    }

    fun updateBubbleVisibility(isVisible: Boolean) {
        _overlaySettings.update { it.copy(isBubbleVisible = isVisible) }
        preferencesManager.saveOverlaySettings(_overlaySettings.value)
        saveSettings()
    }

    fun updateCornerRadius(degree: Float) {
        _overlaySettings.update { it.copy(cornerRadius = degree.toInt()) }
    }

    fun updateTextColor(color: Color) {
        _overlaySettings.update { it.copy(textColor = color) }
    }

    fun updateShowAppName(show: Boolean) {
        _overlaySettings.update { it.copy(showAppName = show) }
    }

    fun updateShowAppUsage(show: Boolean) {
        _overlaySettings.update { it.copy(showAppUsage = show) }
    }

    fun updateShowAppIcon(show: Boolean) {
        _overlaySettings.update { it.copy(showAppIcon = show) }
    }

    fun saveSettings() {
        preferencesManager.saveOverlaySettings(_overlaySettings.value)
    }
}

data class OverlaySettings(
    val background: Color = Color.Black,
    val textColor: Color = Color.White,
    val showAppName: Boolean = true,
    val showAppUsage: Boolean = true,
    val cornerRadius: Int = 10,
    val showAppIcon: Boolean = true,
    val isBubbleVisible: Boolean = false,
)


@Parcelize
data class OverlaySettingsParcelize(
    val background: Int = Color.Black.toArgb(),
    val textColor: Int = Color.White.toArgb(),
    val showAppName: Boolean = true,
    val showAppUsage: Boolean = true,
    val cornerRadius: Int = 10,
    val showAppIcon: Boolean = true,
    val isBubbleVisible: Boolean = false,
): Parcelable

fun OverlaySettings.parcelize() = OverlaySettingsParcelize(
    background = this.background.toArgb(),
    textColor = this.textColor.toArgb(),
    showAppIcon = this.showAppIcon,
    showAppUsage = this.showAppUsage,
    showAppName = this.showAppName,
    cornerRadius = this.cornerRadius,
    isBubbleVisible = this.isBubbleVisible
)