package com.projects.aware.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.aware.data.db.ObjectBoxManager
import com.projects.aware.data.model.ExcludedApp
import com.projects.aware.data.model.LimitAppUsage
import com.projects.aware.data.repo.App
import com.projects.aware.data.repo.AppsRepo
import com.projects.aware.data.repo.PreferencesManager
import com.projects.aware.data.repo.SortType
import com.projects.aware.ui.components.segmentedButtons.HomeSegments
import com.projects.aware.ui.components.segmentedButtons.SegmentedButtonProp
import com.projects.aware.ui.components.segmentedButtons.SegmentedButtonsOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsViewModel(
    private val objectBoxManager: ObjectBoxManager,
    private val appsRepo: AppsRepo,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _uiApps = MutableStateFlow(UIAppsState())
    val apps = _uiApps.asStateFlow()
    private val _excludedApps = MutableStateFlow(ExcludedAppsState())
    val excludedApps = _excludedApps.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _limits = MutableStateFlow(LimitsState())
    val limits = _limits.asStateFlow()
    val queryState = MutableStateFlow("")
    val homeSegmentedButton = MutableStateFlow(SegmentedButtonsOptions.homeOptions.first())
    val sortTypeState = MutableStateFlow(preferencesManager.getSortType())

    init {
        getApps()
        getUiApps()
        getTotalUsageTime()
    }

    fun updateState(excludedApp: ExcludedApp) {
        _excludedApps.update {
            it.copy(
                apps = it.apps.map { app -> if (app?.packageName == excludedApp.packageName) excludedApp else app }
            )
        }
    }

    fun clearState() {
        _uiApps.update { it.copy(uiApps = emptyList(), allApps = emptyList()) }
    }

    fun getTotalUsageTime() {
        viewModelScope.launch {
            val stats = appsRepo.getAppsStats()
            val totalUsageTime = appsRepo.getTotalUsage(stats)
            _uiApps.update {
                it.copy(
                    totalUsageTime = totalUsageTime
                )
            }
        }
    }


    fun getUiApps() {
        viewModelScope.launch {
            _loading.update { true }
            clearState()
            val stats = appsRepo.getAppsStats()
            val flow = appsRepo.getConsolidatedAppUsage(stats)
            flow.collect { appsList ->
                _uiApps.update {
                    it.copy(
                        uiApps = if (homeSegmentedButton.value.storeName == HomeSegments.All.name) appsList else appsList.filter { it.usageTime > 0 },
                        allApps = appsList,
                    )
                }
                sortAppsBy(sortTypeState.value.name)
                _loading.update { false }
            }

        }
    }


    fun getApps() {
        viewModelScope.launch {
            _excludedApps.update {
                it.copy(
                    apps = objectBoxManager.excludedAppBox.all
                )
            }
            _limits.update {
                it.copy(
                    limits = objectBoxManager.limits.all
                )
            }
        }
    }

    fun sortAppsBy(method: String) {
        viewModelScope.launch {
            val sortType = SortType.valueOf(method)
            preferencesManager.saveSortType(sortType)
            withContext(Dispatchers.Default) {
                _uiApps.update {
                    it.copy(
                        uiApps = when (sortType) {
                            SortType.Name -> it.uiApps.sortedBy { it.name }
                            SortType.Usage -> it.uiApps.sortedByDescending { it.usageTime }
                            SortType.UnlockTimes -> it.uiApps.sortedByDescending { it.sessions.size }
                        }
                    )
                }
                sortTypeState.update { sortType }
            }
        }
    }

    fun switchSegmentedHomeApps(segmented: SegmentedButtonProp) {
        if (segmented == homeSegmentedButton.value) return
        homeSegmentedButton.update { segmented }

        if (homeSegmentedButton.value.storeName == HomeSegments.All.name) {
            switchToAll()
        } else if (homeSegmentedButton.value.storeName == HomeSegments.Used.name) {
            switchToUsed()
        } else if (homeSegmentedButton.value.storeName == HomeSegments.Limited.name) {
            switchToLimitedApps()
        }
        sortAppsBy(sortTypeState.value.name)
    }

    private fun switchToAll() {
        _uiApps.update {
            it.copy(
                uiApps = it.allApps
            )
        }
    }

    private fun switchToLimitedApps() {
        val limits = objectBoxManager.limits.all
        _uiApps.update {
            it.copy(
                uiApps = it.allApps.filter {
                    limits.any { limit -> limit?.packageName == it.packageName }
                }
            )
        }
    }

    private fun switchToUsed() {
        _uiApps.update {
            it.copy(
                uiApps = it.allApps.filter { it.usageTime > 0 }
            )
        }
    }

    fun searchQuery(query: String) {
        queryState.update { query }
        viewModelScope.launch(Dispatchers.Default) {
            if (queryState.value.isBlank()) {
                if (homeSegmentedButton.value.storeName == HomeSegments.All.name) {
                    switchToLimitedApps()
                } else if (homeSegmentedButton.value.storeName == HomeSegments.Used.name) {
                    switchToUsed()
                } else if (homeSegmentedButton.value.storeName == HomeSegments.Limited.name) {
                    switchToLimitedApps()
                }
            } else {
                _uiApps.update {
                    it.copy(
                        uiApps = it.allApps.filter { app ->
                            app.name.contains(
                                query,
                                ignoreCase = true
                            )
                        }
                    )
                }
            }
            sortAppsBy(sortTypeState.value.name)
        }
    }

    fun updateSearchFocus(isFocused: Boolean) {
        if (!isFocused) {
            searchQuery("")
        }
    }

    fun updateCurrentApp(app: App) {
        _uiApps.update {
            it.copy(
                currentApp = app
            )
        }
    }

    fun updateAppData(newApp: App?) {
        if (newApp == null) return
        viewModelScope.launch(Dispatchers.Default) {
            _uiApps.update {
                val newList = it.allApps.toMutableList()
                val newUiList = it.uiApps.toMutableList()

                val index = newList.indexOfFirst { app -> app.packageName == newApp.packageName }
                val uiIndex =
                    newUiList.indexOfFirst { app -> app.packageName == newApp.packageName }

                if (uiIndex != -1) newUiList[uiIndex] = newApp
                if (index != -1) newList[index] = newApp

                it.copy(
                    allApps = newList, uiApps = newUiList,
                    currentApp = if (newApp.packageName == it.currentApp?.packageName) newApp else it.currentApp
                )
            }
        }
    }


    fun updateTrackingAppState(pkg: String, countable: Boolean) {
        if (countable) {
            objectBoxManager.deleteApp(pkg) {
                updateState(it)
            }
        } else {
            if (objectBoxManager.getAppByPackage(pkg) == null) {
                val newApp = ExcludedApp(packageName = pkg)
                objectBoxManager.addApp(newApp) {
                    updateState(newApp)
                }
            }
        }
    }

    fun isAppTrackable(pkg: String): Boolean {
        return objectBoxManager.getAppByPackage(pkg) == null
    }

    fun getAverageAppUsage(pkg: String) {
        viewModelScope.launch {
            val averageUsage = appsRepo.getAverageAppUsage(pkg)
            updateAppData(
                _uiApps.value.allApps.find { it.packageName == pkg }?.copy(
                    averageUsage = averageUsage
                )
            )
        }
    }

    fun addLimit(appPackage: String, limit: Long) {
        objectBoxManager.addLimit(appPackage, limit) {
            _limits.update {
                it.copy(
                    limits = objectBoxManager.limits.all
                )
            }
        }
    }

    fun removeLimit(appPackage: String) {
        objectBoxManager.deleteLimit(appPackage) {
            _limits.update {
                it.copy(
                    limits = objectBoxManager.limits.all
                )
            }
        }
    }

    fun startApp() {
        preferencesManager.onboardingFinished()
    }

    fun isOnboardingFinished(): Boolean {
        return preferencesManager.isOnboardingFinished()
    }
}

data class UIAppsState(
    val uiApps: List<App> = listOf(),
    val allApps: List<App> = listOf(),
    val totalUsageTime: Long = 0,
    val currentApp: App? = null
)

data class ExcludedAppsState(
    val apps: List<ExcludedApp?> = listOf(),
)

data class LimitsState(
    val limits: List<LimitAppUsage?> = emptyList()
)
