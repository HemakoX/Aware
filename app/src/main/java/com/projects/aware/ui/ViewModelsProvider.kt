package com.projects.aware.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.projects.aware.data.db.ObjectBoxManager
import com.projects.aware.main.AwareApp
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.ui.screens.overlay.OverlaySettingsViewModel

object ViewModelsProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = (this[APPLICATION_KEY] as AwareApp)
            AppsViewModel(
                objectBoxManager = ObjectBoxManager(app.boxStore),
                appsRepo = app.container.appsRepo,
                preferencesManager = app.container.preferencesManager
            )
        }

        initializer {
            val app = (this[APPLICATION_KEY] as AwareApp)
            OverlaySettingsViewModel(
                preferencesManager = app.container.preferencesManager
            )
        }

        initializer {
            val app = (this[APPLICATION_KEY] as AwareApp)
            SettingsViewModel(
                preferencesManager = app.container.preferencesManager
            )
        }
    }
}