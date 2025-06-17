@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.projects.aware.main

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projects.aware.R
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.ui.AppsViewModel
import com.projects.aware.ui.ViewModelsProvider
import com.projects.aware.ui.screens.app.AppDetailScreen
import com.projects.aware.ui.screens.home.HomeScreen
import com.projects.aware.ui.screens.onboarding.OnboardingScreen
import com.projects.aware.ui.screens.overlay.OverlaySettingsViewModel
import com.projects.aware.ui.screens.splash.SplashScreen

@Composable
fun AppEntry(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    appsViewModel: AppsViewModel = viewModel(factory = ViewModelsProvider.Factory),
    settingsViewModel: SettingsViewModel,
    overlaySettingsViewModel: OverlaySettingsViewModel = viewModel(factory = ViewModelsProvider.Factory)
) {
    // states
    val appsState by appsViewModel.apps.collectAsStateWithLifecycle()
    val appLimitsState by appsViewModel.limits.collectAsStateWithLifecycle()

    // Components
    Scaffold { innerPadding ->
        NavHost(
            modifier = modifier.padding(innerPadding),
            navController = navHostController,
            startDestination = Route.Splash.route,
        ) {
            composable(route = Route.Splash.route) {
                OnboardingScreen(
                    settingsViewModel = settingsViewModel,
                    overlaySettingsViewModel = overlaySettingsViewModel
                ) {
                    appsViewModel.startApp()
                    navHostController.navigate(Route.Home.route) {
                        popUpTo(Route.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(route = Route.Splash.route) {
                SplashScreen {
                    val isOnboardingFinished = appsViewModel.isOnboardingFinished()
                    navHostController.navigate(if (isOnboardingFinished) Route.Home.route else Route.Onboarding.route) {
                        popUpTo(Route.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            }

            composable(route = Route.Home.route) {
                HomeScreen(
                    onAppClick = { app ->
                        appsViewModel.updateCurrentApp(app)
                        navHostController.navigate(Route.App.route)
                    },
                    appsViewModel = appsViewModel,
                    themeViewModel = settingsViewModel,
                    overlaySettingsViewModel = overlaySettingsViewModel
                )
            }

            composable(route = Route.App.route) {
                val app = appsState.currentApp
                AppDetailScreen(
                    app = app!!,
                    onBack = {
                        navHostController.popBackStack()
                    },
                    appsViewModel = appsViewModel,
                    limitsState = appLimitsState
                )
            }
        }
    }

}


sealed class Route(val route: String) {
    object Home : Route("Home")
    object App : Route("App")
    object Splash : Route("Splash")
    object Settings : Route("Settings")
    object Onboarding : Route("Onboarding")
}