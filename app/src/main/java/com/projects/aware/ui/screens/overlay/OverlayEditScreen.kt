package com.projects.aware.ui.screens.overlay

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projects.aware.R
import com.projects.aware.main.isAccessibilityServiceEnabled
import com.projects.aware.ui.ViewModelsProvider
import com.projects.aware.ui.components.ColorPickDialog
import com.projects.aware.ui.screens.onboarding.PermissionCard
import kotlin.math.roundToInt


@Composable
fun OverlaySettingsScreen(
    viewModel: OverlaySettingsViewModel
) {
    val settings by viewModel.overlaySettings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val cornerRadius by animateIntAsState(
        settings.cornerRadius
    )
    Scaffold(
        bottomBar = {
            SaveButton(viewModel, context)
        },
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                color = settings.background.getAdjustedContrast(),
                tonalElevation = 3.dp,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title with better contrast handling
                    Text(
                        text = stringResource(R.string.bubble_preview),
                        style = MaterialTheme.typography.headlineSmall,
                        color = settings.background.getAdjustedContrast()
                            .getAdjustedContrast(), // New contrast function
                        fontWeight = FontWeight.Medium
                    )

                    // Preview bubble with enhanced styling
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(
                                settings.background,
                                // Subtle shadow for depth
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(cornerRadius)
                            )
                            .padding(10.dp)
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
                            verticalAlignment = Alignment.CenterVertically
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
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            EnhancedCustomizationSection(viewModel)
        }
    }
}


@Composable
fun EnhancedCustomizationSection(viewModel: OverlaySettingsViewModel) {
    val context = LocalContext.current
    var overlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var showOverlayPermissionDialog by remember { mutableStateOf(false) }
    var accessibilityPermission by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    val settings by viewModel.overlaySettings.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        overlayPermission = Settings.canDrawOverlays(context)
        accessibilityPermission = isAccessibilityServiceEnabled(context)
        if (overlayPermission && accessibilityPermission) {
            showOverlayPermissionDialog = false
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            Text(
                text = stringResource(R.string.customize_panel),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Settings Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Color Settings
                SettingsCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.colors)
                ) {
                    ColorSettingItem(
                        label = stringResource(R.string.bg_color),
                        currentColor = settings.background,
                        onColorSelected = { viewModel.updateBackgroundColor(it) }
                    )
                    ColorSettingItem(
                        label = stringResource(R.string.text_color),
                        currentColor = settings.textColor,
                        onColorSelected = { viewModel.updateTextColor(it) }
                    )
                }

                // Overlay Control
                SettingsCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.bubble_control)
                ) {
                    OverlayToggleButton(
                        isActive = settings.isBubbleVisible,
                        onToggle = { active ->
                            if (overlayPermission && accessibilityPermission) {
                                viewModel.updateBubbleVisibility(active)
                                val intent = Intent("com.aware.actions.OverlayVisibility").apply {
                                    putExtra("is_visible", active)
                                }
                                context.sendBroadcast(intent)
                            } else {
                                showOverlayPermissionDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Corner Radius Slider
        item {
            SettingsCard(title = stringResource(R.string.appearance)) {
                SliderSetting(
                    label = stringResource(R.string.corner_radius),
                    value = settings.cornerRadius.toFloat(),
                    valueRange = 0f..70f,
                    steps = 10,
                    onValueChange = { viewModel.updateCornerRadius(it) }
                )
            }
        }

        // Display Options
        item {
            SettingsCard(title = stringResource(R.string.display_options)) {
                SwitchSetting(
                    label = stringResource(R.string.show_icon),
                    checked = settings.showAppIcon,
                    icon = Icons.Default.Image
                ) { viewModel.updateShowAppIcon(it) }

                SwitchSetting(
                    label = stringResource(R.string.show_name),
                    checked = settings.showAppName,
                    icon = Icons.Default.Title
                ) { viewModel.updateShowAppName(it) }

                SwitchSetting(
                    label = stringResource(R.string.show_usage),
                    checked = settings.showAppUsage,
                    icon = Icons.Default.Timer
                ) { viewModel.updateShowAppUsage(it) }
            }
        }
    }

    if (showOverlayPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showOverlayPermissionDialog = false },
            title = { Text(stringResource(R.string.permissions_needed)) },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.permissions_onboarding))
                    HorizontalDivider()

                    PermissionCard(
                        icon = Icons.Filled.PermDeviceInformation,
                        title = stringResource(R.string.accessiblity_permission_required),
                        description = "",
                        isGranted = accessibilityPermission,
                        onClick = {
                            if (overlayPermission) {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                launcher.launch(intent)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.overlay_permission_required),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                    PermissionCard(
                        icon = Icons.Filled.Layers,
                        title = stringResource(R.string.overlay_permission_required),
                        description = "",
                        isGranted = overlayPermission,
                        onClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            launcher.launch(intent)
                        }
                    )
                }

            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showOverlayPermissionDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

// Reusable Settings Card Component
@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            content()
        }
    }
}

// Enhanced Color Picker Item
@Composable
private fun ColorSettingItem(
    label: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(currentColor, CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showDialog) {
        ColorPickerDialog(
            initialColor = currentColor,
            label = stringResource(R.string.pick_a_color),
            onColorSelected = { color ->
                onColorSelected(color)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

// Enhanced Overlay Toggle
@Composable
private fun OverlayToggleButton(
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                when {
                    pressed -> MaterialTheme.colorScheme.surfaceVariant
                    isActive -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onToggle(!isActive) }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isActive) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(if (isActive) R.string.activated else R.string.disabled),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Enhanced Switch Setting
@Composable
private fun SwitchSetting(
    label: String,
    checked: Boolean,
    icon: ImageVector? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

// Enhanced Slider Setting
@Composable
private fun SliderSetting(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = value.roundToInt().toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}


@Composable
fun SaveButton(viewModel: OverlaySettingsViewModel, context: Context) {
    val settings by viewModel.overlaySettings.collectAsStateWithLifecycle()
    FilledTonalButton(
        onClick = {
            viewModel.saveSettings()
            val intent = Intent("com.aware.overlay.settings.update")
            context.sendBroadcast(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        enabled = settings.showAppName || settings.showAppUsage || settings.showAppIcon,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30, 30, 0, 0)
    ) {
        Text(
            stringResource(R.string.apply_changes), style = MaterialTheme.typography.titleLarge,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    label: String,
    onDismiss: () -> Unit,
    initialColor: Color,
    onColorSelected: (Color) -> Unit
) {

    ColorPickDialog(
        onDismiss = onDismiss,
        label = label,
        initialColor = initialColor,
        onSave = { color ->
            onColorSelected(color)
            onDismiss()
        }
    )

}

fun Color.getAdjustedContrast(factor: Float = 0.8f): Color {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luminance > 0.5) {
        this.copy(
            red = (red * (1 - factor)).coerceIn(0f, 1f),
            green = (green * (1 - factor)).coerceIn(0f, 1f),
            blue = (blue * (1 - factor)).coerceIn(0f, 1f)
        )
    } else {
        this.copy(
            red = (red + factor).coerceIn(0f, 1f),
            green = (green + factor).coerceIn(0f, 1f),
            blue = (blue + factor).coerceIn(0f, 1f)
        )
    }
}
