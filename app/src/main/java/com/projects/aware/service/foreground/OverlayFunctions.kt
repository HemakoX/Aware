package com.projects.aware.service.foreground

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat

fun startMonitoring(context: Context): Boolean {
    val intent = Intent(context, OverlayService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ requires user interaction to start foreground services
        if (isAppInForeground(context)) {
            ContextCompat.startForegroundService(context, intent)
            return true
        } else {
            Toast.makeText(
                context,
                "Please bring the app to the foreground to start monitoring",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    } else {
        // Legacy behavior for older Androids
        ContextCompat.startForegroundService(context, intent)
        return true
    }
}

private fun isAppInForeground(context: Context): Boolean {
    val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val runningProcs = activityManager.runningAppProcesses ?: return false
    return runningProcs.any {
        it.processName == context.packageName &&
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
}


fun isServiceRunning(context: Context, serviceClass: Class<*> = OverlayService::class.java): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (service.service.className == serviceClass.name) return true
    }
    return false
}


fun restartService(context: Context) {
    if (isServiceRunning(context)) {
        context.stopService(Intent(context, OverlayService::class.java))
        startMonitoring(context)
    } else {
        startMonitoring(context)
    }
}