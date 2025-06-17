@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.projects.aware.ui.screens.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.projects.aware.R
import com.projects.aware.data.model.LimitAppUsage
import com.projects.aware.data.repo.App
import com.projects.aware.data.repo.SortType
import com.projects.aware.data.repo.formatDuration
import com.projects.aware.main.hasUsageStatsPermission
import com.projects.aware.ui.AppsViewModel
import com.projects.aware.ui.components.segmentedButtons.CustomButtonGroup
import com.projects.aware.ui.components.segmentedButtons.SegmentedButtonsOptions
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppsListScreen(
    appsViewModel: AppsViewModel,
    modifier: Modifier = Modifier,
    onAppClick: (App) -> Unit,
) {
    // states
    val context = LocalContext.current
    val apps by appsViewModel.apps.collectAsStateWithLifecycle()
    val limits by appsViewModel.limits.collectAsStateWithLifecycle()
    val loading by appsViewModel.loading.collectAsStateWithLifecycle()
    val sortType by appsViewModel.sortTypeState.collectAsStateWithLifecycle()
    val query by appsViewModel.queryState.collectAsStateWithLifecycle()

    // packages queries permission
    var usagePermission by remember { mutableStateOf(hasUsageStatsPermission(context)) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            usagePermission = hasUsageStatsPermission(context)
            if (usagePermission) {
                appsViewModel.getUiApps()
                appsViewModel.getTotalUsageTime()
            }
        }

    PullToRefreshCustomIndicatorSample(
        isRefreshing = loading,
        onRefresh = {
            appsViewModel.getApps()
            appsViewModel.getUiApps()
            appsViewModel.getTotalUsageTime()
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = modifier.fillMaxWidth()
                        ) {
                            // Search and Filter Section (Sticky Header)
                            SearchBar(
                                query = query,
                                onQueryChange = { appsViewModel.searchQuery(it) },
                                modifier = Modifier.weight(1f),
                            )
                            DropDownSorting(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                currentSort = sortType.name,
                                onClick = { appsViewModel.sortAppsBy(it) }
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            if (usagePermission) {
                AppList(
                    appsViewModel = appsViewModel,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    apps = apps.uiApps,
                    onAppClick = onAppClick,
                    limits = limits.limits,
                    loading = loading,
                    totalUsageTime = apps.totalUsageTime
                )
            } else {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    OutlinedCard(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DataUsage,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                stringResource(R.string.accept_usage_monitoring_permission_to_start),
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(10.dp)
                                    .fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    val intent =
                                        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                            data =
                                                "package:${context.packageName}".toUri()  // Directly target your app
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                    launcher.launch(intent)
                                },
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
        }
    }
}


fun extractDominantColor(context: Context, packageName: String): Color {
    return try {
        val drawable = context.packageManager.getApplicationIcon(packageName)
        val bitmap = drawable.toBitmap(32, 32) // small for memory
        val palette = Palette.from(bitmap).generate()
        val rgb = palette.getDominantColor(Color.Gray.toArgb()) // fallback
        bitmap.recycle() // free memory
        Color(rgb)
    } catch (e: Exception) {
        Color.Gray
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppList(
    modifier: Modifier = Modifier,
    appsViewModel: AppsViewModel,
    onAppClick: (App) -> Unit,
    loading: Boolean,
    totalUsageTime: Long = 0L,
    limits: List<LimitAppUsage?>,
    apps: List<App?>,
) {
    val query by appsViewModel.queryState.collectAsStateWithLifecycle()
    val currentHomeSegment by appsViewModel.homeSegmentedButton.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {

        // Apps List Content
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(15.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.today_usage),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            formatDuration(totalUsageTime),
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                    CustomButtonGroup(
                        options = SegmentedButtonsOptions.homeOptions,
                        selectedOption = currentHomeSegment,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        appsViewModel.switchSegmentedHomeApps(it)
                    }
                }
                when {
                    loading -> {
                        item {
                            LoadingIndicator(modifier = Modifier.fillMaxSize())
                        }
                    }

                    apps.isEmpty() && !loading -> {
                        item {
                            EmptyState(
                                modifier = Modifier.fillParentMaxSize(),
                                query = query
                            )
                        }
                    }

                    else -> {
                        items(
                            items = apps.filterNotNull(),
                            key = { it.packageName }
                        ) { app ->

                            AppItem(
                                app = app,
                                onAppClick = { onAppClick(app) },
                                appsViewModel = appsViewModel,
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val isFocused = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                stringResource(R.string.search),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier
            .heightIn(min = 56.dp)
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
            }
            .focusRequester(focusRequester),
        singleLine = true,
        shape = RoundedCornerShape(0),
    )
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularWavyProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    query: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Apps,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (query.isNotEmpty()) stringResource(R.string.no_results_found)
            else stringResource(R.string.no_apps_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (query.isNotEmpty()) {
            Text(
                text = stringResource(R.string.try_different_search),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    app: App,
    appsViewModel: AppsViewModel,
    onAppClick: () -> Unit,
) {
    val limits by appsViewModel.limits.collectAsStateWithLifecycle()
    val isTrackable by remember { mutableStateOf(appsViewModel.isAppTrackable(app.packageName)) }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Card(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .heightIn(min = 72.dp),
        onClick = onAppClick,
        colors = CardDefaults.cardColors(
            containerColor = if (pressed) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Icon with better error handling
            AppIcon(
                app = app,
                viewModel = appsViewModel,
                modifier = Modifier.size(48.dp)
            )

            // App Info Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // App Name with package name (optional)
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Optional: Package name (commented out by default)
                /*
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                */

                // Usage time with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatDuration(app.usageTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Status indicators (tracking + limited)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isTrackable) {
                    Icon(
                        imageVector = Icons.Filled.TrackChanges,
                        contentDescription = stringResource(R.string.tracking_enabled),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (limits.limits.any { it?.packageName == app.packageName }) {
                    Badge(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.limited).uppercase(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIcon(
    app: App,
    viewModel: AppsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context).data(
            try {
                app.icon ?: run {
                    val icon = context.packageManager.getApplicationIcon(app.packageName)
                    viewModel.updateAppData(app.copy(icon = icon))
                    icon
                }
            } catch (e: Exception) {
                null
            }
        )
            .crossfade(true)
            .build(),
        contentDescription = app.name,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .shimmer()
            )
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clip(MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}