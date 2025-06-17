@file:OptIn(ExperimentalMaterial3Api::class)

package com.projects.aware.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projects.aware.R
import com.projects.aware.data.repo.App
import com.projects.aware.data.repo.SortType
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.ui.AppsViewModel
import com.projects.aware.ui.components.BottomNavTab
import com.projects.aware.ui.components.CustomAnimatedBottomNavBar
import com.projects.aware.ui.screens.overlay.OverlaySettingsScreen
import com.projects.aware.ui.screens.overlay.OverlaySettingsViewModel
import com.projects.aware.ui.screens.settings.SettingsScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    appsViewModel: AppsViewModel,
    onAppClick: (App) -> Unit,
    themeViewModel: SettingsViewModel,
    homeScreenNavController: NavHostController = rememberNavController(),
    overlaySettingsViewModel: OverlaySettingsViewModel,
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.AppsList) }
    Scaffold(
        bottomBar = {
            CustomAnimatedBottomNavBar(
                selectedTab = selectedTab
            ) { selectedTap ->
                selectedTab = selectedTap
                homeScreenNavController.navigate(selectedTap.name) {
                    popUpTo(selectedTap.name) { inclusive = true }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            navController = homeScreenNavController,
            startDestination = BottomNavTab.AppsList.name
        ) {
            composable(BottomNavTab.AppsList.name) {
                AppsListScreen(
                    appsViewModel = appsViewModel,
                    onAppClick = onAppClick,
                )
            }
            composable(BottomNavTab.BubbleCustom.name) {
                OverlaySettingsScreen(overlaySettingsViewModel)
            }
            composable(BottomNavTab.Settings.name) {
                SettingsScreen(
                    settingsViewModel = themeViewModel,
                    back = { selectedTab = BottomNavTab.AppsList }
                )
            }
        }
    }
}


@Composable
fun PullToRefreshCustomIndicatorSample(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
    ) {
        content()
    }
}


@Composable
fun DropDownSorting(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    currentSort: String,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val currentSortType = when (currentSort) {
                SortType.Name.name -> stringResource(R.string.name)
                SortType.Usage.name -> stringResource(R.string.usage)
                SortType.UnlockTimes.name -> stringResource(R.string.unlock_times)
                else -> ""
            }

            Text(
                text = currentSortType,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    modifier = Modifier.size(30.dp),
                    contentDescription = "Sorting Menu",
                )
                val menuColors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.onSurface,
                    trailingIconColor = MaterialTheme.colorScheme.onSurface
                )
                AnimatedVisibility(isExpanded) {
                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            stringResource(R.string.sort_by),
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        HorizontalDivider(
                            Modifier.padding(4.dp),
                            thickness = 2.dp
                        )
                        DropdownMenuItem(
                            colors = menuColors,
                            text = {
                                Text(stringResource(R.string.name))
                            },
                            leadingIcon = {
                                if (currentSort == SortType.Name.name) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    Icon(Icons.Filled.Abc, contentDescription = null)
                                }
                            },
                            onClick = {
                                isExpanded = false
                                onClick(SortType.Name.name)
                            })
                        DropdownMenuItem(
                            colors = menuColors,
                            text = {
                                Text(stringResource(R.string.usage))
                            },
                            leadingIcon = {
                                if (currentSort == SortType.Usage.name) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    Icon(Icons.Filled.Timer, contentDescription = null)
                                }

                            },
                            onClick = {
                                isExpanded = false
                                onClick(SortType.Usage.name)
                            }
                        )
                        DropdownMenuItem(
                            colors = menuColors,
                            text = {
                                Text(stringResource(R.string.unlock_times))
                            },
                            leadingIcon = {
                                if (currentSort == SortType.UnlockTimes.name) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    Icon(Icons.Filled.LockOpen, contentDescription = null)
                                }
                            },
                            onClick = {
                                isExpanded = false
                                onClick(SortType.UnlockTimes.name)
                            }
                        )

                    }
                }
            }

        }
    }
}

