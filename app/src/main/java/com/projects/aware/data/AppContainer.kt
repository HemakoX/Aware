package com.projects.aware.data

import android.content.Context
import com.projects.aware.data.repo.AppsRepo
import com.projects.aware.data.repo.PreferencesManager

interface AppContainer {
    val appsRepo: AppsRepo
    val preferencesManager: PreferencesManager
}


class DefaultAppContainer(
    private val context: Context,
): AppContainer {
    override val appsRepo: AppsRepo by lazy {
        AppsRepo(context)
    }
    override val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(context)
    }
}