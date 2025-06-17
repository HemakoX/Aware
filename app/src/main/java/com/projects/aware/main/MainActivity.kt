@file:OptIn(ExperimentalMaterial3Api::class)

package com.projects.aware.main

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projects.aware.data.repo.PreferencesManager
import com.projects.aware.main.settings.LanguageManager
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.ui.ViewModelsProvider
import com.projects.aware.ui.theme.AwareTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        runBlocking {
            val prefs = PreferencesManager(newBase)
            val lang = prefs.getLanguage()
            val updatedContext = LanguageManager.setLocale(newBase, lang)
            super.attachBaseContext(updatedContext)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: SettingsViewModel = viewModel(
                factory = ViewModelsProvider.Factory
            )
            AwareTheme(themeViewModel) {
                AppEntry(settingsViewModel = themeViewModel)
            }
        }

    }

}



fun openMIUIPermissionEditor(context: Context) {
    try {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setPackage("com.miui.securitycenter")
            intent.putExtra("extra_pkgname", context.packageName)
            context.startActivity(intent)
        } catch (e1: Exception) {
            // If MIUI settings are not available, fallback to app settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }
    }
}


fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        .any { it.resolveInfo.serviceInfo.packageName == context.packageName }
}

fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}