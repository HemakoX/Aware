package com.projects.aware.ui.components.segmentedButtons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewCompactAlt
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AppShortcut
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.ViewCompactAlt
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.projects.aware.R

enum class HomeSegments {
    All, Used, Limited
}

object SegmentedButtonsOptions {
    val homeOptions = listOf(
        SegmentedButtonProp(
            label = R.string.all_apps,
            selectedIcon = Icons.Filled.Apps,
            unselectedIcon = Icons.Outlined.Apps,
            storeName = HomeSegments.All.name
        ),
        SegmentedButtonProp(
            label = R.string.used_today,
            selectedIcon = Icons.Filled.ViewCompactAlt,
            unselectedIcon = Icons.Outlined.ViewCompactAlt,
            storeName = HomeSegments.Used.name
        ),
        SegmentedButtonProp(
            label = R.string.limited_apps,
            selectedIcon = Icons.Filled.LockClock,
            unselectedIcon = Icons.Outlined.LockClock,
            storeName = HomeSegments.Limited.name
        )
    )
}