@file:OptIn(ExperimentalMaterial3Api::class)

package com.projects.aware.ui.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SyncLock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.SyncLock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.projects.aware.R
import com.projects.aware.data.repo.Language
import com.projects.aware.main.settings.AppTheme
import com.projects.aware.main.settings.SettingsViewModel
import com.projects.aware.main.settings.restartApp
import com.projects.aware.main.settings.toColorScheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    back: () -> Unit
) {
    BackHandler { back() }
    val context = LocalContext.current
    val settings by settingsViewModel.awareSettings.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.displaySmall
                    )
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = back
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "navigate back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp), // Add horizontal padding
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Add spacing between items
        ) {
            item {
                SetPasswordCard(
                    viewModel = settingsViewModel,
                )
            }
            item {
                LanguageSettings(
                    onLanguageChange = {
                        settingsViewModel.updateLanguage(it)
                        restartApp(context = context)
                    },
                    currentLanguage = settings.language
                )
            }
            item {
                ThemeSettings(
                    onThemeChange = {
                        settingsViewModel.updateTheme(it)
                    },
                    currentTheme = settings.theme!!
                )
            }
            item {
                ExpandableFeedbackCard(
                    settingsViewModel = settingsViewModel
                )
            }
            item {
                DisableAwareButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp), // Vertical padding for the button
                ) {
                    settingsViewModel.setDisabilityState(true)
                    val intent = Intent("com.aware.activity.settings.disability").apply {
                        putExtra("is_disabled", true)
                    }
                    context.sendBroadcast(intent)
                }
            }
        }

    }
}


@Composable
fun SetPasswordCard(modifier: Modifier = Modifier, viewModel: SettingsViewModel) {
    val settings by viewModel.awareSettings.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PrivacyTip,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.privacy_and_security),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Password card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                AnimatedContent(
                    targetState = viewModel.isPinSet(),
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) }
                ) { isPasswordSet ->
                    if (!isPasswordSet) {
                        SettingsItem(
                            icon = Icons.Outlined.Lock,
                            title = stringResource(R.string.set_PIN),
                            subtitle = stringResource(R.string.add_extra_security_to_your_app),
                            onClick = { viewModel.setNewDialogType(PasswordDialogType.SET) },
                            containerColor = MaterialTheme.colorScheme.surface,
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Column {
                            SettingsItem(
                                icon = Icons.Outlined.SyncLock,
                                title = stringResource(R.string.change_PIN),
                                subtitle = stringResource(R.string.update_your_current_password),
                                onClick = { viewModel.setNewDialogType(PasswordDialogType.CHANGE) }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            SettingsItem(
                                icon = Icons.Outlined.LockOpen,
                                title = stringResource(R.string.remove_PIN),
                                subtitle = stringResource(R.string.disable_password_protection),
                                onClick = { viewModel.setNewDialogType(PasswordDialogType.REMOVE) },
                                contentColor = MaterialTheme.colorScheme.error,
                                iconTint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
    DialogsManager(
        currentDialogType = settings.passwordSettings.dialogType,
        viewModel = viewModel,
        onDismiss = { viewModel.setNewDialogType(PasswordDialogType.NONE) }
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = contentColor
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = containerColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Action",
                tint = contentColor.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LanguageSettings(
    modifier: Modifier = Modifier,
    currentLanguage: String,
    onLanguageChange: (Language) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalDivider(thickness = 1.dp)
        LazyRow(
            modifier = modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(Language.entries, key = { it }) { lang ->
                LanguageCard(
                    language = lang,
                    selected = lang.value == currentLanguage,
                    onLanguageChange = {
                        onLanguageChange(lang)
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    language: Language,
    onLanguageChange: () -> Unit
) {
    Button(
        onClick = {
            if (!selected) {
                onLanguageChange()
            }
        },
        modifier = modifier,
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface) else null,
        shape = RoundedCornerShape(20),
    ) {
        Text(text = language.name)
        AnimatedVisibility(selected) {
            Icon(
                Icons.Filled.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface
            )
        }
    }
}


@Composable
fun ThemeSettings(
    modifier: Modifier = Modifier,
    onThemeChange: (AppTheme) -> Unit,
    currentTheme: AppTheme
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            text = stringResource(R.string.theme),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalDivider(thickness = 1.dp)
        HorizontalThemePreviewRow(
            themes = AppTheme.entries.map { mapOf(it to it.toColorScheme()) },
            selectedIndex = AppTheme.entries.indexOf(currentTheme),
            onThemeSelected = { index -> onThemeChange(AppTheme.entries[index]) }
        )
    }
}

@Composable
fun HorizontalThemePreviewRow(
    themes: List<Map<AppTheme, ColorScheme>>,
    selectedIndex: Int,
    onThemeSelected: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        itemsIndexed(themes) { index, theme ->
            ThemePreviewCard(
                theme = theme.keys.first(),
                colorScheme = theme.values.first(),
                isSelected = index == selectedIndex,
                onClick = { onThemeSelected(index) }
            )
        }
    }
}


@Composable
fun ThemePreviewCard(
    theme: AppTheme,
    colorScheme: ColorScheme,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) colorScheme.primary else Color.Transparent

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .width(120.dp)
            .height(100.dp)
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Color palette preview row
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        colorScheme.primary,
                        colorScheme.secondary,
                        colorScheme.tertiary,
                        colorScheme.surface
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    0.5.dp,
                                    if (color.luminance() > 0.5f) Color.Black else Color.White,
                                    CircleShape
                                )
                        )
                    }
                }

                // Theme name
                Text(
                    text = themeName(theme),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Selected",
                tint = colorScheme.onPrimary,
                modifier = Modifier
                    .padding(4.dp)
                    .size(14.dp)
                    .background(colorScheme.primary, CircleShape)
                    .padding(2.dp)
            )
        }
    }
}

fun themeName(theme: AppTheme): String {
    return theme.name.split("_")
        .joinToString(" ") { it.lowercase().replaceFirstChar { it.uppercase() } }
}


@Composable
fun DisableAwareButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.disable_aware),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun ExpandableFeedbackCard(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Always visible title
            Text(
                text = stringResource(R.string.help_us_improve) + " Aware",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            AnimatedContent(isExpanded) { expanded ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!expanded) {
                        Button(
                            onClick = { isExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(stringResource(R.string.send_your_feedback))
                            // Collapsed state
                        }
                    } else {
                        TextField(
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            label = {
                                Text(stringResource(R.string.name))
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                textDirection = TextDirection.ContentOrRtl
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            supportingText = {
                                Text(stringResource(R.string.optional))
                            }
                        )
                        // Expanded state
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            placeholder = {
                                Text(stringResource(R.string.describe_your_experience))
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.medium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    isExpanded = false
                                    feedbackText = ""
                                    focusManager.clearFocus()
                                }
                            ) {
                                Text(stringResource(R.string.cancel))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    scope.launch {

                                        // Simulate network request
                                        settingsViewModel.sendFeedback(
                                            message = feedbackText,
                                            name = name,
                                            onSuccess = {
                                                feedbackText = ""
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.thanks_for_your_feedback),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onError = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.please_check_your_internet_connection),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                        isExpanded = false
                                        feedbackText = ""
                                        focusManager.clearFocus()
                                    }
                                },
                                enabled = feedbackText.isNotBlank()
                            ) {
                                Text(stringResource(R.string.send))
                            }
                        }
                    }
                }
            }
        }
    }
}

