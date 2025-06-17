package com.projects.aware.data.repo

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar

class AppsRepo(
    private val context: Context
) {
    fun getAppsStats(): Map<String, CustomUsageStats> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        // Calculate time range (now - 24 hours)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val now = System.currentTimeMillis()

        return try {
            val usageMap = mutableMapOf<String, CustomUsageStats>()
            val activeSessions = mutableMapOf<String, Pair<Long, UsageEvents.Event>>() // package to (startTime, event)
            val events = usageStatsManager.queryEvents(startOfDay, now) ?: return emptyMap()

            val event = UsageEvents.Event()
            while (events.hasNextEvent()) {
                events.getNextEvent(event)

                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        // Handle new foreground session
                        activeSessions[event.packageName] = Pair(event.timeStamp, event)
                    }
                    UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                        // Complete existing session
                        activeSessions.remove(event.packageName)?.let { (startTime, foregroundEvent) ->
                            if (foregroundEvent.packageName == event.packageName) {
                                val duration = event.timeStamp - startTime

                                // Only track sessions longer than 3 seconds
                                if (duration > 3000) {
                                    val stats = usageMap.getOrPut(event.packageName) {
                                        CustomUsageStats(packageName = event.packageName)
                                    }

                                    stats.apply {
                                        sessions.add(Session(startTime, event.timeStamp, duration))
                                        totalForegroundTime += duration
                                        lastUsed = maxOf(lastUsed, event.timeStamp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            usageMap
        } catch (e: SecurityException) {
            emptyMap() // Handle missing permission gracefully
        }

    }

    fun getConsolidatedAppUsage(stats: Map<String, CustomUsageStats>): Flow<List<App>> = flow {
        val pm = context.packageManager

        val installedPackages = withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_META_DATA.toLong()))
            } else {
                pm.getInstalledPackages(PackageManager.GET_META_DATA)
            }
        }


        val apps = withContext(Dispatchers.Default) {
            installedPackages.mapNotNull { pkg ->
                async {
                    try {
                        val packageName = pkg.packageName
                        if (!isLauncherApp(pm, packageName)) return@async null

                        val usageTime = stats[packageName]?.totalForegroundTime ?: 0L
                        val sessions = stats[packageName]?.sessions ?: emptyList()

                        // Create App object for each valid package
                        App(
                            name = pkg.applicationInfo?.loadLabel(pm).toString(),
                            packageName = packageName,
                            icon = null, // Lazy icon loading
                            usageTime = usageTime,
                            unlockTimes = sessions.size,
                            sessions = sessions
                        )
                    } catch (e: Exception) {
                        null // Skip problematic packages
                    }
                }
            }.awaitAll().filterNotNull()
        }
        emit(apps)

    }.flowOn(Dispatchers.IO)

    suspend fun getUsedAppsUsage(stats: Map<String, CustomUsageStats>): List<App> {
        val pm = context.packageManager

        return withContext(Dispatchers.IO) {
            pm.getInstalledPackages(PackageManager.GET_META_DATA)
                .mapNotNull { pkg ->
                    try {
                        // Sum usage for this package
                        val usageTime = stats.values
                            .filter {
                                it.packageName == pkg.packageName
                            }
                            .sumOf { it.totalForegroundTime }
                        val sessions = stats.values
                            .filter { it.packageName == pkg.packageName }
                            .map { it.sessions }
                        if (isLauncherApp(pm, pkg.packageName)) {
                            App(
                                name = pkg.applicationInfo!!.loadLabel(pm).toString(),
                                packageName = pkg.packageName,
                                icon = pkg.applicationInfo!!.loadIcon(pm),
                                usageTime = usageTime,
                                unlockTimes = sessions.size,
                                sessions = sessions.flatten()
                            )
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null // Skip problematic packages
                    }

                }
        }

    }

    suspend fun getTotalUsage(stats: Map<String, CustomUsageStats>): Long {
        val pm = context.packageManager

        return withContext(Dispatchers.IO) {
            pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                .mapNotNull { pkg ->
                    stats.values
                        .filter {
                            it.packageName == pkg.packageName
                        }
                        .sumOf { it.totalForegroundTime }
                }.sumOf { it }
        }
    }

    suspend fun getAverageAppUsage(packageName: String): Long {
        return withContext(Dispatchers.Default) {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000L * 60 * 60 * 24 * 7  // 7 days ago

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_WEEKLY,
                startTime,
                endTime
            )

            val totalUsage = stats.find { it.packageName == packageName }?.totalTimeInForeground ?: 0L

            totalUsage / 7  // Average per day in milliseconds
        }
    }

}



data class Session(
    val start: Long,
    val end: Long,
    val duration: Long,
)

data class App(
    val name: String,
    val packageName: String,
    val icon: Drawable?,
    val usageTime: Long,
    val unlockTimes: Int,
    val averageUsage: Long? = null,
    val sessions: List<Session?> = listOf(),
)


fun isLauncherApp(packageManager: PackageManager, packageName: String?): Boolean {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfos = packageManager.queryIntentActivities(intent, 0)
    return resolveInfos.any { it.activityInfo.packageName == packageName }
}




fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

data class CustomUsageStats(
    val packageName: String,
    var totalForegroundTime: Long = 0L,
    var sessions: MutableList<Session?> = mutableListOf(),
    var lastUsed: Long = 0L
)
