package com.projects.aware.data.db

import com.projects.aware.data.model.ExcludedApp
import com.projects.aware.data.model.LimitAppUsage
import io.objectbox.Box
import io.objectbox.BoxStore

class ObjectBoxManager(boxStore: BoxStore) {
    val excludedAppBox: Box<ExcludedApp?> = boxStore.boxFor(ExcludedApp::class.java)
    val limits: Box<LimitAppUsage> = boxStore.boxFor(LimitAppUsage::class.java)

    fun addApp(excludedApp: ExcludedApp, onSuccess: () -> Unit) {
        try {
            excludedAppBox.put(excludedApp)
            onSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllApps(): List<ExcludedApp?> {
        val apps = excludedAppBox.all
        return apps
    }

    fun getAppByPackage(pkg: String): ExcludedApp? {
        val apps = excludedAppBox.all
        return apps.find { it?.packageName == pkg }
    }

    fun deleteApp(pkg: String, onSuccess: (ExcludedApp) -> Unit) {
        val app = getAppByPackage(pkg)
        app?.let {
            excludedAppBox.remove(it)
            onSuccess.invoke(it)
        }
    }

    fun addLimit(appPackageName: String, limit: Long, onSuccess: () -> Unit) {
        try {
            if (limits.all.find { it?.packageName == appPackageName } == null) {
                val limitAppUsage = LimitAppUsage(packageName = appPackageName, dailyLimit =  limit)
                limits.put(limitAppUsage)
                onSuccess()
            } else {
                var limitAppUsage = limits.all.find { it?.packageName == appPackageName }!!
                val newLimit = limitAppUsage.copy(id = limitAppUsage.id, dailyLimit = limit)
                limits.put(newLimit)
            }
            onSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteLimit(pkg: String, onSuccess: () -> Unit) {
        val limit = limits.all.find { it?.packageName == pkg }
        limit?.let {
            limits.remove(it)
            onSuccess.invoke()
        }
    }
}


sealed class LimitationState() {
    object NoLimitation: LimitationState()
    class Limited(val limit: Long): LimitationState()
}