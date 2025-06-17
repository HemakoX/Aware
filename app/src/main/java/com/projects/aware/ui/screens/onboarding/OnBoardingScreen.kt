package com.projects.aware.ui.screens.onboarding

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.projects.aware.R
import com.projects.aware.main.settings.AppTheme
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.main.settings.restartApp
import com.projects.aware.ui.screens.overlay.OverlaySettings
import com.projects.aware.ui.screens.overlay.OverlaySettingsViewModel
import com.projects.aware.ui.screens.overlay.getAdjustedContrast
import com.projects.aware.ui.screens.settings.LanguageSettings
import com.projects.aware.ui.screens.settings.ThemeSettings
import kotlinx.coroutines.launch


@Composable
fun OnboardingScreen(settingsViewModel: SettingsViewModel,overlaySettingsViewModel: OverlaySettingsViewModel, onFinish: () -> Unit) {
    val context = LocalContext.current
    val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
    var currentTheme by remember { mutableStateOf(theme) }
    val language by settingsViewModel.language.collectAsStateWithLifecycle()
    var currentLanguage by remember { mutableStateOf(settingsViewModel.getLanguage(language)) }
    val settings by overlaySettingsViewModel.overlaySettings.collectAsStateWithLifecycle()
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.welcome_to) + " Aware",
            description = stringResource(R.string.take_control_of_your_screen_time_Track_reflect_and_build_better_habits),
            animationRes = R.raw.welcome,
            pageType = PageType.WELCOME
        ),
        OnboardingPage(
            title = stringResource(R.string.live_screen_time_tracking),
            description = "Aware" + stringResource(R.string.quietly_tracks_which_app_you_are_using_and_how_long_you_are_spending_on_it_live_),
            animationRes = R.raw.tracking,
            pageType = PageType.TRACKING
        ),
        OnboardingPage(
            title = stringResource(R.string.set_daily_limit),
            description = stringResource(R.string.set_daily_limits_for_distracting_apps_we_will_remind_you_or_block_access_when_needed),
            animationRes = R.raw.limits,
            pageType = PageType.LIMITS
        ),
        OnboardingPage(
            title = "",
            description = "",
            space = 10.dp,
            animationRes = null,
            pageType = PageType.BUBBLE,
            additionalContent = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(settings.cornerRadius))
                        .background(
                            settings.background,
                            // Subtle shadow for depth
                            shape = RoundedCornerShape(settings.cornerRadius)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.background.getAdjustedContrast(),
                            shape = RoundedCornerShape(settings.cornerRadius)
                        )

                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        // App icon with smooth visibility animation
                        AnimatedVisibility(
                            visible = settings.showAppIcon,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Image(
                                painter = painterResource(R.drawable.play_store_512),
                                contentDescription = stringResource(R.string.app_icon),
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // App info with better typography
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AnimatedVisibility(
                                visible = settings.showAppName,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text(
                                    text = "Aware",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = settings.textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            AnimatedVisibility(
                                visible = settings.showAppUsage,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text(
                                    text = "0m 0s",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = settings.textColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.bubble_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Text(
                    text = stringResource(R.string.bubble_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.padding(10.dp))
                Text(
                    text = stringResource(R.string.bubble_privacy_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.bubble_privacy_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.isBubbleVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = if (settings.isBubbleVisible) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    onClick = {
                        if (settings.isBubbleVisible) {
                            overlaySettingsViewModel.updateBubbleVisibility(false)
                            val intent = Intent("com.aware.actions.OverlayVisibility").apply {
                                putExtra("is_visible", false)
                            }
                            context.sendBroadcast(intent)
                        } else {
                            overlaySettingsViewModel.updateBubbleVisibility(true)
                            val intent = Intent("com.aware.actions.OverlayVisibility").apply {
                                putExtra("is_visible", true)
                            }
                            context.sendBroadcast(intent)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (settings.isBubbleVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = if (settings.isBubbleVisible) stringResource(R.string.activated) else stringResource(R.string.disabled)
                    )

                }
            }

        ),
        OnboardingPage(
            title = stringResource(R.string.default_settings),
            description = stringResource(R.string.customize_your_default_settings),
            animationRes = R.raw.settings,
            pageType = PageType.SETTINGS,
            additionalContent = {
                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                ) {
                    item {
                        ThemeSettings(
                            onThemeChange = {
                                settingsViewModel.updateTheme(it)
                            },
                            currentTheme = currentTheme!!
                        )
                    }
                    item {
                        LanguageSettings(
                            onLanguageChange = {
                                settingsViewModel.updateLanguage(it)
                                restartApp(context)
                            },
                            currentLanguage = currentLanguage.value
                        )
                    }
                }
            }
        ),
        OnboardingPage(
            title = stringResource(R.string.permissions_needed),
            description = stringResource(R.string.permissions_onboarding),
            animationRes = R.raw.permissions2,
            pageType = PageType.PERMISSION
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    LocalDensity.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Animated pager indicator
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        val width by animateDpAsState(
                            targetValue = if (pagerState.currentPage == index) 24.dp else 8.dp,
                            animationSpec = tween(durationMillis = 300)
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation buttons with animated transitions
                AnimatedContent(
                    targetState = pagerState.currentPage,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    },
                    label = "OnboardingButtons"
                ) { currentPage ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (currentPage > 0) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(currentPage - 1)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            ) {
                                Text(stringResource(R.string.back))
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Button(
                            onClick = {
                                if (currentPage == pages.lastIndex) {
                                    onFinish()
                                } else {
                                    scope.launch {
                                        pagerState.animateScrollToPage(currentPage + 1)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                if (currentPage == pages.lastIndex)
                                    stringResource(R.string.get_started)
                                else
                                    stringResource(R.string.next)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) { page ->
            OnboardingPageUI(page = pages[page])
        }
    }
}

@Composable
fun OverlayPermissionCard(
    modifier: Modifier = Modifier,
    isGranted: Boolean,
    onGrant: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.padding(10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Layers,
                contentDescription = null,
                modifier = modifier
                    .padding(5.dp)
                    .size(60.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                stringResource(R.string.accept_overlay_permission_to_start_aware_panel),
                modifier = modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,

                )
            Spacer(modifier.padding(5.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isGranted) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = modifier
                            .padding(5.dp)
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                    )
                    Text(
                        stringResource(R.string.permission_is_granted),
                        modifier = modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onClick = onGrant,
                    ) {
                        Text(
                            stringResource(R.string.grant),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsagePermissionCard(modifier: Modifier = Modifier, isGranted: Boolean, onGrant: () -> Unit) {
    LocalContext.current
    OutlinedCard(
        modifier = Modifier.padding(10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.DataUsage,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(50.dp)
            )
            Text(
                stringResource(R.string.accept_usage_monitoring_permission_to_start),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )

            if (isGranted) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = modifier.background(MaterialTheme.colorScheme.secondary, CircleShape)
                )
                Text(
                    stringResource(R.string.permission_is_granted),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            } else {
                Button(
                    onClick = onGrant,
                    modifier = Modifier.fillMaxWidth(0.5f),
                ) {
                    Text(
                        stringResource(R.string.grant),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

}